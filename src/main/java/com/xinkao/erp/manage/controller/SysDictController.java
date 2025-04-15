package com.xinkao.erp.manage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.param.ClassInfoParam;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.query.DictQuery;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import com.xinkao.erp.system.entity.Dict;
import com.xinkao.erp.system.service.DictService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 字典相关服务
 * 
 * @author Ldy
 *
 */
@RequestMapping("/sys-dict")
@RestController
@Slf4j
public class SysDictController extends BaseController {

    @Autowired
    private DictService dictService;

    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询字典信息")
    public BaseResponse<Page<Dict>> page(@Valid @RequestBody DictQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Dict> page = dictService.pageDict(query.getCode(), query.getValue(), pageable);
        return BaseResponse.ok("成功",page);
    }

    @PrimaryDataSource
    @PostMapping("/getList")
    @ApiOperation("下拉列表字典信息")
    public BaseResponse<List<Dict>> getList(@RequestParam String type) {
        List<Dict> list = dictService.selectBy(type);
        return BaseResponse.ok("成功",list);
    }

    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("新增字典信息")
    @Log(content = "新增字典信息", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody Dict dict) {
        return dictService.saveBy(dict.getDictType(), dict.getDictLabel(), dict.getDictValue()) ? BaseResponse.ok("成功") : BaseResponse.fail("失败");
    }

    @PostMapping("/update")
    @ApiOperation("编辑字典信息")
    @Log(content = "编辑字典信息", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody Dict dict) {
        return dictService.updateById(dict) ? BaseResponse.ok("成功") : BaseResponse.fail("失败");
    }

    @PostMapping("/del")
    @ApiOperation("批量删除字典")
    @Log(content = "批量删除字典", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> del(@RequestBody DeleteParam param) {
        return dictService.deleteByIds(param) ? BaseResponse.ok("成功") : BaseResponse.fail("失败");
    }

    @PostMapping("/updateState")
    @ApiOperation("修改字典状态")
    @Log(content = "修改字典状态", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> updateState(@Valid @RequestBody UpdateStateParam updateStateParam) {
        return dictService.updateState(updateStateParam);
    }

}