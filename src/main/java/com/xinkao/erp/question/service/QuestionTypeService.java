package com.xinkao.erp.question.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.question.param.QuestionTypeParam;
import com.xinkao.erp.question.param.QuestionTypeAddParam;
import com.xinkao.erp.common.model.param.DeleteParam;

public interface QuestionTypeService extends BaseService<QuestionType> {

    BaseResponse<?> update(QuestionTypeParam param);
    
    BaseResponse<?> save(QuestionTypeAddParam param);

    BaseResponse<?> delQuestionType(DeleteParam param);
}
