package com.sky.controller.admin;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrdersController")
@RequestMapping("/admin/order")
@Api(tags = "商家订单接口")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;


    /**
     * 搜索订单
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("搜索订单")
    public Result<PageResult> searchOrder(OrdersPageQueryDTO ordersPageQueryDTO){

        PageResult pageResult = ordersService.searchOrder(ordersPageQueryDTO);

        return null;
    }


    /**
     * 获取各个订单的状态
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("获取各个订单的状态")
    public Result<OrderStatisticsVO> getStatusOrderNumber(){

        OrderStatisticsVO orderStatisticsVO = ordersService.getStatusOrderNumber();

        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> selectOrderDetails(@PathVariable Long id){

        OrderVO orderVO = ordersService.selectOrderDetails(id);
        return Result.success(orderVO);
    }


    /**
     * 接单
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result orderReceiving(@RequestBody Long id){

        ordersService.orderReceiving(id);
        return  Result.success();
    }


    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectAnOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){

        ordersService.rejectAnOrder(ordersRejectionDTO);
        return Result.success();
    }


    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancellationOfOrder(@RequestBody  OrdersCancelDTO ordersCancelDTO){

        ordersService.cancellationOfOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result dispatchOrder(@PathVariable Long id){

        ordersService.dispatchOrder(id);

        return Result.success();

    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result completeAnOrder(@PathVariable Long id){

        ordersService.completeAnOrder(id);

        return Result.success();

    }
}
