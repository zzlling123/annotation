package com.xinkao.erp.question.controller;

import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.question.entity.DifficultyPoint;
import com.xinkao.erp.question.entity.Symbol;
import com.xinkao.erp.question.service.DifficultyPointService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  难度知识点对应
 *
 * @author Ldy
 * @since 2025-09-13 18:28:39
 */
@RestController
@RequestMapping("/difficulty-point")
public class DifficultyPointController {

    @Autowired
    private DifficultyPointService difficultyPointService;

    /**
     * 难度知识点下拉框
     *
     */
    @PrimaryDataSource
    @GetMapping("/getList/{level}")
    @ApiOperation("获取所有对应难度知识点")
    public BaseResponse<List<DifficultyPoint>> getList(@PathVariable Integer level) {
        return BaseResponse.ok(difficultyPointService.lambdaQuery().eq(DifficultyPoint::getDifficultyLevel, level).eq(DifficultyPoint::getIsDel, 0).list());
    }
}
