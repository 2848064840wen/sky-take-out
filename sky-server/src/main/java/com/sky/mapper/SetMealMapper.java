package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealMapper {

    /**
     * 根据套餐分类
     * @param category
     */
    void isSetMealCategory(Category category);

}
