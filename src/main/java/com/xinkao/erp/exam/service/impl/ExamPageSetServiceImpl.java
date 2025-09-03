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

/**
 * <p>
 * 考试设置表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Slf4j
@Service
public class ExamPageSetServiceImpl extends BaseServiceImpl<ExamPageSetMapper, ExamPageSet> implements ExamPageSetService {

    @Autowired
    private ResultUtils resultUtils;
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private ExamPageSetTypeService examPageSetTypeService;

    /**
     * 导入分类题目分布
     * @param response
     * @param addExamPageSetTypeMap
     * @param handleResult
     * @param examPageSetImportErrorModelList
     * @param token
     */
    //导入试卷分布
    @Transactional
    @Override
    public void importExamPageSetType(HttpServletResponse response, Map<Integer, List<ExamPageSetType>> addExamPageSetTypeMap, HandleResult handleResult, List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList, String token){
//        Integer successCount = handleResult.getSuccessCount();
//        List<String> errorList = handleResult.getErrorList();
//        if(errorList.isEmpty()){
//            if (!addExamPageSetTypeMap.isEmpty()) {
//                int rowNum = 0;
//                //设置总分
//                BigDecimal allScore = 0;
//                //判断所有分数加起来是否等于总分，如果不等于则报错
//                //计算规则：题数乘以每题分数后的总和
//                //计算试卷题目总数
//                ExamPageSet examPageSet = getById(addExamPageSetTypeMap.keySet().iterator().next());
//                for (Integer examPageSetTypeId : addExamPageSetTypeMap.keySet()) {
//                    //新增题目分类数据
//                    List<ExamPageSetType> examPageSetTypes = addExamPageSetTypeMap.get(examPageSetTypeId);
//                    for (ExamPageSetType examPageSetType : examPageSetTypes) {
//                        //增加总分
//                        allScore += examPageSetType.getQuestionNum() * examPageSetType.getScore();
//                    }
//                }
//                if (allScore != examPageSet.getScore()){
//                    log.error("导入试卷分布试题计算总分与设置总分不相等");
//                    errorList.add(resultUtils.getErrMsg(2,"试题计算总分与设置总分不相等"));
//                    redisUtils.set(token, JSONObject.toJSONString(BaseResponse.fail("导入试卷分布试题计算总分与设置总分不相等")), 2, TimeUnit.HOURS);
//                    return;
//                }else{
//                    //删除原题目分类数据
//                    examPageSetTypeService.lambdaUpdate()
//                            .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
//                            .remove();
//                    for (Integer examPageSetTypeId : addExamPageSetTypeMap.keySet()) {
//                        int questionCount = 0;
//                        try {
//                            //新增题目分类数据
//                            List<ExamPageSetType> examPageSetTypes = addExamPageSetTypeMap.get(examPageSetTypeId);
//                            //计算试卷题目总数
//                            for (ExamPageSetType examPageSetType : examPageSetTypes) {
//                                questionCount += examPageSetType.getQuestionNum();
//                            }
//                            examPageSet.setQuestionCount(questionCount);
//                            examPageSet.setQuestionStatus(1);
//                            //新增题目分类数据
//                            examPageSetTypeService.saveBatch(examPageSetTypes);
//                            successCount++;
//                        } catch (Exception e) {
//                            log.error("出现异常: {}", e);
//                            errorList.add(resultUtils.getErrMsg(rowNum + 1,
//                                    "新增时出现异常：" + e.getMessage()));
//                        }
//                        rowNum++;
//                    }
//                    //修改设置
//                    updateById(examPageSet);
//                    //清除预览题目数据
////                    examPageReviewService.removePreview(examPageSet.getId());
//                }
//            }
//            resultUtils.getResult(handleResult,successCount,errorList);
//        }
//
//        if(!errorList.isEmpty()) {
//            redisUtils.set(token, JSONObject.toJSONString(BaseResponse.other("导入失败",examPageSetImportErrorModelList)), 2, TimeUnit.HOURS);
//        }else{
//            redisUtils.set(token, JSONObject.toJSONString(BaseResponse.ok("成功导入")), 2, TimeUnit.HOURS);
//        }
    }

    @Transactional
    @Override
    public BaseResponse<?> saveExamPageSetPoint(String examId, List<ExamPageSetParam> list) {
        //设置总分
        BigDecimal allScore = new BigDecimal(0);
        //判断所有分数加起来是否等于总分，如果不等于则报错
        //计算规则：题数乘以每题分数后的总和
        //计算试卷题目总数
        ExamPageSet examPageSet = lambdaQuery().eq(ExamPageSet::getExamId,examId).one();
        List<ExamPageSetType> examPageSetTypeList = new ArrayList<>();
        int questionCount = 0;
        for (ExamPageSetParam examPageSetParam : list) {
            //增加总分
            allScore = allScore.add(examPageSetParam.getScore().multiply(new BigDecimal(examPageSetParam.getQuestionNum())));
            ExamPageSetType examPageSetType = BeanUtil.copyProperties(examPageSetParam, ExamPageSetType.class);
            examPageSetType.setExamId(examId);
            examPageSetTypeList.add(examPageSetType);
            questionCount += examPageSetType.getQuestionNum();
        }
        if (allScore.compareTo(examPageSet.getScore()) != 0){
            return BaseResponse.fail("试题计算总分与设置总分不相等");
        }
        //删除原题目分类数据
        examPageSetTypeService.lambdaUpdate()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .remove();
        examPageSet.setQuestionCount(questionCount);
        examPageSet.setQuestionStatus(1);
        //新增题目分类数据
        examPageSetTypeService.saveBatch(examPageSetTypeList);
        //修改设置
        updateById(examPageSet);
        return BaseResponse.ok("成功");
    }
}
