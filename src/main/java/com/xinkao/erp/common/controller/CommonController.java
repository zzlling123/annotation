package com.xinkao.erp.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.CommonService;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.course.utils.FileTypeChecker;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CommonService commonService;
    @Value("${path.fileUrl}")
    private String fileUrl;
    @Value("${ipurl.url}")
    private String ipurl;


    @PrimaryDataSource
    @RequestMapping(value = "/getImportByToken", method = RequestMethod.POST)
    public BaseResponse getImportByToken(String importToken) {
        JSONObject json = JSON.parseObject(redisUtil.get(importToken));
        if (json == null){
            return BaseResponse.ok("导入中......");
        }else{
            if ("fail".equals(json.getString("state"))){
                return BaseResponse.fail("导入失败！");
            }else{
                return BaseResponse.ok(json.getString("msg"));
            }
        }
    }

    @PrimaryDataSource
    @PostMapping("/getOBSInfo")
    public BaseResponse getOBSInfo() {
        return commonService.getOBSInfo();
    }


//    @PrimaryDataSource
    @PostMapping("/upload/file")
    public BaseResponse<String> uploadRequest(@RequestParam(value="file") MultipartFile file, HttpServletRequest request) {
        try {
            String saveFileName;
            File file1 = new File(fileUrl);
            if (!file1.exists()){
                file1.mkdirs();
            }
            if (file.isEmpty()) {
                throw new BusinessException("文件为空");
            }
            System.out.println("fileName:" + file.getOriginalFilename());
            saveFileName = UUID.randomUUID().toString()+file.getOriginalFilename();

            File fileNew = new File(fileUrl,saveFileName);
            if (!fileNew.getParentFile().exists()) {
                fileNew.getParentFile().mkdirs();
            }
            file.transferTo(fileNew);
            return BaseResponse.ok(ipurl+"/annotation/fileUrl/"+saveFileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("上传失败");
        }
    }

}
