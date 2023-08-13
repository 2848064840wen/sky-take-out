package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrdersService {
    // 用户下单
    OrderSubmitVO placeAnOrder(OrdersSubmitDTO ordersDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     * 分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery(Integer page, Integer pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 再来一单
     * @param id
     */
    void oneMoreOrder(Long id);



    //------------------------------

    // 服务端

    /**
     * 搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult searchOrder(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 获取哥各个订单的状态
     * @return
     */
    OrderStatisticsVO getStatusOrderNumber();

    /**
     * 查询订单详情
     * @return
     */
    OrderVO selectOrderDetails(Long id);

    /**
     * 接单
     * @return
     */
    void orderReceiving(Long id);

    // 拒单
    void rejectAnOrder(OrdersRejectionDTO ordersRejectionDTO);

    // 取消订单
    void cancellationOfOrder(OrdersCancelDTO ordersCancelDTO);

    // 派送订单
    void dispatchOrder(Long id);

    void completeAnOrder(Long id);

    // 催单
    void reminderOrder(Long id);

}
