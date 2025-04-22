package com.xinkao.erp.scene.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.ScenePcdImg;
import com.xinkao.erp.scene.query.ScenePcdImgQuery;
import com.xinkao.erp.scene.service.ScenePcdImgService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 *  场景pcd图片管理 前端控制器
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@RestController
@RequestMapping("/scene-pcd-img")
public class ScenePcdImgController {
    @Autowired
    private ScenePcdImgService scenePcdImgService;

    /**
     * 分页查询场景pcd对应的图片文件
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询场景pcd对应的图片文件")
    public BaseResponse<Page<ScenePcdImg>> page(@Valid @RequestBody ScenePcdImgQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ScenePcdImg> voPage = scenePcdImgService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 新增场景pcd对应的图片文件
     *
     * @param scenePcdImg 场景pcd对应的图片文件参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("新增场景pcd对应的图片文件")
    @Log(content = "新增场景pcd对应的图片文件", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody ScenePcdImg scenePcdImg) {
        return scenePcdImgService.save1(scenePcdImg);
    }

    /**
     * 编辑场景pcd对应的图片文件
     *
     * @param scenePcdImg 场景pcd对应的图片文件参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("编辑场景pcd对应的图片文件")
    @Log(content = "编辑场景pcd对应的图片文件", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody ScenePcdImg scenePcdImg) {
        return scenePcdImgService.update(scenePcdImg);
    }

    /**
     * 删除场景pcd对应的图片文件
     *
     * @param id 课程章节ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除场景pcd对应的图片文件")
    @Log(content = "删除场景pcd对应的图片文件", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return scenePcdImgService.delete(id);
    }

}
