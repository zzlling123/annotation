package com.xinkao.erp.manual.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.manual.entity.Manual;
import com.xinkao.erp.manual.param.ManualParam;
import com.xinkao.erp.manual.query.ManualQuery;
import com.xinkao.erp.manual.service.ManualService;
import com.xinkao.erp.manual.vo.ManualVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manual")
public class ManualController extends BaseController {

    @Value("${path.fileUrl}")
    private String fileUrl;
    @Value("${ipurl.url}")
    private String ipurl;

    @Resource
    private ManualService manualService;

    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/page")
    public BaseResponse<Page<ManualVo>> page(@RequestBody ManualQuery query) {
        Page<ManualVo> voPage = manualService.page(query);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/save")
    public BaseResponse<?> save(@Valid @RequestBody ManualParam manualParam) {
        return manualService.save(manualParam);
    }

    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/update")
    public BaseResponse<?> update(@Valid @RequestBody ManualParam manualParam) {
        return manualService.update(manualParam);
    }

    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/del")
    public BaseResponse<?> del(@RequestBody DeleteParam param) {
        return manualService.del(param);
    }

    @PrimaryDataSource
    @GetMapping("/getByUserType")
    public BaseResponse<ManualVo> getByUserType() {


        ManualVo manual = manualService.getByUserType();
        if (manual == null) {
            return BaseResponse.fail("该用户类型暂无文档");
        }
        
        return BaseResponse.ok("成功", manual);
    }

    @PrimaryDataSource
    @GetMapping("/getUserTypes")
    public BaseResponse<List<Manual.UserTypeEnum>> getUserTypes() {
        List<Manual.UserTypeEnum> userTypes = Arrays.asList(Manual.UserTypeEnum.values());
        return BaseResponse.ok("成功", userTypes);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping(value = "/upload/file", consumes = "multipart/form-data")
    public BaseResponse<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {

        try {
            String saveFileName;
            File file1 = new File(fileUrl);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (file.isEmpty()) {
                return BaseResponse.fail("文件为空");
            }
            
            System.out.println("fileName:" + file.getOriginalFilename());
            saveFileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            File fileNew = new File(fileUrl, saveFileName);

            if (!fileNew.getParentFile().exists()) {
                fileNew.getParentFile().mkdirs();
            }
            
            file.transferTo(fileNew);
            String newFileUrl = ipurl + "/annotation/fileUrl/" + saveFileName;
            
            return BaseResponse.ok("上传成功", newFileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.fail("上传失败：" + e.getMessage());
        }
    }


} 