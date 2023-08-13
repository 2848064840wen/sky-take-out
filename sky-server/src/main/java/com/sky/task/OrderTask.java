package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 */

@Component
@Slf4j
public class OrderTask {


    @Autowired
    private OrdersMapper ordersMapper;


    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ") // 每分钟
    public void manageOvertimeOrder() {
        log.info("处理超时订单 : {}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> list = ordersMapper.manageStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

        if (list != null && list.isEmpty()) {
            list.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setOrderTime(LocalDateTime.now());
                orders.setCancelReason("订单超时");

                ordersMapper.update(orders);
            });

        }
    }

    /**
     * 处理已完成的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") // 凌晨1点处理
    public void manageDoneOrder() {
        log.info("处理已完成的订单 {} ",LocalDateTime.now());

        // 查询已经完成的订单 且订单在凌晨一点一个小时前
        List<Orders> ordersList = ordersMapper.manageStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));

        if(ordersList != null && ordersList.isEmpty()){
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                ordersMapper.update(orders);
            });
        }

    }

}
