package com.xinkao.erp.question.controller;

import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.question.entity.QuestionMark;
import com.xinkao.erp.question.service.QuestionMarkService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目-标记关联表 前端控制器
 *
 * @author Ldy
 * @since 2025-04-20 22:26:27
 */
@RestController
@RequestMapping("/question-mark")
public class QuestionMarkController extends BaseController {

    @Resource
    private QuestionMarkService questionMarkService;

//    /**
//     * 根据题目ID获取标记树状图
//     *
//     * @return
//     */
//    @PrimaryDataSource
//    @PostMapping("/getListByQid/{qid}")
//    @ApiOperation("根据题目ID获取标记树状图")
//    public BaseResponse<List<Mark>> getListByQid(@PathVariable Integer qid) {
//        return questionMarkService.getListByQid(qid);
//    }
}
