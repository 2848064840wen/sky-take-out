package com.sky.controller.admin;

import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜品分类")
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 分类的分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类的分页查询,根据菜品类型")
    public Result<PageResult> pageSelect(CategoryPageQueryDTO categoryPageQueryDTO) {

        log.info("CategoryPageQueryDTO  :  {}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageSelect(categoryPageQueryDTO);


        return Result.success(pageResult);
    }


    /**
     * 新增分类
     *
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result newAndCategory(@RequestBody CategoryDTO categoryDTO) {

        log.info("categoryDTO新增分类 : {}", categoryDTO);


        categoryService.newAndCategory(categoryDTO);


        return Result.success();
    }


    /**
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用和禁用分类")
    public Result statsAndStop(@PathVariable Integer status, Long id) {

        categoryService.updateCategory(status, id);

        log.info("启用和禁用分类:   {}   {}", status, id);

        return Result.success();
    }


    /**
     * 修改分类
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {


        categoryService.updateIsCategory(categoryDTO);

        return Result.success();
    }


    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result deleteCategory(Integer id){

        categoryService.deleteCategory(id);
        log.info("删除分类:   {}",id);

        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> getById(Integer type){

        List<Category> list = categoryService.getById(type);

        return Result.success(list);
    }

}
