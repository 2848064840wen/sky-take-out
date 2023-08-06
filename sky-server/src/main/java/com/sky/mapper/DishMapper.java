package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {
    /**
     * 根据菜品添加分类
     * @param category
     */
    @AutoFill(value = OperationType.INSERT)
    void isCuisineCategory(Category category);
}
