package com.pinyougou.shop.controller;

import com.pinyougou.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author W
 * @version 1.0
 * @description com.pinyougou.shop.controller
 * @date 2018/6/23
 * 文件上传
 */
@RestController
public class UploadController {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;//文件服务器地址

    @RequestMapping("/upload")
    public Result upload(MultipartFile file) {
        //1.取文件的扩展名
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        try {
            //创建FastDFS
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:/config/fdfs_client.conf");
            //执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //拼接返回的url和ip地址 拼接成完整的url
            String url=IMAGE_SERVER_URL+path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }

}
