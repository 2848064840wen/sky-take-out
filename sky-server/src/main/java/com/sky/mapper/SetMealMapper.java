package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {

    /**
     * 根据套餐分类
     * @param category
     */
    @AutoFill(OperationType.INSERT)
    void isSetMealCategory(Category category);

    /**
     * 新增套餐
     * @return
     */
    @AutoFill(OperationType.INSERT)
    void addSetMeal(Setmeal setmeal);


    /**
     * 分页查询
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    SetmealVO getByIdSetMeal(Long id);

    /**
     * 修改起售、停售
     * @param status
     * @param id
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Delete("delete setmeal ,setmeal_dish from setmeal,setmeal_dish where setmeal_dish.setmeal_id = #{id}  and setmeal.id = #{id};")
    void delete(Long id);
}
