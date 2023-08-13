package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;


    // 用户下单
    @Override
    @Transactional
    public OrderSubmitVO placeAnOrder(OrdersSubmitDTO ordersDTO) {

        // 处理异常 ， 购物车为空   地址溥为空
        AddressBook addressBook = addressBookMapper.getById(ordersDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId).build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getCommodity(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


        // 向订单表添加 一 条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());

        ordersMapper.instr(orders);

        // 向订单明细表添加 N 条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.inster(orderDetailList);

        // 清空购物车
        shoppingCartMapper.delete(shoppingCart);
        // 封装返回 VO对象
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getCancelTime())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = WeChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);
    }

    /**
     * 查询历史订单
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery(Integer page, Integer pageSize, Integer status) {

        // 分页查询
        PageHelper.startPage(page, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> pageOrders = ordersMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();

        if (pageOrders != null && pageOrders.isEmpty()) {
            for (Orders pageOrder : pageOrders) {
                // 获取订单id
                Long id = pageOrder.getId();

                // 查询订单详情

                List<OrderDetail> list = orderDetailMapper.select(id);

                // 封装vo对象
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(pageOrder, orderVO);
                orderVO.setOrderDetailList(list);

                orderVOList.add(orderVO);
            }


        }


        return new PageResult(pageOrders.getTotal(), orderVOList);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {

        // 查询订单详情
        // 查询订单
        Orders orders = ordersMapper.getOrders(id, BaseContext.getCurrentId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.select(orders.getId());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);


        log.info("查询订单详情 {}  ", orderVO);
        return orderVO;
    }


    /**
     * 取消订单
     *
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {

        // 获取订单
        Orders orders = ordersMapper.getOrders(id, BaseContext.getCurrentId());
        // 判断获取到的订单是否为null
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //- 待支付和待接单状态下，用户可直接取消订单
        //- 商家已接单状态下，用户取消订单需电话沟通商家
        //- 派送中状态下，用户取消订单需电话沟通商家

        if (orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.PLEASE_CONTACT_THE_MERCHANT);
        }

        // 订单处于待接单状态下取消，需要进行退款
        if (orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            // 调用微信支付退款接口
            try {
                weChatPayUtil.refund(
                        orders.getNumber(),
                        orders.getNumber(),
                        orders.getAmount(),
                        orders.getAmount()
                );

                // 修改状态为退款
                orders.setPayStatus(Orders.REFUND);
            } catch (Exception e) {
                throw new OrderBusinessException(MessageConstant.REFUND_FAILURE);
            }

        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());

        ordersMapper.update(orders);

        log.info("取消订单 {}", orders);
    }


    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void oneMoreOrder(Long id) {
        // 在来一单就是把订单中的商品，添加到购物车中

        // 根据订单id获取订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.select(id);
        // 既然在来一单，获取到的订单肯定不为空

        Long userId = BaseContext.getCurrentId();
        orderDetails.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);

            // 重新添加商品
            shoppingCartMapper.inster(shoppingCart);
        });


    }


//    -------------------------
    // 服务端

    /**
     * 搜索订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult searchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {

        // 获取用户id
        Long userId = BaseContext.getCurrentId();

        // 分页查询订单表
        // 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        // 返回结果
        Page<Orders> page = ordersMapper.pageQuery(ordersPageQueryDTO);

        List<Orders> ordersList = page.getResult();

        List<OrderVO> orderVOList = new ArrayList<>();

        ordersList.forEach(orders -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            List<OrderDetail> orderDetails = orderDetailMapper.select(orders.getId());
            List<String> stringList = new ArrayList<>();
            orderDetails.forEach(orderDetail -> {
                stringList.add(orderDetail.getName() + " * " + orderDetail.getNumber() + " ;");
            });

            // 封装菜品信息
            orderVO.setOrderDishes(String.join("", stringList));

            orderVOList.add(orderVO);
        });


        return new PageResult(page.getTotal(), orderVOList);
    }


    /**
     * 获取各个订单的状态
     *
     * @return
     */
    @Override
    public OrderStatisticsVO getStatusOrderNumber() {

        Integer pendingPayment = ordersMapper.byStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = ordersMapper.byStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = ordersMapper.byStatus(Orders.DELIVERY_IN_PROGRESS);


        return new OrderStatisticsVO(pendingPayment,confirmed,deliveryInProgress);
    }


    /**
     * 查询订单详情
     * @return
     */
    @Override
    public OrderVO selectOrderDetails(Long id) {

        // 获取菜单
        Orders orders = ordersMapper.getOrders(id, BaseContext.getCurrentId());

        // 数据拷贝
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);

        // 获取订单详情
        orderVO.setOrderDetailList(orderDetailMapper.select(orders.getId()));
        return orderVO;
    }


    /**
     * 接单
     * @return
     */
    @Override
    public void orderReceiving(Long id) {
        // 将订单的状态修改为已经单
        Orders orders = new Orders();

        orders.setId(id);
        orders.setStatus(Orders.CONFIRMED);

        ordersMapper.update(orders);

    }


    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejectAnOrder(OrdersRejectionDTO ordersRejectionDTO) {
        // 获取订单
        Orders orders = ordersMapper.getOrders(ordersRejectionDTO.getId(), BaseContext.getCurrentId());
        if(!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_CANCELLATION_FAILED);
        }

        Integer payStatus = orders.getPayStatus();
        if(payStatus == Orders.PAID){
                //用户已支付，需要退款
            try {
                String refund = weChatPayUtil.refund(
                        orders.getNumber(),
                        orders.getNumber(),
                        new BigDecimal(0.01),
                        new BigDecimal(0.01));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());


        ordersMapper.update(orders);
    }


    // 取消订单
    @Override
    public void cancellationOfOrder(OrdersCancelDTO ordersCancelDTO) {

        Orders orders = ordersMapper.getOrders(ordersCancelDTO.getId(), BaseContext.getCurrentId());

        Integer status = orders.getStatus();

        if(status == Orders.PAID){
            //用户已支付，需要退款
            try {
                String refund = weChatPayUtil.refund(
                        orders.getNumber(),
                        orders.getNumber(),
                        new BigDecimal(0.01),
                        new BigDecimal(0.01));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        ordersMapper.update(orders);
    }

    // 派送订单
    @Override
    public void dispatchOrder(Long id) {
        Orders orders = ordersMapper.getOrders(id, BaseContext.getCurrentId());

        Integer status = orders.getStatus();
        if(status == Orders.TO_BE_CONFIRMED){
            orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
            orders.setCancelTime(LocalDateTime.now());
            ordersMapper.update(orders);
        }
    }


    @Override
    public void completeAnOrder(Long id) {
        Orders orders = ordersMapper.getOrders(id, BaseContext.getCurrentId());

        Integer status = orders.getStatus();
        if(status == Orders.DELIVERY_IN_PROGRESS){
            orders.setStatus(Orders.COMPLETED);
            orders.setCancelTime(LocalDateTime.now());
            ordersMapper.update(orders);
        }
    }
}



