package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.model.vo.ExamProgressVo;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;

import java.util.List;
import java.util.Map;

public interface ExamPageUserService extends BaseService<ExamPageUser> {

    Page<ExamUserVo>page(BasePageQuery query, Pageable pageable);

    BaseResponse<ExamUserVo> getExamUserInfo(ExamUserQuery examUserQuery);

    BaseResponse<List<ExamProgressVo>> getExamUserProgress(ExamUserQuery examUserQuery);

    BaseResponse<?> submitAnswer(ExamPageUserAnswerParam examPageUserAnswerParam);

    BaseResponse<Map<String,Integer>> submitExam(SubmitParam submitParam);

    BaseResponse<?> heartBeat(ExamUserQuery examUserQuery);

    //教师批改部分
    Page<ExamPageTeacherVo>pageTeacher(ExamTeacherQuery query, Pageable pageable);
}