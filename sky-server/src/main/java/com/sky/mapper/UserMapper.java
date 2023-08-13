package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 根据openid获取用户
    @Select("select * from  user where openid = #{openid} ")
    User getByOpenidUser(String openid);

    // 添加用户
    void addUser(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
