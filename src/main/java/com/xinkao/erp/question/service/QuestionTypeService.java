package com.xinkao.erp.question.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.param.QuestionTypeParam;

/**
 * <p>
 * 题库类型表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
public interface QuestionTypeService extends BaseService<QuestionType> {

    BaseResponse<?> update(QuestionTypeParam param);
}
