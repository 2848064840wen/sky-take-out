package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "套餐管理")
@RestController
@RequestMapping("/admin/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    /**
     * 新增套餐
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result addSetMeal(@RequestBody SetmealDTO setmealDTO){

        log.info("套餐管理 --- >  新增套餐 :   {}",setmealDTO);
        setMealService.addSetMeal(setmealDTO);

        return Result.success();
    }


    /**
     * 分页查询
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> setMealPageQueryDTO(SetmealPageQueryDTO setmealPageQueryDTO){

       PageResult pageResult =  setMealService.setMealPageQueryDTO(setmealPageQueryDTO);

        return Result.success(pageResult);
    }


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getByIdSetMeal(@PathVariable Long id){

        return Result.success(setMealService.getByIdSetMeal(id));
    }

    /**
     * 套餐的起售、停售
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐的起售、停售")
    public Result setStartStop(@PathVariable Integer status, Long id){

        setMealService.update(status,id);

        return Result.success();
    }


    /**
     * 修改套餐
     */

    @PutMapping
    @ApiOperation("修改套餐")
    public Result put(@RequestBody SetmealVO setmealVO){

        setMealService.put(setmealVO);

        return Result.success();
    }


    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result delete(@RequestParam List<Long> ids){

        setMealService.delete(ids);

        return Result.success();
    }
}
