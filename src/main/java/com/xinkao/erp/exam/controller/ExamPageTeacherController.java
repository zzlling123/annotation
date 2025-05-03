package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.model.vo.ExamProgressVo;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.param.ExamCorrectParam;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.exam.vo.ExamPageAnswerVo;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;
import com.xinkao.erp.exam.vo.ExamPageUserListVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 教师批改管理
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@RestController
@RequestMapping("/exam-page-teacher")
public class ExamPageTeacherController {

    @Resource
    private ExamPageUserService examPageUserService;

    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("根据当前登录教师获取考试列表(已批阅/应批阅)(固定筛选一个班级)")
    public BaseResponse<Page<ExamPageTeacherVo>> page(@RequestBody @Valid ExamTeacherQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageTeacherVo> voPage = examPageUserService.pageTeacher(query, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @PostMapping("/getExamUserListForExamId")
    @ApiOperation("根据试卷id、班级ID获取学生列表")
    public BaseResponse<Page<ExamPageUserListVo>> getExamUserListForExamId(@RequestBody @Valid ExamUserQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ExamPageUserListVo> voPage = examPageUserService.getExamUserListForExamId(query, pageable);
        return BaseResponse.ok("成功",voPage);
    }

    @PrimaryDataSource
    @PostMapping("/getExamUserInfo/{examPageUserId}")
    @ApiOperation("点击学生，获取该学生此次试卷答题信息")
    public BaseResponse<ExamPageAnswerVo> getExamUserAnswerInfo(@PathVariable String examPageUserId) {
        return examPageUserService.getExamUserAnswerInfo(examPageUserId);
    }

    @PrimaryDataSource
    @PostMapping("/correct")
    @ApiOperation("批改，提交分数(如果是该学生该试卷最后一道问答题则会进行计算总分)")
    @Log(content = "批改",operationType = OperationType.INSERT)
    public BaseResponse<?> correct(@RequestBody @Valid ExamCorrectParam param) {
        return examPageUserService.correct(param);
    }


}
