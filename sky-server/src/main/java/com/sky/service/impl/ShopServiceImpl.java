package com.sky.service.impl;

import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate redisTemplate;
    // 获取店铺的营业状态
    @Override
    public Integer getShopStatus() {
        // 获取状态
       Integer status =  (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        if (status != null) {
            // 获取不为空的状态
            return status;
        } else {
            // 为空就设置
            redisTemplate.opsForValue().set("SHOP_STATUS",1);
            return  1;
        }
    }

    // 设置营业状态
    @Override
    public void setShopStatus(Integer status) {
        redisTemplate.opsForValue().set("SHOP_STATUS",status);
    }
}
