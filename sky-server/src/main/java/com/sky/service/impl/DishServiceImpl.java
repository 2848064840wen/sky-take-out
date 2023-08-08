package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        // 开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> result = page.getResult();

        log.info("查询后的数据 ：  {}", result);

        return new PageResult(total, result);
    }

    /**
     * 添加菜品
     *
     * @param dishDTO
     */

    @Transactional// 开启事务
    @Override
    public void andDish(DishDTO dishDTO) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);

        // 插入菜品
        dishMapper.andDish(dish);
        log.info("插入菜品 :   {}", dish);
        // 插入口味

        // 获取组件值
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            log.info("插入菜品的口味:   {}", flavors);

            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.andDishFlavor(flavors);
        }

    }


    /**
     * `    * 删除一条或多条菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteAllIsOneDish(List<Long> ids) {

        // 判断是否有套餐关联 ----->不能删除
        List<Long> dishIds = setmealDishMapper.selectByDishId(ids);

        log.info("是否有套餐dishId:   {}", dishIds.size());
        if (dishIds != null && dishIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        ids.forEach(id -> {
            log.info("删除的id为:   {}", id);

            Dish dish = dishMapper.getById(id);

            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                // 判断菜品是否能删除------> 起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            // 删除菜品
            dishMapper.deleteAllIsOneDish(id);

            // 删除口味
            dishFlavorMapper.deleteByDishId(id);
        });
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {

        // 查询dish基本数据
        Dish dish = dishMapper.getById(id);

        DishVO dishVO = new DishVO();

        // 查询口味表
        List<DishFlavor> listFlavor = dishFlavorMapper.getById(id);

        // 对象copy
        BeanUtils.copyProperties(dish, dishVO);

        dishVO.setFlavors(listFlavor);

        log.info("查询后数据:   {}", dishVO);
        return dishVO;
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Override
    public void putByIdDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();

        // 修改dish表
        dishMapper.putByIdDish(dish);
        // 修改dish_flavor表
        //先删除对应的口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 设置dish属性
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });

            // 在添加对应的口味
            dishFlavorMapper.andDishFlavor(flavors);
        }

        log.info("口味:    {}",flavors);
    }

    /**
     * 菜品起售，停售
     * @param status
     * @param id
     */

    @Override
    public void dishStartStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);

        dishMapper.putByIdDish(dish);
    }

    /**
     *根据菜品类型查询
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {

        return dishMapper.getByCategoryId(categoryId);
    }
}
