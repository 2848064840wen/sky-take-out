package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    // 查寻购物车
    // 查询所有
    List<ShoppingCart> selectAll(Long userId);


    // 清空购物车
    void deleteAll(Long userId);

    // 删除一条数据
    void delete(ShoppingCartDTO shoppingCartDTO);
}
