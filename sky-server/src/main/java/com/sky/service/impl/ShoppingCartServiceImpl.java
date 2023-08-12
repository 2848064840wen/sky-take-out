package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setMealMapper;


    // 添加购物车
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 获取id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setId(userId);
        shoppingCart.setUserId(userId);

        // 判断当前加入购物车的商品是否已经存在
        List<ShoppingCart> list = shoppingCartMapper.getCommodity(shoppingCart);

        if (list != null && list.size() > 0) {
            // 存在，数量加一 update操作
            ShoppingCart ca = list.get(0);
            ca.setNumber(ca.getNumber() + 1);
            ca.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.update(ca);
        }
        // 不存在，进行插入操作 inter


        // 判断是套餐还是菜品
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long dishId = shoppingCartDTO.getDishId();
        if (dishId != null) {
            // 本次添加的是菜品
            Dish dish = dishMapper.getById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setImage(dish.getImage());

        } else {
            // 添加的是套餐
            SetmealVO setmealVO = setMealMapper.getByIdSetMeal(setmealId);
            shoppingCart.setName(setmealVO.getName());
            shoppingCart.setAmount(setmealVO.getPrice());
            shoppingCart.setImage(setmealVO.getImage());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());


        shoppingCartMapper.inster(shoppingCart);
        log.info("添加购物车  :  {}", shoppingCart);
    }


    // 查看购物车
    @Override
    public List<ShoppingCart> selectAll(Long userId) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();

        return shoppingCartMapper.getCommodity(shoppingCart);
    }


    // 清空购物车
    @Override
    public void deleteAll(Long userId) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        shoppingCartMapper.delete(shoppingCart);
    }


    // 删除一条数据

    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        List<ShoppingCart> list = shoppingCartMapper.getCommodity(shoppingCart);
        // 判断是否存在
        if (list != null && list.size() > 0) {
            // 如果数量是多个就减少1
            ShoppingCart cart = list.get(0);
            Integer number = cart.getNumber();
            cart.setNumber(number - 1);
            if (number > 1) {
                // 修改数量
                shoppingCartMapper.update(cart);
            } else {

                // 如果数量不是多个就直接删除
                shoppingCartMapper.delete(shoppingCart);
            }
        }

        log.info("删除一条数据 :  {}", shoppingCart);
    }
}
