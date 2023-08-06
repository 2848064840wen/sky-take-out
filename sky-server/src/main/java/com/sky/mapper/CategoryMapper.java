package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
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
    @AutoFill(value = OperationType.UPDATE)
    void updateCategory(Category ca);

    /**
     * 删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id};")
    void deleteCategory(Integer id);
}
