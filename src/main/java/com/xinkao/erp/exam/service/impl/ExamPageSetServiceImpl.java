package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.ResultUtils;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.excel.ExamPageSetImportErrorModel;
import com.xinkao.erp.exam.mapper.ExamPageSetMapper;
import com.xinkao.erp.exam.param.ExamPageSetParam;
import com.xinkao.erp.exam.service.ExamPageSetService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.exam.service.ExamPageSetTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ExamPageSetServiceImpl extends BaseServiceImpl<ExamPageSetMapper, ExamPageSet> implements ExamPageSetService {

    @Autowired
    private ResultUtils resultUtils;
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private ExamPageSetTypeService examPageSetTypeService;

    @Transactional
    @Override
    public void importExamPageSetType(HttpServletResponse response, Map<Integer, List<ExamPageSetType>> addExamPageSetTypeMap, HandleResult handleResult, List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList, String token){

    }

    @Transactional
    @Override
    public BaseResponse<?> saveExamPageSetPoint(String examId, List<ExamPageSetParam> list) {
        BigDecimal allScore = new BigDecimal(0);
        ExamPageSet examPageSet = lambdaQuery().eq(ExamPageSet::getExamId,examId).one();
        List<ExamPageSetType> examPageSetTypeList = new ArrayList<>();
        int questionCount = 0;
        for (ExamPageSetParam examPageSetParam : list) {
            allScore = allScore.add(examPageSetParam.getScore().multiply(new BigDecimal(examPageSetParam.getQuestionNum())));
            ExamPageSetType examPageSetType = BeanUtil.copyProperties(examPageSetParam, ExamPageSetType.class);
            examPageSetType.setExamId(examId);
            examPageSetTypeList.add(examPageSetType);
            questionCount += examPageSetType.getQuestionNum();
        }
        if (allScore.compareTo(examPageSet.getScore()) != 0){
            return BaseResponse.fail("试题计算总分与设置总分不相等");
        }
        examPageSetTypeService.lambdaUpdate()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .remove();
        examPageSet.setQuestionCount(questionCount);
        examPageSet.setQuestionStatus(1);
        examPageSetTypeService.saveBatch(examPageSetTypeList);
        updateById(examPageSet);
        return BaseResponse.ok("成功");
    }
}
