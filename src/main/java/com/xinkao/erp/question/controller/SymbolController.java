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

@RestController
@RequestMapping("/symbol")
public class SymbolController {

    @Autowired
    private SymbolService symbolService;

    @PrimaryDataSource
    @GetMapping("/getList")
    public BaseResponse<List<Symbol>> getList() {
        return BaseResponse.ok(symbolService.lambdaQuery().eq(Symbol::getIsDel, 0).list());
    }

    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<Symbol>> page(@RequestBody SymbolQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Symbol> voPage = symbolService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/save")
    public BaseResponse<?> save(@Valid @RequestBody SymbolParam param) {
        return symbolService.save(param);
    }

    @PrimaryDataSource
    @PostMapping("/update")
    public BaseResponse<?> update(@Valid @RequestBody SymbolParam param) {
        return symbolService.update(param);
    }

    @PrimaryDataSource
    @PostMapping("/del/{id}")
    public BaseResponse<?> del(@PathVariable Integer id) {
        return symbolService.del(id);
    }

}
