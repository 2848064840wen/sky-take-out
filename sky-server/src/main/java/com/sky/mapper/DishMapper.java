package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    /**
     * 根据菜品添加分类
     * @param category
     */
    @AutoFill(value = OperationType.INSERT)
    void isCuisineCategory(Category category);

    /**
     * 菜品管理 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    // 添加菜品
    @AutoFill(OperationType.INSERT)
    void andDish(Dish dish);


    /**
     * 删除菜品
     * @param id
     */
    void deleteAllIsOneDish(Long id);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    /**
     * 修改dish
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void putByIdDish(Dish dish);
}
