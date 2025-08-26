package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/common")
@ApiOperation("通用接口")
public class CommonController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传： {}", file);
        //获得原始文件的扩展名(.png .jpg)
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //用UUID随机生成新的文件名,并拼接上原始文件的扩展名(.png .jpg)
        String newFileName = UUID.randomUUID().toString() + extension;
        //保存文件到C盘，并用UUID命名, 如e216ab8c-2f76-4a55-95c3-1a0f177aad75.jpg
        file.transferTo(new File("C:/images/" + newFileName ));
        return Result.success();
    }

}
