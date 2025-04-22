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
import com.xinkao.erp.question.mapper.QuestionMapper;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
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
    private ExamPageUserLogService examPageUserLogService;
    @Resource
    private ExamPageSetTypeService examPageSetTypeService;
    @Resource
    private ExamPageUserAnswerService examPageUserAnswerService;
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
        //移除之前的数据
        examPageUserService.lambdaUpdate()
                .eq(ExamPageUser::getExamId,examPageSet.getExamId())
                .remove();
        examPageUserLogService.lambdaUpdate()
                .eq(ExamPageUserLog::getExamId,examPageSet.getExamId())
                .remove();
        //首先清除可能存在生成失败的无用数据
        lambdaUpdate().eq(ExamPageUserQuestion::getExamId,examPageSet.getExamId()).remove();
        //清除答题卡数据
        examPageUserAnswerService.lambdaUpdate().eq(ExamPageUserAnswer::getExamId,examPageSet.getExamId()).remove();
        //查询题型分布设置中是否包含主观题，如果包含，则ExamPageUser中needCorrect为1，否则为0
        boolean needCorrect = examPageSetTypeService.lambdaQuery()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .eq(ExamPageSetType::getShape,400)
                .count() > 0;
        for (User user : userList) {
            //查询是否已生成过
            ExamPageUser examPageUser = new  ExamPageUser();
            examPageUser.setUserId(user.getId());
            examPageUser.setClassId(user.getClassId());
            examPageUser.setExamId(examPageSet.getExamId());
            examPageUser.setSelectStatus(1);
            examPageUser.setCreateTime(DateUtil.date());
            examPageUser.setNeedCorrect(needCorrect?1:0);
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
        Map<Integer,List<ExamPageSetType>> examPageSetTypeMap = examPageSetTypeService.lambdaQuery()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .orderByAsc(ExamPageSetType::getShape)
                .list().stream().collect(Collectors.groupingBy(ExamPageSetType::getShape));
        //如果是0-同题同序 1-同题不同序则先获取一套试题
        if (0 == examPageSet.getPageMode()|| 1 == examPageSet.getPageMode()) {
            //根据知识点获取随机题目
            Map<Integer,List<Question>> questionMap = getQuestionMap(examPageSetTypeMap);
            //如果为0则进行循环插入
            if (examPageSet.getPageMode() == 0) {
                for (ExamPageUser examPageUser : examPageUserList) {
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
                                examPageUserQuestion.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserQuestion.setOldQuestionId(question.getId());
                                examPageUserQuestion.setNum(String.valueOf(num+1));
                                examPageUserQuestion.setNumSort(num+1);
                                examPageUserQuestion.setCreateTime(DateUtil.date());
                                save(examPageUserQuestion);
                                //生成用户答案模板
                                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                                BeanUtil.copyProperties(examPageUserQuestion,examPageUserAnswer);
                                examPageUserAnswer.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                                examPageUserAnswer.setRightAnswer(question.getAnswer());
                                examPageUserAnswer.setCreateTime(DateUtil.date());
                                examPageUserAnswerService.save(examPageUserAnswer);
                                num++;
                            }
                        }
                        //修改该考生生成试卷状态为完成
                        examPageUser.setSelectStatus(2);
                        examPageUserService.updateById(examPageUser);
                    }catch (Exception e){
                        continue;
                    }
                }
            }else {
                //如果为1则生成题目列表后进行随机排列题号后进行插入
                for (ExamPageUser examPageUser : examPageUserList) {
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
                                examPageUserQuestion.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserQuestion.setOldQuestionId(question.getId());
                                examPageUserQuestion.setNum(String.valueOf(num));
                                examPageUserQuestion.setNumSort(num);
                                examPageUserQuestion.setCreateTime(DateUtil.date());
                                save(examPageUserQuestion);
                                //生成用户答案模板
                                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                                BeanUtil.copyProperties(examPageUserQuestion,examPageUserAnswer);
                                examPageUserAnswer.setId(null);
                                examPageUserQuestion.setExamId(examPageUser.getExamId());
                                examPageUserQuestion.setUserId(examPageUser.getUserId());
                                examPageUserAnswer.setQuestionId(examPageUserQuestion.getId());
                                examPageUserAnswer.setRightAnswer(examPageUserQuestion.getAnswer());
                                examPageUserAnswer.setCreateTime(DateUtil.date());
                                examPageUserAnswerService.save(examPageUserAnswer);
                            }
                        }
                        //修改该考生生成试卷状态为完成
                        examPageUser.setSelectStatus(2);
                        examPageUserService.updateById(examPageUser);
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }else{
            //如果为2则每个个学生都是单独生成一套试卷
            for (ExamPageUser examPageUser : examPageUserList) {
                try{
                    //根据知识点获取随即题目
                    Map<Integer,List<Question>> questionMap = getQuestionMap(examPageSetTypeMap);
                    //修改该考生生成试卷状态为生成中
                    examPageUser.setSelectStatus(1);
                    examPageUserService.updateById(examPageUser);
                    int num = 0;
                    List<ExamPageUserQuestion> examPageUserQuestionList = new ArrayList<>();
                    List<ExamPageUserAnswer> examPageUserAnswerList = new ArrayList<>();
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
                            if(examPageUserAnswer.getShape() == 400){
                                //如果是主观题则为需要批改
                                examPageUserAnswer.setNeedCorrect(1);
                            }
                            examPageUserAnswerList.add(examPageUserAnswer);
                            num++;
                        }
                    }
                    saveBatch(examPageUserQuestionList);
                    examPageUserAnswerService.saveBatch(examPageUserAnswerList);
                    //修改该考生生成试卷状态为完成
                    examPageUser.setSelectStatus(2);
                    examPageUserService.updateById(examPageUser);
                }catch (Exception e){
                    continue;
                }
            }
        }
        redisUtil.set(token, "1", 2, TimeUnit.HOURS);
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
        }else{
            map.put("isOver",0);
        }
        return BaseResponse.ok("成功",map);
    }

    public Map<Integer,List<Question>> getQuestionMap(Map<Integer,List<ExamPageSetType>> examPageSetPointMap){
        Map<Integer,List<Question>> questionMap = new HashMap<>();
        for (Integer integer : examPageSetPointMap.keySet()) {
            List<ExamPageSetType> examPageSetPointList = examPageSetPointMap.get(integer);
            List<Question> questionList = new ArrayList<>();
            for (ExamPageSetType examPageSetPoint : examPageSetPointList) {
                //获取该知识点的随机抽取题目
                questionList.addAll(questionMapper.getRandQuestion(examPageSetPoint));
            }
            questionMap.put(integer,questionList);
        }
        return questionMap;
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
