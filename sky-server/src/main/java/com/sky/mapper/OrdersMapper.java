package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {
    // 添加数据
    void instr(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    // 分页查询
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    // 根据id查询
    @Select("select * from orders where id = #{id} and user_id = #{userId}")
    Orders getOrders(Long id,Long userId);

    @Select("select * from orders where status = #{status}")
    Integer byStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> manageStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    @Select("select SUM(amount) from orders where order_time > #{beginTime} and order_time < #{endTime} and status = #{status}")
    Double turnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer status);


    Integer orderCountList(LocalDateTime begin,LocalDateTime end ,Integer status);

    List<GoodsSalesDTO>  getTop10Order(LocalDateTime beginTime, LocalDateTime endTime);
}
