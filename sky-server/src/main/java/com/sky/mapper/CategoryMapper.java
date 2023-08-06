package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CategoryMapper {
    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改 分类
     * @param ca
     */
    void updateCategory(Category ca);

    /**
     * 删除分类
     * @param id
     */
    @Update("delete from category where id = #{id};")
    void deleteCategory(Integer id);
}
