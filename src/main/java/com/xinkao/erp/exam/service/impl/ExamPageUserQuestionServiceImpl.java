package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.exam.entity.*;
import com.xinkao.erp.exam.mapper.ExamPageUserQuestionMapper;
import com.xinkao.erp.exam.service.*;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.entity.QuestionChild;
import com.xinkao.erp.question.entity.QuestionFormTitle;
import com.xinkao.erp.question.mapper.QuestionMapper;
import com.xinkao.erp.question.service.QuestionChildService;
import com.xinkao.erp.question.service.QuestionFormTitleService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExamPageUserQuestionServiceImpl extends BaseServiceImpl<ExamPageUserQuestionMapper, ExamPageUserQuestion> implements ExamPageUserQuestionService {

    @Resource
    private ExamPageUserService examPageUserService;
    @Resource
    private QuestionFormTitleService questionFormTitleService;
    @Resource
    private QuestionChildService questionChildService;
    @Resource
    private ExamService examService;
    @Resource
    private ExamPageUserLogService examPageUserLogService;
    @Resource
    private ExamPageSetTypeService examPageSetTypeService;
    @Resource
    private ExamPageUserAnswerService examPageUserAnswerService;
    @Resource
    private ExamPageUserQuestionFormTitleService examPageUserQuestionFormTitleService;
    @Resource
    private ExamPageUserQuestionChildService examPageUserQuestionChildService;
    @Resource
    private ExamPageUserChildAnswerService examPageUserChildAnswerService;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private ExamClassService examClassService;
    @Resource
    private UserService userService;
    @Resource
    private ExamExpertAssignmentService examExpertAssignmentService;

    @Override
    public void rollMaking(ExamPageSet examPageSet, List<User> userList, String token) {

        List<ExamPageUser> examPageUserList = new ArrayList<>();
        List<ExamPageUserLog> examPageUserLogList = new ArrayList<>();
        Exam exam = examService.getById(examPageSet.getExamId());

        examPageUserService.lambdaUpdate()
                .eq(ExamPageUser::getExamId,examPageSet.getExamId())
                .remove();
        examPageUserLogService.lambdaUpdate()
                .eq(ExamPageUserLog::getExamId,examPageSet.getExamId())
                .remove();

        lambdaUpdate().eq(ExamPageUserQuestion::getExamId,examPageSet.getExamId()).remove();
        examPageUserQuestionFormTitleService.lambdaUpdate().eq(ExamPageUserQuestionFormTitle::getExamId,examPageSet.getExamId()).remove();
        examPageUserQuestionChildService.lambdaUpdate().eq(ExamPageUserQuestionChild::getExamId,examPageSet.getExamId()).remove();
        examPageUserChildAnswerService.lambdaUpdate().eq(ExamPageUserChildAnswer::getExamId,examPageSet.getExamId()).remove();

        examPageUserAnswerService.lambdaUpdate().eq(ExamPageUserAnswer::getExamId,examPageSet.getExamId()).remove();
        for (User user : userList) {

            ExamPageUser examPageUser = new  ExamPageUser();
            examPageUser.setUserId(user.getId());
            examPageUser.setClassId(user.getClassId());
            examPageUser.setExamId(examPageSet.getExamId());
            examPageUser.setSelectStatus(1);
            examPageUser.setCreateTime(DateUtil.date());
            examPageUserList.add(examPageUser);


            ExamPageUserLog examPageUserLog = new ExamPageUserLog();
            BeanUtil.copyProperties(examPageUser, examPageUserLog);
            examPageUserLog.setId(null);
            examPageUserLog.setCreateTime(DateUtil.date());
            examPageUserLogList.add(examPageUserLog);
        }
        examPageUserService.saveBatch(examPageUserList);
        examPageUserLogService.saveBatch(examPageUserLogList);

        List<ExamPageSetType> examPageSetTypeList = examPageSetTypeService.lambdaQuery()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .orderByAsc(ExamPageSetType::getShape)
                .list();

        Map<Integer,List<ExamPageSetType>> examPageSetTypeMap = examPageSetTypeList.stream().collect(Collectors.groupingBy(ExamPageSetType::getShape));


        if (0 == examPageSet.getPageMode()|| 1 == examPageSet.getPageMode()) {

            Map<Integer,List<Question>> questionMap = getQuestionMap(exam,examPageSetTypeMap);

            if (examPageSet.getPageMode() == 0) {
                for (ExamPageUser examPageUser : examPageUserList) {
                    List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                    List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = new ArrayList<>();
                    List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
                    List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();

                    boolean needCorrect = false;

                    try{
                        examPageUser.setSelectStatus(1);
                        examPageUserService.updateById(examPageUser);
                        int num = 0;
                        for (Integer integer : questionMap.keySet()) {
                            List<Question> questionList = questionMap.get(integer);
                            for (int i = 0;i < questionList.size();i++) {
                                Question question = questionList.get(i);
                                ExamPageUserQuestion examPageUserQuestion = new ExamPageUserQuestion();
                                BeanUtil.copyProperties(question,examPageUserQuestion);
                                examPageUserQuestion.setId(IdUtil.getSnowflakeNextIdStr());
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserQuestion.setOldQuestionId(question.getId());
                                examPageUserQuestion.setNum(String.valueOf(num+1));
                                examPageUserQuestion.setNumSort(num+1);
                                examPageUserQuestion.setCreateTime(DateUtil.date());
                                examPageUserQuestionList.add(examPageUserQuestion);


                                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                                BeanUtil.copyProperties(examPageUserQuestion,examPageUserAnswer);
                                examPageUserAnswer.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                                examPageUserAnswer.setRightAnswer(question.getAnswer());
                                examPageUserAnswer.setCreateTime(DateUtil.date());
                                examPageUserAnswerList.add(examPageUserAnswer);


                                if (question.getIsForm() == 1){
                                    examPageUserQuestionFormTitleList.addAll(getQuestionFormTitle(question.getId(),examPageUser.getUserId(),examPageUser.getExamId(),examPageUserQuestion.getId()));
                                    examPageUserQuestionChildList.addAll(getQuestionChild(question,examPageUser.getUserId(),examPageUser.getExamId(),examPageUserQuestion.getId(),examPageUserQuestionFormTitleList));
                                }
                                num++;
                                if(question.getNeedCorrect() == 1){
                                    needCorrect = true;
                                }
                            }
                        }
                        saveBatch(examPageUserQuestionList);
                        examPageUserAnswerService.saveBatch(examPageUserAnswerList);

                        if (!examPageUserQuestionChildList.isEmpty()) {

                            List<ExamPageUserChildAnswer> examPageUserChildAnswerList = getQuestionChildAnswer(examPageUserQuestionChildList);
                            examPageUserQuestionFormTitleService.saveBatch(examPageUserQuestionFormTitleList);
                            examPageUserQuestionChildService.saveBatch(examPageUserQuestionChildList);
                            examPageUserChildAnswerService.saveBatch(examPageUserChildAnswerList);
                        }

                        examPageUser.setSelectStatus(2);
                        examPageUser.setNeedCorrect(needCorrect?1:0);
                        examPageUserService.updateById(examPageUser);
                    }catch (Exception e){
                        continue;
                    }
                }
            }else {

                for (ExamPageUser examPageUser : examPageUserList) {
                    List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                    List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = new ArrayList<>();
                    List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
                    List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();

                    boolean needCorrect = false;
                    try{

                        examPageUser.setSelectStatus(1);
                        examPageUserService.updateById(examPageUser);
                        int allNum = 0;
                        Set<Integer> numSet = new HashSet<>();
                        for (Integer integer : questionMap.keySet()) {
                            List<Question> questionList = questionMap.get(integer);
                            allNum += questionList.size();
                            for (int i = 0;i < questionList.size();i++) {

                                int num = getQuestionNum(numSet,allNum);
                                Question question = questionList.get(i);
                                ExamPageUserQuestion examPageUserQuestion = new ExamPageUserQuestion();
                                BeanUtil.copyProperties(question,examPageUserQuestion);
                                examPageUserQuestion.setId(IdUtil.getSnowflakeNextIdStr());
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserQuestion.setOldQuestionId(question.getId());
                                examPageUserQuestion.setNum(String.valueOf(num));
                                examPageUserQuestion.setNumSort(num);
                                examPageUserQuestion.setCreateTime(DateUtil.date());
                                examPageUserQuestionList.add(examPageUserQuestion);

                                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                                BeanUtil.copyProperties(examPageUserQuestion,examPageUserAnswer);
                                examPageUserAnswer.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                                examPageUserAnswer.setRightAnswer(examPageUserQuestion.getAnswer());
                                examPageUserAnswer.setCreateTime(DateUtil.date());
                                examPageUserAnswerList.add(examPageUserAnswer);


                                if (question.getIsForm() == 1){
                                    examPageUserQuestionFormTitleList.addAll(getQuestionFormTitle(question.getId(),examPageUser.getUserId(),examPageUser.getExamId(),examPageUserQuestion.getId()));
                                    examPageUserQuestionChildList.addAll(getQuestionChild(question,examPageUser.getUserId(),examPageUser.getExamId(),examPageUserQuestion.getId(),examPageUserQuestionFormTitleList));
                                }
                                if(question.getNeedCorrect() == 1){
                                    needCorrect = true;
                                }
                            }
                        }
                        saveBatch(examPageUserQuestionList);
                        examPageUserAnswerService.saveBatch(examPageUserAnswerList);

                        if (!examPageUserQuestionChildList.isEmpty()) {

                            List<ExamPageUserChildAnswer> examPageUserChildAnswerList = getQuestionChildAnswer(examPageUserQuestionChildList);
                            examPageUserQuestionFormTitleService.saveBatch(examPageUserQuestionFormTitleList);
                            examPageUserQuestionChildService.saveBatch(examPageUserQuestionChildList);
                            examPageUserChildAnswerService.saveBatch(examPageUserChildAnswerList);
                        }

                        examPageUser.setSelectStatus(2);
                        examPageUser.setNeedCorrect(needCorrect?1:0);
                        examPageUserService.updateById(examPageUser);
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }else{

            for (ExamPageUser examPageUser : examPageUserList) {
                List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = new ArrayList<>();
                List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
                List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();
                try{

                    Map<Integer,List<Question>> questionMap = getQuestionMap(exam,examPageSetTypeMap);

                    examPageUser.setSelectStatus(1);
                    examPageUserService.updateById(examPageUser);
                    int num = 0;

                    boolean needCorrect = false;
                    for (Integer integer : questionMap.keySet()) {
                        List<Question> questionList = questionMap.get(integer);
                        for (Question question : questionList) {
                            ExamPageUserQuestion examPageUserQuestion = new ExamPageUserQuestion();
                            BeanUtil.copyProperties(question, examPageUserQuestion);
                            examPageUserQuestion.setId(IdUtil.getSnowflakeNextIdStr());
                            examPageUserQuestion.setExamId(examPageUser.getExamId());
                            examPageUserQuestion.setUserId(examPageUser.getUserId());
                            examPageUserQuestion.setOldQuestionId(question.getId());
                            examPageUserQuestion.setNum(String.valueOf(num + 1));
                            examPageUserQuestion.setNumSort(num + 1);
                            examPageUserQuestion.setCreateTime(DateUtil.date());
                            examPageUserQuestionList.add(examPageUserQuestion);

                            ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                            BeanUtil.copyProperties(examPageUserQuestion, examPageUserAnswer);
                            examPageUserAnswer.setId(null);
                            examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                            examPageUserAnswer.setRightAnswer(examPageUserQuestion.getAnswer());
                            examPageUserAnswer.setCreateTime(DateUtil.date());
                            examPageUserAnswerList.add(examPageUserAnswer);

                            if (question.getIsForm() == 1){
                                examPageUserQuestionFormTitleList.addAll(getQuestionFormTitle(question.getId(),examPageUser.getUserId(),examPageUser.getExamId(),examPageUserQuestion.getId()));
                                examPageUserQuestionChildList.addAll(getQuestionChild(question,examPageUser.getUserId(),examPageUser.getExamId(),examPageUserQuestion.getId(),examPageUserQuestionFormTitleList));
                            }
                            num++;
                            if(question.getNeedCorrect() == 1){
                                needCorrect = true;
                            }
                        }
                    }
                    saveBatch(examPageUserQuestionList);
                    examPageUserAnswerService.saveBatch(examPageUserAnswerList);

                    if (!examPageUserQuestionChildList.isEmpty()) {

                        List<ExamPageUserChildAnswer> examPageUserChildAnswerList = getQuestionChildAnswer(examPageUserQuestionChildList);
                        examPageUserQuestionFormTitleService.saveBatch(examPageUserQuestionFormTitleList);
                        examPageUserQuestionChildService.saveBatch(examPageUserQuestionChildList);
                        examPageUserChildAnswerService.saveBatch(examPageUserChildAnswerList);
                    }

                    examPageUser.setSelectStatus(2);
                    examPageUser.setNeedCorrect(needCorrect?1:0);
                    examPageUserService.updateById(examPageUser);
                }catch (Exception e){
                    continue;
                }
            }
        }
        examExpertAssignmentService.assignExamToExperts(exam.getId());
        redisUtil.set(token, "1", 2, TimeUnit.HOURS);
    }

    public List<ExamPageUserQuestionFormTitle> getQuestionFormTitle(Integer questionId,Integer userId,Integer examId,String pid) {
        List<QuestionFormTitle> questionFormTitles = questionFormTitleService.lambdaQuery().eq(QuestionFormTitle::getPid, questionId).list();
        return questionFormTitles.stream().map(questionFormTitle -> {
            ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle = new ExamPageUserQuestionFormTitle();
            BeanUtil.copyProperties(questionFormTitle, examPageUserQuestionFormTitle);
            examPageUserQuestionFormTitle.setId(IdUtil.getSnowflakeNextIdStr());
            examPageUserQuestionFormTitle.setUserId(userId);
            examPageUserQuestionFormTitle.setExamId(examId);
            examPageUserQuestionFormTitle.setPid(pid);
            examPageUserQuestionFormTitle.setOldQuestionTitle(questionFormTitle.getId());
            return examPageUserQuestionFormTitle;
        }).collect(Collectors.toList());
    }

    public List<ExamPageUserQuestionChild> getQuestionChild(Question question,Integer userId,Integer examId,String examQuestionId,List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList) {
        List<QuestionChild> questionChildList = questionChildService.lambdaQuery().eq(QuestionChild::getQuestionId, question.getId()).list();
        Map<Integer,List<QuestionChild>> questionChildMap = questionChildList
                .stream()
                .collect(Collectors.groupingBy(QuestionChild::getPid));
        List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
        if (examPageUserQuestionFormTitleList.size() > 0){
            BigDecimal scoreChild = questionChildList.size() == 0?BigDecimal.ZERO: NumberUtil.div(new BigDecimal(question.getScore()),new BigDecimal(questionChildList.size()));
            for (ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle : examPageUserQuestionFormTitleList) {
                ExamPageUserQuestionChild examPageUserQuestionChild = new ExamPageUserQuestionChild();
                List<QuestionChild> questionChildThisTitlePidList = new ArrayList<>();
                questionChildThisTitlePidList = questionChildMap.get(examPageUserQuestionFormTitle.getOldQuestionTitle());
                if (questionChildThisTitlePidList != null && questionChildThisTitlePidList.size() > 0){
                    for (QuestionChild questionChild : questionChildThisTitlePidList) {
                        examPageUserQuestionChild = BeanUtil.copyProperties(questionChild, ExamPageUserQuestionChild.class);
                        examPageUserQuestionChild.setId(IdUtil.getSnowflakeNextIdStr());
                        examPageUserQuestionChild.setUserId(userId);
                        examPageUserQuestionChild.setExamId(examId);
                        examPageUserQuestionChild.setScore(scoreChild);
                        examPageUserQuestionChild.setPid(examPageUserQuestionFormTitle.getId());
                        examPageUserQuestionChild.setQuestionId(examQuestionId);
                        examPageUserQuestionChild.setNeedCorrect(question.getNeedCorrect());
                        examPageUserQuestionChildList.add(examPageUserQuestionChild);
                    }
                }
            }
        }
        return examPageUserQuestionChildList;
    }

    public List<ExamPageUserChildAnswer> getQuestionChildAnswer(List<ExamPageUserQuestionChild> examPageUserQuestionChildList){
        List<ExamPageUserChildAnswer> list = new ArrayList<>();
        for (ExamPageUserQuestionChild examPageUserQuestionChild : examPageUserQuestionChildList) {
            ExamPageUserChildAnswer examPageUserChildAnswer = BeanUtil.copyProperties(examPageUserQuestionChild, ExamPageUserChildAnswer.class);
            examPageUserChildAnswer.setId(IdUtil.getSnowflakeNextIdStr());
            examPageUserChildAnswer.setQuestionChildId(examPageUserQuestionChild.getId());
            examPageUserChildAnswer.setRightAnswer(examPageUserQuestionChild.getAnswer());
            list.add(examPageUserChildAnswer);
        }
        return list;
    }

    
    @Transactional
    @Override
    public BaseResponse<Map<String,Integer>> getProgress(String examId,String token) {

        List<Integer> classList = examClassService.lambdaQuery().eq(ExamClass::getExamId, examId).list().stream().map(ExamClass::getClassId).collect(Collectors.toList());
        Long count0 = userService.lambdaQuery().in(User::getClassId, classList).eq(User::getIsDel, 0).count();
        Map<String,Integer> map = new HashMap<>();
        map.put("all",count0.intValue());
        Long count1 = examPageUserService.lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getSelectStatus, 1).count();
        map.put("ing",count1.intValue());
        Long count2 = examPageUserService.lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getSelectStatus, 2).count();
        map.put("finish",count2.intValue());
        String isOver = redisUtil.get(token);
        if(StrUtil.isNotBlank(isOver)){
            map.put("isOver", Integer.valueOf(isOver));
            if ("1".equals(isOver)){
                examService.lambdaUpdate()
                        .eq(Exam::getId,examId)
                        .set(Exam::getRollMakeOver,1)
                        .update();
            }
        }else{
            map.put("isOver",0);
        }
        return BaseResponse.ok("成功",map);
    }

    public Map<Integer,List<Question>> getQuestionMap(Exam exam,Map<Integer,List<ExamPageSetType>> examPageSetPointMap){
        Map<Integer,List<Question>> questionMap = new HashMap<>();
        List<String> symbolList = Arrays.asList(exam.getSymbol().split(","));
        for (Integer integer : examPageSetPointMap.keySet()) {
            List<ExamPageSetType> examPageSetPointList = examPageSetPointMap.get(integer);
            List<Question> questionList = new ArrayList<>();
            for (ExamPageSetType examPageSetPoint : examPageSetPointList) {

                questionList.addAll(questionMapper.getRandQuestion(examPageSetPoint,exam.getDifficultyLevel(),symbolList));
            }
            questionMap.put(integer,questionList);
        }
        return questionMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public int getQuestionNum(Set<Integer> set,int max){
        boolean setOk = false;
        int r;
        do {
            r = RandomUtil.randomInt(1,max+1);
            if (set.add(r)){

                setOk = true;
            }
        } while (!setOk);
        return r;
    }
}
