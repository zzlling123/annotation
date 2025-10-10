package com.xinkao.erp.manage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.manage.query.DictQuery;
import com.xinkao.erp.system.entity.Dict;
import com.xinkao.erp.system.service.DictService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sys-dict")
public class SysDictController {

    @Autowired
    private DictService sysDictService;

    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<Dict>> page(@Valid @RequestBody DictQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Dict> voPage = sysDictService.pageDict(query.getType(), query.getValue(), pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/selectBy")
    public BaseResponse<List<Dict>> selectBy(@RequestParam String type) {
        List<Dict> dictList = sysDictService.selectBy(type);
        return BaseResponse.ok("成功",dictList);
    }

    @PrimaryDataSource
    @PostMapping("/selectOne")
    public BaseResponse<Dict> selectOne(@RequestParam String type) {
        Dict dict = sysDictService.selectOne(type);
        return BaseResponse.ok("成功",dict);
    }

    @PrimaryDataSource
    @PostMapping("/saveBy")
    public BaseResponse<?> saveBy(@RequestParam String type,@RequestParam String name, @RequestParam String value) {
        sysDictService.saveBy(type,name, value);
        return BaseResponse.ok("成功");
    }

    @PrimaryDataSource
    @PostMapping("/deleteByIds")
    public BaseResponse<?> deleteByIds(@RequestParam String ids) {
        sysDictService.deleteByIds(ids);
        return BaseResponse.ok("成功");
    }

    @PrimaryDataSource
    @PostMapping("/setDictBy")
    public BaseResponse<Dict> setDictBy(@RequestParam String type,@RequestParam String name, @RequestParam String value) {
        Dict dict = sysDictService.setDictBy(type,name, value);
        return BaseResponse.ok("成功",dict);
    }

    @PrimaryDataSource
    @PostMapping("/updateState")
    public BaseResponse<?> updateState(@RequestBody UpdateStateParam param) {
        return sysDictService.updateState(param);
    }

}
