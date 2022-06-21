package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

//        获取文件原始名称
        String originalFilename = file.getOriginalFilename();

//        截取.jpg
        String fileName = originalFilename.substring(originalFilename.lastIndexOf("."));

        fileName = fileName + UUID.randomUUID().toString();

//        创建图片路径
        File path = new File(basePath);
        if (!path.exists()) {
            path.mkdirs();
        }

//        存放图片的路径
        try {
            file.transferTo(new File(basePath + fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
//            读取文件内容


        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

//        写入到浏览器
        ServletOutputStream outputStream = response.getOutputStream();
//        设置响应方式
        response.setContentType("image/jpeg");

//            读取一行一行
        int len = 0;

        byte[] bytes = new byte[1024];

        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();
    }

}
