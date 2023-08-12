package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    // 查询
    List<ShoppingCart> getCommodity(ShoppingCart shoppingCart);

    // 修改
    void update(ShoppingCart shoppingCart);

    // 添加
    void inster(ShoppingCart shoppingCart);



    // 条件删除
    void delete(ShoppingCart shoppingCart);

}
