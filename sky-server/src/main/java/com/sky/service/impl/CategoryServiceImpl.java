package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    // 菜品
    @Autowired
    DishMapper dishMapper;

    // 套餐
    @Autowired
    SetMealMapper setMealMapper;

    /**
     * 分类的分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageSelect(CategoryPageQueryDTO categoryPageQueryDTO) {

        // 开始分页
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        // 分页查询
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = page.getTotal();

        List<Category> result = page.getResult();

        return new PageResult(total, result);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    @Override
    public void newAndCategory(CategoryDTO categoryDTO) {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO, category);

        // 分类状态
        category.setStatus(0);

        // 更改创建时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        // 创建人和修改人
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        // 根据菜品分类
        if (category.getType() == 1) {
            dishMapper.isCuisineCategory(category);
        } else {
            // 根据套餐分类
            setMealMapper.isSetMealCategory(category);
        }

        log.info("分类:    {}", category);
    }


    /**
     * 启用 和 禁用 分类
     */
    @Override
    public void updateCategory(Integer status, Long id) {
        Category ca = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        categoryMapper.updateCategory(ca);


        log.info("Category 修改 :    {}",ca);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void updateIsCategory(CategoryDTO categoryDTO) {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO,category);

        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.updateCategory(category);

        log.info("修改分类:  {} ",category );
    }

    /**
     * 删除分类
     * @param id
     */
    @Override
    public void deleteCategory(Integer id) {
        categoryMapper.deleteCategory(id);
    }
}
