package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 试卷表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:06:02
 */
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

    //三种试卷格式：0-同题同序 1-同题不同序 2-不同题不同序
    /**
     * 制卷<br>
     * 同题同序：
     * 	按照设置随机获取题目，生成一套试卷题号，循环插入
     * 同题不同序：
     * 	按照设置随机获取题目，按照单选多选判断流程，将题目中的第几个和题号进行随机数值占用插入，循环生成
     * 不同题不同序
     * 	按照此次科目涉及的人，循环抽题
     * */
    @Override
    public void rollMaking(ExamPageSet examPageSet, List<User> userList, String token) {
        //想将考生添加至exam_page_stu表，方便后期查询进度
        List<ExamPageUser> examPageUserList = new ArrayList<>();
        List<ExamPageUserLog> examPageUserLogList = new ArrayList<>();
        Exam exam = examService.getById(examPageSet.getExamId());
        //移除之前的数据
        examPageUserService.lambdaUpdate()
                .eq(ExamPageUser::getExamId,examPageSet.getExamId())
                .remove();
        examPageUserLogService.lambdaUpdate()
                .eq(ExamPageUserLog::getExamId,examPageSet.getExamId())
                .remove();
        //首先清除可能存在生成失败的无用数据
        lambdaUpdate().eq(ExamPageUserQuestion::getExamId,examPageSet.getExamId()).remove();
        examPageUserQuestionFormTitleService.lambdaUpdate().eq(ExamPageUserQuestionFormTitle::getExamId,examPageSet.getExamId()).remove();
        examPageUserQuestionChildService.lambdaUpdate().eq(ExamPageUserQuestionChild::getExamId,examPageSet.getExamId()).remove();
        examPageUserChildAnswerService.lambdaUpdate().eq(ExamPageUserChildAnswer::getExamId,examPageSet.getExamId()).remove();
        //清除答题卡数据
        examPageUserAnswerService.lambdaUpdate().eq(ExamPageUserAnswer::getExamId,examPageSet.getExamId()).remove();
        for (User user : userList) {
            //查询是否已生成过
            ExamPageUser examPageUser = new  ExamPageUser();
            examPageUser.setUserId(user.getId());
            examPageUser.setClassId(user.getClassId());
            examPageUser.setExamId(examPageSet.getExamId());
            examPageUser.setSelectStatus(1);
            examPageUser.setCreateTime(DateUtil.date());
            examPageUserList.add(examPageUser);

            //创建答题心跳记录备用
            ExamPageUserLog examPageUserLog = new ExamPageUserLog();
            BeanUtil.copyProperties(examPageUser, examPageUserLog);
            examPageUserLog.setId(null);
            examPageUserLog.setCreateTime(DateUtil.date());
            examPageUserLogList.add(examPageUserLog);
        }
        examPageUserService.saveBatch(examPageUserList);
        examPageUserLogService.saveBatch(examPageUserLogList);
        //获取设置中的知识点列表
        List<ExamPageSetType> examPageSetTypeList = examPageSetTypeService.lambdaQuery()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .orderByAsc(ExamPageSetType::getShape)
                .list();
        //按照之前shape的排列顺序分组成map格式
        Map<Integer,List<ExamPageSetType>> examPageSetTypeMap = examPageSetTypeList.stream().collect(Collectors.groupingBy(ExamPageSetType::getShape));

        //如果是0-同题同序 1-同题不同序则先获取一套试题
        if (0 == examPageSet.getPageMode()|| 1 == examPageSet.getPageMode()) {
            //根据知识点获取随机题目
            Map<Integer,List<Question>> questionMap = getQuestionMap(exam,examPageSetTypeMap);
            //如果为0则进行循环插入
            if (examPageSet.getPageMode() == 0) {
                for (ExamPageUser examPageUser : examPageUserList) {
                    List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                    List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = new ArrayList<>();
                    List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
                    List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();
                    //查询题型分布设置中是否包含主观题，如果包含，则ExamPageUser中needCorrect为1，否则为0
                    boolean needCorrect = false;
                    //修改该考生生成试卷状态为生成中
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

                                //生成用户答案模板
                                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                                BeanUtil.copyProperties(examPageUserQuestion,examPageUserAnswer);
                                examPageUserAnswer.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                                examPageUserAnswer.setRightAnswer(question.getAnswer());
                                examPageUserAnswer.setCreateTime(DateUtil.date());
                                examPageUserAnswerList.add(examPageUserAnswer);

                                //如果题目为题目单，则添加相关表
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
                        //如果包含题目单则执行相关添加
                        if (!examPageUserQuestionChildList.isEmpty()) {
                            //添加答案的子表
                            List<ExamPageUserChildAnswer> examPageUserChildAnswerList = getQuestionChildAnswer(examPageUserQuestionChildList);
                            examPageUserQuestionFormTitleService.saveBatch(examPageUserQuestionFormTitleList);
                            examPageUserQuestionChildService.saveBatch(examPageUserQuestionChildList);
                            examPageUserChildAnswerService.saveBatch(examPageUserChildAnswerList);
                        }
                        //修改该考生生成试卷状态为完成
                        examPageUser.setSelectStatus(2);
                        examPageUser.setNeedCorrect(needCorrect?1:0);
                        examPageUserService.updateById(examPageUser);
                    }catch (Exception e){
                        continue;
                    }
                }
            }else {
                //如果为1则生成题目列表后进行随机排列题号后进行插入
                for (ExamPageUser examPageUser : examPageUserList) {
                    List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                    List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = new ArrayList<>();
                    List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
                    List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();
                    //查询题型分布设置中是否包含主观题，如果包含，则ExamPageUser中needCorrect为1，否则为0
                    boolean needCorrect = false;
                    try{
                        //修改该考生生成试卷状态为生成中
                        examPageUser.setSelectStatus(1);
                        examPageUserService.updateById(examPageUser);
                        int allNum = 0;
                        Set<Integer> numSet = new HashSet<>();
                        for (Integer integer : questionMap.keySet()) {
                            List<Question> questionList = questionMap.get(integer);
                            allNum += questionList.size();
                            for (int i = 0;i < questionList.size();i++) {
                                //获取随机题号
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
                                //生成用户答案模板
                                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                                BeanUtil.copyProperties(examPageUserQuestion,examPageUserAnswer);
                                examPageUserAnswer.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                                examPageUserAnswer.setRightAnswer(examPageUserQuestion.getAnswer());
                                examPageUserAnswer.setCreateTime(DateUtil.date());
                                examPageUserAnswerList.add(examPageUserAnswer);

                                //如果题目为题目单，则添加相关表
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
                        //如果包含题目单则执行相关添加
                        if (!examPageUserQuestionChildList.isEmpty()) {
                            //添加答案的子表
                            List<ExamPageUserChildAnswer> examPageUserChildAnswerList = getQuestionChildAnswer(examPageUserQuestionChildList);
                            examPageUserQuestionFormTitleService.saveBatch(examPageUserQuestionFormTitleList);
                            examPageUserQuestionChildService.saveBatch(examPageUserQuestionChildList);
                            examPageUserChildAnswerService.saveBatch(examPageUserChildAnswerList);
                        }
                        //修改该考生生成试卷状态为完成
                        examPageUser.setSelectStatus(2);
                        examPageUser.setNeedCorrect(needCorrect?1:0);
                        examPageUserService.updateById(examPageUser);
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }else{
            //如果为2则每个个学生都是单独生成一套试卷
            for (ExamPageUser examPageUser : examPageUserList) {
                List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = new ArrayList<>();
                List<ExamPageUserQuestionChild> examPageUserQuestionChildList = new ArrayList<>();
                List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();
                try{
                    //根据知识点获取随即题目
                    Map<Integer,List<Question>> questionMap = getQuestionMap(exam,examPageSetTypeMap);
                    //修改该考生生成试卷状态为生成中
                    examPageUser.setSelectStatus(1);
                    examPageUserService.updateById(examPageUser);
                    int num = 0;
                    //查询题型分布设置中是否包含主观题，如果包含，则ExamPageUser中needCorrect为1，否则为0
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
                            //生成用户答案模板
                            ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                            BeanUtil.copyProperties(examPageUserQuestion, examPageUserAnswer);
                            examPageUserAnswer.setId(null);
                            examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                            examPageUserAnswer.setRightAnswer(examPageUserQuestion.getAnswer());
                            examPageUserAnswer.setCreateTime(DateUtil.date());
                            examPageUserAnswerList.add(examPageUserAnswer);
                            //如果题目为题目单，则添加相关表
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
                    //如果包含题目单则执行相关添加
                    if (!examPageUserQuestionChildList.isEmpty()) {
                        //添加答案的子表
                        List<ExamPageUserChildAnswer> examPageUserChildAnswerList = getQuestionChildAnswer(examPageUserQuestionChildList);
                        examPageUserQuestionFormTitleService.saveBatch(examPageUserQuestionFormTitleList);
                        examPageUserQuestionChildService.saveBatch(examPageUserQuestionChildList);
                        examPageUserChildAnswerService.saveBatch(examPageUserChildAnswerList);
                    }
                    //修改该考生生成试卷状态为完成
                    examPageUser.setSelectStatus(2);
                    examPageUser.setNeedCorrect(needCorrect?1:0);
                    examPageUserService.updateById(examPageUser);
                }catch (Exception e){
                    continue;
                }
            }
        }
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
        Integer scoreChild = Integer.parseInt(question.getScore())/questionChildList.size();
        for (ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle : examPageUserQuestionFormTitleList) {
            ExamPageUserQuestionChild examPageUserQuestionChild = new ExamPageUserQuestionChild();
            List<QuestionChild> questionChildThisTitlePidList = questionChildMap.get(examPageUserQuestionFormTitle.getOldQuestionTitle());
            for (QuestionChild questionChild : questionChildThisTitlePidList) {
                BeanUtil.copyProperties(questionChild, examPageUserQuestionChild);
                examPageUserQuestionChild.setId(IdUtil.getSnowflakeNextIdStr());
                examPageUserQuestionChild.setUserId(userId);
                examPageUserQuestionChild.setExamId(examId);
                examPageUserQuestionChild.setScore(scoreChild);
                examPageUserQuestionChild.setPid(examPageUserQuestionFormTitle.getId());
                examPageUserQuestionChild.setQuestionId(examQuestionId);
                examPageUserQuestionChildList.add(examPageUserQuestionChild);
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

    /**
     * 查询进度
     */
    @Transactional
    @Override
    public BaseResponse<Map<String,Integer>> getProgress(String examId,String token) {
        //获取考试相关班级，然后查询班级下有多少人
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
        for (Integer integer : examPageSetPointMap.keySet()) {
            List<ExamPageSetType> examPageSetPointList = examPageSetPointMap.get(integer);
            List<Question> questionList = new ArrayList<>();
            for (ExamPageSetType examPageSetPoint : examPageSetPointList) {
                //获取该知识点的随机抽取题目
                questionList.addAll(questionMapper.getRandQuestion(examPageSetPoint,exam.getDifficultyLevel(),exam.getSymbol()));
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
            r = RandomUtil.randomInt(1,max+1);//MAX+1
            if (set.add(r)){
                //set不能重复，所以在成功添加进SET之后，附加给这个题号集合
                setOk = true;
            }
        } while (!setOk);
        return r;
    }
}
