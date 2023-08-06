package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {
    /**
     * 根据菜品添加分类
     * @param category
     */
    void isCuisineCategory(Category category);
}
