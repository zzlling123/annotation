package com.xinkao.erp.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.CommonService;
import com.xinkao.erp.common.util.RedisUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ldy
 * @since 2022-06-02 16:50:19
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CommonService commonService;


    /**
     * 根据导入token获取导入进度
     * @return
     */
    @PrimaryDataSource
    @ApiOperation(value = "根据导入token获取导入进度")
    @RequestMapping(value = "/getImportByToken", method = RequestMethod.POST)
    public BaseResponse getImportByToken(String importToken) {
        JSONObject json = JSON.parseObject(redisUtil.get(importToken));
        if (json == null){
            return BaseResponse.ok("导入中......");
        }else{
            //验证是否成功
            if ("fail".equals(json.getString("state"))){
                return BaseResponse.fail("导入失败！");
            }else{
                return BaseResponse.ok(json.getString("msg"));
            }
        }
    }

    @PrimaryDataSource
    @PostMapping("/getOBSInfo")
    @ApiOperation("获取OBS信息")
    public BaseResponse getOBSInfo() {
        return commonService.getOBSInfo();
    }




}
