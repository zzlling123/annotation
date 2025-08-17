package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.model.param.ExamPageUserChildAnswerParam;
import com.xinkao.erp.exam.model.vo.ExamPageUserQuestionVo;
import com.xinkao.erp.exam.model.vo.ExamPageUserVo;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.model.vo.ExamProgressVo;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.param.ExamCorrectChildParam;
import com.xinkao.erp.exam.param.ExamCorrectParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.vo.ExamPageAnswerVo;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;
import com.xinkao.erp.exam.vo.ExamPageUserListVo;

import java.util.List;
import java.util.Map;

public interface ExamPageUserService extends BaseService<ExamPageUser> {

    Page<ExamUserVo>page(ExamQuery query, Pageable pageable);

    BaseResponse<ExamUserVo> getExamUserInfo(ExamUserQuery examUserQuery);

    BaseResponse<List<ExamProgressVo>> getExamUserProgress(ExamUserQuery examUserQuery);

    BaseResponse<?> submitAnswer(ExamPageUserAnswerParam examPageUserAnswerParam);

    BaseResponse<?> submitChildAnswer(ExamPageUserChildAnswerParam param);

    BaseResponse<Map<String,Integer>> submitExam(SubmitParam submitParam);

    BaseResponse<?> heartBeat(ExamUserQuery examUserQuery);

    //教师批改部分
    Page<ExamPageTeacherVo>pageTeacher(ExamTeacherQuery query, Pageable pageable);

    Page<ExamPageUserListVo>getExamUserListForExamId(ExamUserQuery query, Pageable pageable);

    BaseResponse<ExamPageAnswerVo> getExamUserAnswerInfo(String examPageUserId);

    BaseResponse<?> correct(ExamCorrectParam param);

    BaseResponse<?> correctChild(ExamCorrectChildParam param);

    void sumScore(Integer userId,Integer examId);

    BaseResponse<ExamPageUserQuestionVo> getUserQuestionInfo(String id);

    List<ExamPageUserVo> getExamPageUserName(Integer classId);

    Page<ExamPageUserListVo> getExamUserListForExamIdByUserIds(Integer examId, List<Integer> userIds, Pageable pageable);
}