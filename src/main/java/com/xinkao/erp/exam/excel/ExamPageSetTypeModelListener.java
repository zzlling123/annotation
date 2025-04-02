package com.xinkao.erp.exam.excel;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.ResultUtils;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.service.ExamPageSetService;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.service.QuestionTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listener - 用于监听导入试卷设置
 * @Description:
 * @Author: 777
 * @Date: 2021/8/3 18:04
 */
@Slf4j
public class ExamPageSetTypeModelListener extends AnalysisEventListener<ExamPageSetImportModel> {


    private RedisUtil redisUtils;
    private ResultUtils resultUtils;
    private HttpServletResponse response;

    private List<String> errorList = new ArrayList<>();
    private List<ExamPageSetType> examPageSetTypes = new ArrayList<>();

    private Map<String, List<ExamPageSetType>> addExamPageSetPointMap = new HashMap<>();

    private HandleResult handleResult = new HandleResult();

    private QuestionTypeService questionTypeService;

    private ExamPageSetService examPageSetService;

    private List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList = new ArrayList<>();


    private final String token;

    private final String examId;

    public ExamPageSetTypeModelListener(HttpServletResponse response, String token, String examId) {
        this.response = response;
        this.token = token;
        this.examId = examId;
        this.redisUtils = SpringUtil.getBean(RedisUtil.class);
        this.resultUtils = SpringUtil.getBean(ResultUtils.class);
        this.questionTypeService = SpringUtil.getBean(QuestionTypeService.class);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("exception");
        super.onException(exception, context);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(ExamPageSetImportModel examPageSetImportModel, AnalysisContext analysisContext) {
        log.debug("解析到一条数据:{}", JSON.toJSONString(examPageSetImportModel));
        // 可以增加对数据的必要解析
        int rowNum = analysisContext.readRowHolder().getRowIndex();
        String errorMsg = checkEmptyError(examPageSetImportModel, rowNum + 1);
        if(StrUtil.isNotBlank(errorMsg)){
            errorList.add(errorMsg);
            handleResult.setErrorList(errorList);
            log.error("分类：{}，导入信息有误：{}", examPageSetImportModel.getType(), errorMsg);
            return;
        }

        String msg = "";
        String type = examPageSetImportModel.getType();
        String choiceSingleCount = examPageSetImportModel.getChoiceSingleCount();
        String choiceSingleChouCount = examPageSetImportModel.getChoiceSingleChouCount();
        String choiceSingleScore = examPageSetImportModel.getChoiceSingleScore();
        String choiceMultiCount = examPageSetImportModel.getChoiceMultiCount();
        String choiceMultiChouCount = examPageSetImportModel.getChoiceMultiChouCount();
        String choiceMultiScore = examPageSetImportModel.getChoiceMultiScore();
        String choiceMultiPerPart = examPageSetImportModel.getChoiceMultiPerPart();
        String choiceFillCount = examPageSetImportModel.getChoiceFillCount();
        String choiceFillChouCount = examPageSetImportModel.getChoiceFillChouCount();
        String choiceFillScore = examPageSetImportModel.getChoiceFillScore();
        String choiceAnswerCount = examPageSetImportModel.getChoiceAnswerCount();
        String choiceAnswerChouCount = examPageSetImportModel.getChoiceAnswerChouCount();
        String choiceAnswerScore = examPageSetImportModel.getChoiceAnswerScore();
        String choicePracticeCount = examPageSetImportModel.getChoicePracticeCount();
        String choicePracticeChouCount = examPageSetImportModel.getChoicePracticeChouCount();
        String choicePracticeScore = examPageSetImportModel.getChoicePracticeScore();

        //获取设置信息
        ExamPageSet examPageSet = examPageSetService.lambdaQuery().eq(ExamPageSet::getExamId, examId).one();

        QuestionType questionType = questionTypeService.lambdaQuery().eq(QuestionType::getTypeName, type).one();
        if (questionType == null){
            msg = "分类："+type+"，导入信息有误：该分类不存在";
            errorList.add(getHandleMsg(rowNum + 1, msg));
            handleResult.setErrorList(errorList);

            ExamPageSetImportErrorModel examPageSetImportErrorModel = new ExamPageSetImportErrorModel();
            examPageSetImportErrorModel.setType(type);
            examPageSetImportErrorModel.setChoiceSingleCount(choiceSingleCount);
            examPageSetImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
            examPageSetImportErrorModel.setChoiceSingleScore(choiceSingleScore);
            examPageSetImportErrorModel.setChoiceMultiCount(choiceMultiCount);
            examPageSetImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
            examPageSetImportErrorModel.setChoiceMultiScore(choiceMultiScore);
            examPageSetImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
            examPageSetImportErrorModel.setChoiceFillCount(choiceFillCount);
            examPageSetImportErrorModel.setChoiceFillChouCount(choiceFillChouCount);
            examPageSetImportErrorModel.setChoiceFillScore(choiceFillScore);
            examPageSetImportErrorModel.setChoiceAnswerCount(choiceAnswerCount);
            examPageSetImportErrorModel.setChoiceAnswerChouCount(choiceAnswerChouCount);
            examPageSetImportErrorModel.setChoiceAnswerScore(choiceAnswerScore);
            examPageSetImportErrorModel.setChoicePracticeCount(choicePracticeCount);
            examPageSetImportErrorModel.setChoicePracticeChouCount(choicePracticeChouCount);
            examPageSetImportErrorModel.setChoicePracticeScore(choicePracticeScore);
            examPageSetImportErrorModel.setErrorMsg(msg);
            examPageSetImportErrorModelList.add(examPageSetImportErrorModel);

            log.error("题目分类：{}，导入信息有误：{}", type, msg);
            return;
        }

        //如果题库题数大于0，且抽取提数大于0，则开始添加单选题
        if(Integer.parseInt(choiceSingleCount) > 0 && StrUtil.isNotBlank(choiceSingleChouCount)){
            //如果抽取提数大于题库题数，则报错
            if (Integer.parseInt(choiceSingleChouCount) > Integer.parseInt(choiceSingleCount)){
                msg = "题目分类："+type+"，导入信息有误：单选题抽取提数不能大于题库题数";
                errorList.add(getHandleMsg(rowNum + 1, msg));
                handleResult.setErrorList(errorList);

                ExamPageSetImportErrorModel examPageSetImportErrorModel = new ExamPageSetImportErrorModel();
                examPageSetImportErrorModel.setType(type);
                examPageSetImportErrorModel.setChoiceSingleCount(choiceSingleCount);
                examPageSetImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
                examPageSetImportErrorModel.setChoiceSingleScore(choiceSingleScore);
                examPageSetImportErrorModel.setChoiceMultiCount(choiceMultiCount);
                examPageSetImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
                examPageSetImportErrorModel.setChoiceMultiScore(choiceMultiScore);
                examPageSetImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
                examPageSetImportErrorModel.setChoiceFillCount(choiceFillCount);
                examPageSetImportErrorModel.setChoiceFillChouCount(choiceFillChouCount);
                examPageSetImportErrorModel.setChoiceFillScore(choiceFillScore);
                examPageSetImportErrorModel.setChoiceAnswerCount(choiceAnswerCount);
                examPageSetImportErrorModel.setChoiceAnswerChouCount(choiceAnswerChouCount);
                examPageSetImportErrorModel.setChoiceAnswerScore(choiceAnswerScore);
                examPageSetImportErrorModel.setChoicePracticeCount(choicePracticeCount);
                examPageSetImportErrorModel.setChoicePracticeChouCount(choicePracticeChouCount);
                examPageSetImportErrorModel.setChoicePracticeScore(choicePracticeScore);
                examPageSetImportErrorModel.setErrorMsg(msg);
                examPageSetImportErrorModelList.add(examPageSetImportErrorModel);

                log.error("题目分类：{}，导入信息有误：{}", type, msg);
                return;
            }else {
                if(Integer.parseInt(choiceSingleChouCount) > 0){
                    //添加单选题
                    ExamPageSetType examPageSetType = new ExamPageSetType();
                    examPageSetType.setExamId(examId);
                    examPageSetType.setTypeId(questionType.getId());
                    examPageSetType.setTypeName(questionType.getTypeName());
                    examPageSetType.setShape("100");
                    examPageSetType.setQuestionNum(Integer.parseInt(choiceSingleChouCount));
                    examPageSetType.setScore(Integer.parseInt(choiceSingleScore));
                    examPageSetTypes.add(examPageSetType);
                }
            }
        }

        //如果题库题数大于0，且抽取提数大于0，则开始添加多选题
        if(Integer.parseInt(choiceMultiCount) > 0 && StrUtil.isNotBlank(choiceMultiChouCount)){
            //如果抽取提数大于题库题数，则报错
            if (Integer.parseInt(choiceMultiChouCount) > Integer.parseInt(choiceMultiCount)){
                msg = "题目分类："+type+"，导入信息有误：多选题抽取提数不能大于题库题数";
                errorList.add(getHandleMsg(rowNum + 1, msg));
                handleResult.setErrorList(errorList);

                ExamPageSetImportErrorModel examPageSetImportErrorModel = new ExamPageSetImportErrorModel();
                examPageSetImportErrorModel.setType(type);
                examPageSetImportErrorModel.setChoiceSingleCount(choiceSingleCount);
                examPageSetImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
                examPageSetImportErrorModel.setChoiceSingleScore(choiceSingleScore);
                examPageSetImportErrorModel.setChoiceMultiCount(choiceMultiCount);
                examPageSetImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
                examPageSetImportErrorModel.setChoiceMultiScore(choiceMultiScore);
                examPageSetImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
                examPageSetImportErrorModel.setChoiceFillCount(choiceFillCount);
                examPageSetImportErrorModel.setChoiceFillChouCount(choiceFillChouCount);
                examPageSetImportErrorModel.setChoiceFillScore(choiceFillScore);
                examPageSetImportErrorModel.setChoiceAnswerCount(choiceAnswerCount);
                examPageSetImportErrorModel.setChoiceAnswerChouCount(choiceAnswerChouCount);
                examPageSetImportErrorModel.setChoiceAnswerScore(choiceAnswerScore);
                examPageSetImportErrorModel.setChoicePracticeCount(choicePracticeCount);
                examPageSetImportErrorModel.setChoicePracticeChouCount(choicePracticeChouCount);
                examPageSetImportErrorModel.setChoicePracticeScore(choicePracticeScore);
                examPageSetImportErrorModel.setErrorMsg(msg);
                examPageSetImportErrorModelList.add(examPageSetImportErrorModel);

                log.error("题目分类：{}，导入信息有误：{}", type, msg);
                return;
            }else {
                if (StrUtil.isBlank(choiceMultiPerPart)){
                    msg = "题目分类："+type+"，导入信息有误：多选题部分得分不能为空";
                    errorList.add(getHandleMsg(rowNum + 1, msg));
                    handleResult.setErrorList(errorList);

                    ExamPageSetImportErrorModel examPageSetImportErrorModel = new ExamPageSetImportErrorModel();
                    examPageSetImportErrorModel.setType(type);
                    examPageSetImportErrorModel.setChoiceSingleCount(choiceSingleCount);
                    examPageSetImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
                    examPageSetImportErrorModel.setChoiceSingleScore(choiceSingleScore);
                    examPageSetImportErrorModel.setChoiceMultiCount(choiceMultiCount);
                    examPageSetImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
                    examPageSetImportErrorModel.setChoiceMultiScore(choiceMultiScore);
                    examPageSetImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
                    examPageSetImportErrorModel.setChoiceFillCount(choiceFillCount);
                    examPageSetImportErrorModel.setChoiceFillChouCount(choiceFillChouCount);
                    examPageSetImportErrorModel.setChoiceFillScore(choiceFillScore);
                    examPageSetImportErrorModel.setChoiceAnswerCount(choiceAnswerCount);
                    examPageSetImportErrorModel.setChoiceAnswerChouCount(choiceAnswerChouCount);
                    examPageSetImportErrorModel.setChoiceAnswerScore(choiceAnswerScore);
                    examPageSetImportErrorModel.setChoicePracticeCount(choicePracticeCount);
                    examPageSetImportErrorModel.setChoicePracticeChouCount(choicePracticeChouCount);
                    examPageSetImportErrorModel.setChoicePracticeScore(choicePracticeScore);
                    examPageSetImportErrorModel.setErrorMsg(msg);
                    examPageSetImportErrorModelList.add(examPageSetImportErrorModel);

                    log.error("题目分类：{}，导入信息有误：{}", type, msg);
                    return;
                }
                if(Integer.parseInt(choiceMultiChouCount) > 0){
                    //添加多选题
                    ExamPageSetType examPageSetType = new ExamPageSetType();
                    examPageSetType.setExamId(examId);
                    examPageSetType.setTypeId(questionType.getId());
                    examPageSetType.setTypeName(questionType.getTypeName());
                    examPageSetType.setShape("200");
                    examPageSetType.setQuestionNum(Integer.parseInt(choiceSingleChouCount));
                    examPageSetType.setScore(Integer.parseInt(choiceSingleScore));
                    examPageSetType.setScorePart(Integer.parseInt(choiceMultiPerPart));
                    examPageSetTypes.add(examPageSetType);
                }
            }
        }

        //如果题库题数大于0，且抽取提数大于0，则开始添加填空题
        if(Integer.parseInt(choiceFillCount) > 0 && StrUtil.isNotBlank(choiceFillChouCount)){
            //如果抽取提数大于题库题数，则报错
            if (Integer.parseInt(choiceFillChouCount) > Integer.parseInt(choiceFillCount)){
                msg = "题目分类："+type+"，导入信息有误：填空题题抽取提数不能大于题库题数";
                errorList.add(getHandleMsg(rowNum + 1, msg));
                handleResult.setErrorList(errorList);

                ExamPageSetImportErrorModel examPageSetImportErrorModel = new ExamPageSetImportErrorModel();
                examPageSetImportErrorModel.setType(type);
                examPageSetImportErrorModel.setChoiceSingleCount(choiceSingleCount);
                examPageSetImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
                examPageSetImportErrorModel.setChoiceSingleScore(choiceSingleScore);
                examPageSetImportErrorModel.setChoiceMultiCount(choiceMultiCount);
                examPageSetImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
                examPageSetImportErrorModel.setChoiceMultiScore(choiceMultiScore);
                examPageSetImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
                examPageSetImportErrorModel.setChoiceFillCount(choiceFillCount);
                examPageSetImportErrorModel.setChoiceFillChouCount(choiceFillChouCount);
                examPageSetImportErrorModel.setChoiceFillScore(choiceFillScore);
                examPageSetImportErrorModel.setChoiceAnswerCount(choiceAnswerCount);
                examPageSetImportErrorModel.setChoiceAnswerChouCount(choiceAnswerChouCount);
                examPageSetImportErrorModel.setChoiceAnswerScore(choiceAnswerScore);
                examPageSetImportErrorModel.setChoicePracticeCount(choicePracticeCount);
                examPageSetImportErrorModel.setChoicePracticeChouCount(choicePracticeChouCount);
                examPageSetImportErrorModel.setChoicePracticeScore(choicePracticeScore);
                examPageSetImportErrorModel.setErrorMsg(msg);
                examPageSetImportErrorModelList.add(examPageSetImportErrorModel);

                log.error("题目分类：{}，导入信息有误：{}", type, msg);
                return;
            }else {
                if(Integer.parseInt(choiceFillChouCount) > 0){
                    //添加填空题
//                    ExamPageSetPoint examPageSetPoint = new ExamPageSetPoint();
//                    examPageSetPoint.setYear(examPageSet.getYear());
//                    examPageSetPoint.setProjectId(examPageSet.getProjectId());
//                    examPageSetPoint.setPageSetId(examPageSet.getId());
//                    examPageSetPoint.setSubjectCode(examPageSet.getSubjectCode());
//                    examPageSetPoint.setSubjectName(examPageSet.getSubjectName());
//                    examPageSetPoint.setPointId(questionType.getId());
//                    examPageSetPoint.setPoint(questionType.getPoint());
//                    examPageSetPoint.setQuestionCount(Integer.valueOf(judgeChouCount));
//                    examPageSetPoint.setType(300);
//                    examPageSetPoint.setScorePer(Integer.valueOf(judgeScore));
//                    examSetPoints.add(examPageSetPoint);
                }
            }
        }

//        try {
//            if (!examPageSetTypes.isEmpty()){
//                addExamPageSetPointMap.put(examPageSet.getId(), examPageSetTypes);
//            }
//            //将正确的也进行保存（错误原因为空）
//            ExamSetPointImportErrorModel examSetPointImportErrorModel = new ExamSetPointImportErrorModel();
//            examSetPointImportErrorModel.setPoint(type);
//            examSetPointImportErrorModel.setChoiceSingleCount(choiceSingleCount);
//            examSetPointImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
//            examSetPointImportErrorModel.setChoiceSingleScore(choiceSingleScore);
//            examSetPointImportErrorModel.setChoiceMultiCount(choiceMultiCount);
//            examSetPointImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
//            examSetPointImportErrorModel.setChoiceMultiScore(choiceMultiScore);
//            examSetPointImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
//            examSetPointImportErrorModel.setJudgeCount(judgeCount);
//            examSetPointImportErrorModel.setJudgeChouCount(judgeChouCount);
//            examSetPointImportErrorModel.setJudgeScore(judgeScore);
//            examSetPointImportErrorModel.setErrorInfo("");
//            examSetPointImportErrorModelList.add(examSetPointImportErrorModel);
//        } catch (BusinessException e) {
//            errorList.add(getHandleMsg(rowNum + 1, e.getMessage()));
//            handleResult.setErrorList(errorList);
//            return;
//        }
    }

    private String checkEmptyError(ExamPageSetImportModel examPageSetImportModel, int row) {
        String msg = "";
        String type = examPageSetImportModel.getType();
        String choiceSingleCount = examPageSetImportModel.getChoiceSingleCount();
        String choiceSingleChouCount = examPageSetImportModel.getChoiceSingleChouCount();
        String choiceSingleScore = examPageSetImportModel.getChoiceSingleScore();
        String choiceMultiCount = examPageSetImportModel.getChoiceMultiCount();
        String choiceMultiChouCount = examPageSetImportModel.getChoiceMultiChouCount();
        String choiceMultiScore = examPageSetImportModel.getChoiceMultiScore();
        String choiceMultiPerPart = examPageSetImportModel.getChoiceMultiPerPart();
        String choiceFillCount = examPageSetImportModel.getChoiceFillCount();
        String choiceFillChouCount = examPageSetImportModel.getChoiceFillChouCount();
        String choiceFillScore = examPageSetImportModel.getChoiceFillScore();
        String choiceAnswerCount = examPageSetImportModel.getChoiceAnswerCount();
        String choiceAnswerChouCount = examPageSetImportModel.getChoiceAnswerChouCount();
        String choiceAnswerScore = examPageSetImportModel.getChoiceAnswerScore();
        String choicePracticeCount = examPageSetImportModel.getChoicePracticeCount();
        String choicePracticeChouCount = examPageSetImportModel.getChoicePracticeChouCount();
        String choicePracticeScore = examPageSetImportModel.getChoicePracticeScore();
        List<String> msgList = new ArrayList<String>();
        if (StrUtil.isBlank(type)) {
        	msgList.add("题目分类名称不能为空");
        }
        if (StrUtil.isNotBlank(choiceSingleChouCount)) {
            if (!NumberUtil.isNumber(choiceSingleChouCount)){
            	msgList.add("单选抽取题数必须为纯数字");
            }else {
            	if (StrUtil.isBlank(choiceSingleScore)) {
            		msgList.add("请填写单选题分值");
            	}else {
            		if (!NumberUtil.isNumber(choiceSingleScore)){
            			msgList.add("单选题分值必须为纯数字");
                    }
            	}
            }
        }
        
        if (StrUtil.isNotBlank(choiceMultiChouCount)) {
            if (!NumberUtil.isNumber(choiceMultiChouCount)){
            	msgList.add("多选题抽取题数必须为纯数字");
            }else {
            	 if (StrUtil.isBlank(choiceMultiScore)) {
            		 msgList.add("请填写多选题分值");
                 }else {
                	 if (!NumberUtil.isNumber(choiceMultiScore)){
                		 msgList.add("多选题分值必须为纯数字");
                	 }
                 }
            	 if (StrUtil.isBlank(choiceMultiPerPart)) {
            		 msgList.add("请填写多选部分得分");
            	 }else {
            		 if (!NumberUtil.isNumber(choiceMultiPerPart)){
            			 msgList.add("多选部分得分必须为纯数字");
            		 }
            	 }
            }
        }
        
//        if (StrUtil.isNotBlank(judgeChouCount)) {
//            if (!NumberUtil.isNumber(judgeChouCount)){
//            	msgList.add("填空题抽取题数必须为纯数字");
//            }else {
//            	 if (StrUtil.isBlank(judgeScore)) {
//            		 msgList.add("请填写填空题分值");
//                 }else {
//                	 if (!NumberUtil.isNumber(judgeScore)){
//                		 msgList.add("填空题分值必须为纯数字");
//                	 }
//                 }
//            }
//        }
        
        //如果msg不为空，则添加到错误集合
        if (!msgList.isEmpty()){
        	msg = StringUtils.join(msgList,";");
            ExamPageSetImportErrorModel examPageSetImportErrorModel = new ExamPageSetImportErrorModel();
            examPageSetImportErrorModel.setType(type);
            examPageSetImportErrorModel.setChoiceSingleCount(choiceSingleCount);
            examPageSetImportErrorModel.setChoiceSingleChouCount(choiceSingleChouCount);
            examPageSetImportErrorModel.setChoiceSingleScore(choiceSingleScore);
            examPageSetImportErrorModel.setChoiceMultiCount(choiceMultiCount);
            examPageSetImportErrorModel.setChoiceMultiChouCount(choiceMultiChouCount);
            examPageSetImportErrorModel.setChoiceMultiScore(choiceMultiScore);
            examPageSetImportErrorModel.setChoiceMultiPerPart(choiceMultiPerPart);
            examPageSetImportErrorModel.setChoiceFillCount(choiceFillCount);
            examPageSetImportErrorModel.setChoiceFillChouCount(choiceFillChouCount);
            examPageSetImportErrorModel.setChoiceFillScore(choiceFillScore);
            examPageSetImportErrorModel.setChoiceAnswerCount(choiceAnswerCount);
            examPageSetImportErrorModel.setChoiceAnswerChouCount(choiceAnswerChouCount);
            examPageSetImportErrorModel.setChoiceAnswerScore(choiceAnswerScore);
            examPageSetImportErrorModel.setChoicePracticeCount(choicePracticeCount);
            examPageSetImportErrorModel.setChoicePracticeChouCount(choicePracticeChouCount);
            examPageSetImportErrorModel.setChoicePracticeScore(choicePracticeScore);
            examPageSetImportErrorModel.setErrorMsg(msg);
            examPageSetImportErrorModelList.add(examPageSetImportErrorModel);
        }
        return getHandleMsg(row, msg);
    }

    /**
     * 返回msg
     * @param index
     * @param msg
     * @return
     */
    private String getHandleMsg(Integer index, String msg){
        if(StrUtil.isBlank(msg)){
            return "";
        }
        return resultUtils.getErrMsg(index, msg);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        handleResult.setTotalCount(addExamPageSetPointMap.size() + errorList.size());
//        examPageSetService.importExamPageSetPoint(response,addExamPageSetPointMap, handleResult,examSetPointImportErrorModelList,token);
        log.info("存储数据库成功！");
    }

    /**
     * 返回结果
     * @return
     */
    public BaseResponse getResult() {
        return BaseResponse.ok(handleResult.getResult());
    }
}
