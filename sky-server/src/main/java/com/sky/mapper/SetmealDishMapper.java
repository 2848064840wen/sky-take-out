package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐id
     * @param ids
     * @return
     */
    List<Long> selectByDishId(List<Long> ids);

    /**
     * 添加菜品与套餐的关系
     * @param setmealDish
     */
    void addSetmealDish(SetmealDish setmealDish);

    /**
     * 根据setmealID查询
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getByIdSetMealDish(Long setmealId);


    //删除
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId} ")
    void delete(SetmealDish setmealDish);
}
