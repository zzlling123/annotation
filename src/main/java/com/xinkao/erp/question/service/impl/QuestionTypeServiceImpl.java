package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.mapper.QuestionTypeMapper;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.param.QuestionTypeParam;
import com.xinkao.erp.question.service.QuestionTypeService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 题库类型表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImpl<QuestionTypeMapper, QuestionType> implements QuestionTypeService {


    @Override
    public BaseResponse<?> update(QuestionTypeParam param) {
        QuestionType questionType = BeanUtil.copyProperties(param, QuestionType.class);
        return updateById(questionType) ? BaseResponse.ok("编辑成功") : BaseResponse.fail("编辑失败");
    }
}
