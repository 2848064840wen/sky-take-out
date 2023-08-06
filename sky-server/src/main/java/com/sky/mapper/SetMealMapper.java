package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealMapper {

    /**
     * 根据套餐分类
     * @param category
     */
    @AutoFill(OperationType.INSERT)
    void isSetMealCategory(Category category);

}
