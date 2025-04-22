package com.xinkao.erp.scene.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.ScenePcd;
import com.xinkao.erp.scene.query.ScenePcdQuery;
import com.xinkao.erp.scene.service.ScenePcdService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  场景pcd管理 前端控制器
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@RestController
@RequestMapping("/scene-pcd")
public class ScenePcdController {
    
    @Autowired
    private ScenePcdService scenePcdService;

    /**
     * 分页查询场景pcd文件
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询场景pcd文件")
    public BaseResponse<Page<ScenePcd>> page(@Valid @RequestBody ScenePcdQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ScenePcd> voPage = scenePcdService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 新增场景pcd文件
     *
     * @param ScenePcd 场景pcd文件参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("新增场景pcd文件")
    @Log(content = "新增场景pcd文件", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody ScenePcd ScenePcd) {
        return scenePcdService.save1(ScenePcd);
    }

    /**
     * 编辑场景pcd文件
     *
     * @param ScenePcd 场景pcd文件参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("编辑场景pcd文件")
    @Log(content = "编辑场景pcd文件", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody ScenePcd ScenePcd) {
        return scenePcdService.update(ScenePcd);
    }

    /**
     * 删除场景pcd文件
     *
     * @param id 课程章节ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除场景pcd文件")
    @Log(content = "删除场景pcd文件", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return scenePcdService.delete(id);
    }

}
