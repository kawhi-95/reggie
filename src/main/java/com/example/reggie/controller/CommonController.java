package com.example.reggie.controller;

import com.example.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("common")
public class CommonController {

    @Value("${reggie.images}")
    private String basePath;

    @PostMapping("upload")
    public Result<String> upload(MultipartFile file) {
        String fileName = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filePath = basePath + fileName + suffix;
        log.info("{}", filePath);
        //file.transferTo(new File(filePath));
        return  Result.success("上传图片成功");
    }
}
