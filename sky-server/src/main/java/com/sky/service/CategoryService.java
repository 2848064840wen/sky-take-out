package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageSelect(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void newAndCategory(CategoryDTO categoryDTO);

    /**
     * 启用 和 禁用套餐
     * @param status
     * @param id
     */
    void updateCategory(Integer status, Long id);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void updateIsCategory(CategoryDTO categoryDTO);

    /**
     * 删除分类
     * @param id
     */
    void deleteCategory(Integer id);

    /**
     * 根据id查询分类
     * @param type
     * @return
     */
    List<Category> getById(Integer type);
}
