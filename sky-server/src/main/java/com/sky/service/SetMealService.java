package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    /**
     * 新增套餐
     * @return
     */
    void addSetMeal(SetmealDTO setmealDTO);



    /**
     * 分页查询
     * @return
     */
    PageResult setMealPageQueryDTO(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getByIdSetMeal(Long id);

    /**
     * 修改起售、停售
     * @param status
     * @param id
     */
    void update(Integer status, Long id);

    /**
     * 修改套装
     * @param setmealVO
     */
    void put(SetmealVO setmealVO);

    /**
     * 删除套餐
     * @param ids
     */
    void delete(List<Long> ids);
}
