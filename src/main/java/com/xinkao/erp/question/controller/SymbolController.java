package com.xinkao.erp.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.query.SymbolQuery;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.question.entity.Symbol;
import com.xinkao.erp.question.param.SymbolParam;
import com.xinkao.erp.question.service.SymbolService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 题目标记管理
 *
 * @author Ldy
 * @since 2025-09-03 19:13:25
 */
@RestController
@RequestMapping("/symbol")
public class SymbolController {

    @Autowired
    private SymbolService symbolService;

    /**
     * 标记下拉框
     *
     */
    @PrimaryDataSource
    @GetMapping("/getList")
    @ApiOperation("获取所有标记")
    public BaseResponse<List<Symbol>> getList() {
        return BaseResponse.ok(symbolService.lambdaQuery().eq(Symbol::getIsDel, 0).list());
    }

    /**
     * 分页查询标记信息
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询标记信息")
    public BaseResponse<Page<Symbol>> page(@RequestBody SymbolQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Symbol> voPage = symbolService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 新增标记
     *
     * @param param 标记信息
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("新增标记")
    @Log(content = "新增标记",operationType = OperationType.INSERT)
    public BaseResponse<?> save(@Valid @RequestBody SymbolParam param) {
        return symbolService.save(param);
    }

    /**
     * 编辑标记
     *
     * @param param 标记信息
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/update")
    @ApiOperation("编辑标记")
    @Log(content = "编辑标记",operationType = OperationType.UPDATE)
    public BaseResponse<?> update(@Valid @RequestBody SymbolParam param) {
        return symbolService.update(param);
    }

    /**
     * 删除标记
     *
     * @param id 标记信息
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/del/{id}")
    @ApiOperation("删除标记")
    @Log(content = "删除标记",operationType = OperationType.DELETE)
    public BaseResponse<?> del(@PathVariable Integer id) {
        return symbolService.del(id);
    }

}
