package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    // 营业额统计
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        // 营业额统计就 是查询订单表中指定时间的段中订单金额的总和
        List<LocalDate> dateList = getLocalDates(begin, end);

        List<Double> doubleList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // sql select COUNT(*) from order where order_time > #{beginTime} and order_time < #{endTime} and status = #{status}
            Double turnover =
                    ordersMapper.turnoverStatistics(beginTime, endTime, Orders.COMPLETED);
            turnover = turnover == null ? 0.0 : turnover;
            doubleList.add(turnover);
        });

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(doubleList, ","))
                .build();
    }


    // 用户统计
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        // dateList 数据
        List<LocalDate> dateList = getLocalDates(begin, end);


        // totalUserList 数据
        // 就是查询当天的下单总数 status为 Orders.COMPLETED
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Integer users = userMapper.getUserStatistics(beginTime, endTime);
            Integer newUsers = userMapper.getUserStatistics(null, endTime);
            totalUserList.add(users);
            newUserList.add(newUsers);
        });

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    // 订单统计
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {


        // dateList 数据
        List<LocalDate> dateList = getLocalDates(begin, end);

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            // 查询每日订单数量  select COUNT(*) from order where order_time > beginTime and order_time < endTime
            Integer orderCount = ordersMapper.orderCountList(beginTime, endTime, null);
            // 查询每日有效订单数 select COUNT(*) from order where order_time > beginTime and order_time < endTime and status = 5
            Integer validOrderCount = ordersMapper.orderCountList(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);

        }
        // 订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        // 有效订单总数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();


        Double orderCompletionRate =
                validOrderCount == 0 ? 0 : validOrderCount.doubleValue() / totalOrderCount;


        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }




    // 销量top10
    @Override
    public SalesTop10ReportVO getTop10Order(LocalDate begin, LocalDate end) {

        List<GoodsSalesDTO> list = ordersMapper.getTop10Order(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));

        List<String> nams = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nams,","))
                .numberList(StringUtils.join(numbers,","))
                .build();
    }

    // date数据
    private static List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }


}
