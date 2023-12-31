package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    /**
     * 阿里云oss配置类
     */
    @Configuration
    @Slf4j
    public class ALiYunOssConfig {
        @Bean
        public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
            log.info("创建阿里云oss文件上传对象  :{}",aliOssProperties);
            return new AliOssUtil(
                    aliOssProperties.getEndpoint(),
                    aliOssProperties.getAccessKeyId(),
                    aliOssProperties.getAccessKeySecret(),
                    aliOssProperties.getBucketName()
            );

        }
    }
