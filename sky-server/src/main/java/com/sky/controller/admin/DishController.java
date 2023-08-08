package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    /**
     * 菜品分页查询
     *
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        log.info("菜品分页查询:   {}", dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 添加菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加菜品")
    public Result andDish(@RequestBody DishDTO dishDTO) {
        log.info("添加菜品 :   {}", dishDTO);

        dishService.andDish(dishDTO);

        return Result.success();
    }


    /**
     * 删除一条或多条菜品
     *
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除一条或多条菜品")
    public Result deleteAllIsOneDish(@RequestParam List<Long> ids) {
        log.info("删除的id ：  {} ", ids);

        dishService.deleteAllIsOneDish(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getById(id);
        log.info("根据id查询菜品 查询到的数据 : {}", dishVO);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @return
     */

    @PutMapping
    @ApiOperation("修改菜品")
    public Result putByIdDish(@RequestBody DishDTO dishDTO) {

        dishService.putByIdDish(dishDTO);

        return Result.success();
    }

    /**
     * 菜品起售，停售
     */

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售，停售")
    public Result dishStartStop(@PathVariable Integer status, Long id) {

        log.info("菜品状态 : {}  , 菜品id :  {}", status, id);

        dishService.dishStartStop(status, id);

        return Result.success();
    }

    /**
     *根据菜品类型查询
     */

    @GetMapping("/list")
    @ApiOperation("根据菜品类型查询")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("菜品分类id  : {}",categoryId);

        List<Dish> list =  dishService.getByCategoryId(categoryId);

        return Result.success(list);
    }




}
