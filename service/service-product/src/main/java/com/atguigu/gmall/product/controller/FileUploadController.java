package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("admin/product/")
public class FileUploadController {

    //回显数据
    @Value("${fileServer.url}")
    private String fileUrl;

    //利用springMVC文件上传技术
    /*
    * 文件上传的时候注意点：
    *       1：规定大小，格式
    *       2：文件名不能重复
    *       3：文件的后缀名与原有文件后缀名要保持一致！
    * */
    @RequestMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws Exception{
        //获取到上传文件的路径
        //先读取到配置文件tracker.conf tracker-server 的ip地址
        String configFile = this.getClass().getResource("/tracker.conf").getFile();
        String path = null;

        if (configFile!=null){
            // 初始化
            ClientGlobal.init(configFile);
            // 创建trackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            // 获取trackerService
            TrackerServer trackerServer = trackerClient.getConnection();
            // 创建storageClient1
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);
            path = storageClient1.upload_appender_file1(file.getBytes(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
            System.out.println(fileUrl + path);
        }
        return Result.ok(fileUrl+path);
    }

}
