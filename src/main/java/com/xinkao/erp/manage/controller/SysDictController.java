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

/**
 * <p>
 * 全局-字典(两级) 前端控制器
 * </p>
 *
 * @author Ldy
 * @since 2025-04-12 15:01:19
 */
@RestController
@RequestMapping("/sys-dict")
public class SysDictController {

    @Autowired
    private DictService sysDictService;

    /**
     * 分页查询字典信息
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询字典信息")
    public BaseResponse<Page<Dict>> page(@Valid @RequestBody DictQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Dict> voPage = sysDictService.pageDict(query.getCode(), query.getValue(), pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/selectBy")
    @ApiOperation("查询字典列表")
    public BaseResponse<List<Dict>> selectBy(@RequestParam String type) {
        List<Dict> dictList = sysDictService.selectBy(type);
        return BaseResponse.ok("成功",dictList);
    }

    @PrimaryDataSource
    @PostMapping("/selectOne")
    @ApiOperation("查询字典信息")
    public BaseResponse<Dict> selectOne(@RequestParam String type) {
        Dict dict = sysDictService.selectOne(type);
        return BaseResponse.ok("成功",dict);
    }

    @PrimaryDataSource
    @PostMapping("/saveBy")
    @ApiOperation("新增字典信息")
    @Log(content = "新增字典信息",operationType = OperationType.INSERT)
    public BaseResponse<?> saveBy(@RequestParam String type,@RequestParam String name, @RequestParam String value) {
        sysDictService.saveBy(type,name, value);
        return BaseResponse.ok("成功");
    }

    @PrimaryDataSource
    @PostMapping("/deleteByIds")
    @ApiOperation("删除字典信息")
    @Log(content = "删除字典信息",operationType = OperationType.DELETE)
    public BaseResponse<?> deleteByIds(@RequestParam String ids) {
        sysDictService.deleteByIds(ids);
        return BaseResponse.ok("成功");
    }

    @PrimaryDataSource
    @PostMapping("/setDictBy")
    @ApiOperation("设置字典值,默认为空")
    @Log(content = "设置字典值,默认为空",operationType = OperationType.UPDATE)
    public BaseResponse<Dict> setDictBy(@RequestParam String type,@RequestParam String name, @RequestParam String value) {
        Dict dict = sysDictService.setDictBy(type,name, value);
        return BaseResponse.ok("成功",dict);
    }

    @PrimaryDataSource
    @PostMapping("/updateState")
    @ApiOperation("修改字典启用状态")
    @Log(content = "修改字典启用状态",operationType = OperationType.UPDATE)
    public BaseResponse<?> updateState(@RequestBody UpdateStateParam param) {
        return sysDictService.updateState(param);
    }

}
