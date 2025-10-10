package com.xinkao.erp.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.question.param.QuestionChildParam;
import com.xinkao.erp.question.param.QuestionFormTitleParam;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService extends BaseService<Question> {

    Page<QuestionPageVo> page(QuestionQuery query, Pageable pageable);


    Page<QuestionExercisePageVo> page1(QuestionQuery query, Pageable pageable);

    QuestionInfoVo getQuestionDetail(Integer id);

    BaseResponse<?> save(QuestionParam questionParam);

    BaseResponse<?> saveQuestionFormTitle(QuestionFormTitleParam questionFormTitleParam);

    BaseResponse<?> updateQuestionFormTitle(QuestionFormTitleParam questionFormTitleParam);

    BaseResponse<?> saveQuestionChild(QuestionChildParam questionChildParam);

    BaseResponse<?> updateQuestionChild(QuestionChildParam questionChildParam);

    BaseResponse<?> update(QuestionParam questionParam);

    BaseResponse<?> del(DeleteParam param);

    BaseResponse<?> delTitle(DeleteParam param);

    BaseResponse<?> delChild(DeleteParam param);

    void selfSave();

    BaseResponse<List<QuestionFormVo>> getQuestionFormInfo(Integer questionId);

    QuestionImportResultVO importQuestions(MultipartFile file);

    QuestionImportResultVO importQuestionFormZipV2(MultipartFile file) throws IOException;
}