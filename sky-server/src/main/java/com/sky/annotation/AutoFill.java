package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 标识该注解只能标识在方法上
@Target(ElementType.METHOD)
// 设置保留策略 RetentionPolicy.RUNTIME在编译后的字节码中保留
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 数据库类型 insert update 两种
    OperationType value();
}
