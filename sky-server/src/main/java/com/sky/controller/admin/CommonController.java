package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传功能")
    public Result<String> fileUploading(MultipartFile file){

        log.info("文件上传 :{}" ,file);
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 截取文件名的后缀名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID创建新的文件名
        String newFileName = UUID.randomUUID() + extension;


        try {
            return  Result.success(aliOssUtil.upload(file.getBytes(),newFileName));

        } catch (IOException e) {
            log.info("文件上传失败:  {}",e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
