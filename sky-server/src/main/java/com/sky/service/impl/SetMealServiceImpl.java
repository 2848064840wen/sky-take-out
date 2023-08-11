package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增套餐
     *
     * @return
     */
    @Override
    public void addSetMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        // 拷贝对象
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 添加setmeal套餐
        setMealMapper.addSetMeal(setmeal);


        // 添加套餐包含的菜品
        List<SetmealDish> setMealDishes = setmealDTO.getSetmealDishes();

        setMealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishMapper.addSetmealDish(setmealDish);
        });
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public PageResult setMealPageQueryDTO(SetmealPageQueryDTO setmealPageQueryDTO) {


        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdSetMeal(Long id) {

        // 查询 SetmealVO
        SetmealVO setmealVO = setMealMapper.getByIdSetMeal(id);
        // 在查询 SetmealDish
        setmealVO.setSetmealDishes(setmealDishMapper.getByIdSetMealDish(setmealVO.getId()));
        return setmealVO;
    }

    /**
     * 修改起售、停售
     *
     * @param status
     * @param id
     */
    @Override
    public void update(Integer status, Long id) {

        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        setMealMapper.update(setmeal);
    }

    /**
     * 修改套餐
     */

    @Override
    public void put(SetmealVO setmealVO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO, setmeal);

        //修改setmeal表
        setMealMapper.update(setmeal);

        // 修改Setmeal_dish表
        setmealVO.getSetmealDishes().forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());

            // 先删除 后添加
            setmealDishMapper.delete(setmealDish);

            setmealDishMapper.addSetmealDish(setmealDish);
        });

        System.out.println(setmeal);
    }

    /**
     * 删除套餐
     */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
        // 起售中的套餐不能删除
        for (Long id : ids) {
            SetmealVO setmealVO = setMealMapper.getByIdSetMeal(id);
            // 如果status的值为1不能删除
            if(setmealVO.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            setMealMapper.delete(id);
        }
    }




    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }
}
