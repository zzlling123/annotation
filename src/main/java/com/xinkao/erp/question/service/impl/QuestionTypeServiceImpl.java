package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.mapper.QuestionTypeMapper;
import com.xinkao.erp.question.param.QuestionTypeParam;
import com.xinkao.erp.question.param.QuestionTypeAddParam;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.question.service.QuestionTypeService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class QuestionTypeServiceImpl extends BaseServiceImpl<QuestionTypeMapper, QuestionType> implements QuestionTypeService {


    @Override
    public BaseResponse<?> update(QuestionTypeParam param) {
        QuestionType questionType = BeanUtil.copyProperties(param, QuestionType.class);
        return updateById(questionType) ? BaseResponse.ok("编辑成功") : BaseResponse.fail("编辑失败");
    }

    @Override
    public BaseResponse<?> save(QuestionTypeAddParam param) {
        QuestionType questionType = BeanUtil.copyProperties(param, QuestionType.class);
        return save(questionType) ? BaseResponse.ok("新增成功") : BaseResponse.fail("新增失败");
    }

    @Override
    public BaseResponse<?> delQuestionType(DeleteParam param) {
        if (param.getIds() == null || param.getIds().isEmpty()) {
            return BaseResponse.fail("ID不能为空");
        }
        return this.removeByIds(param.getIds()) ? BaseResponse.ok("删除成功") : BaseResponse.fail("删除失败");
    }
}
