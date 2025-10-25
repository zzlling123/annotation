package com.xinkao.erp.exercise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.param.*;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
import com.xinkao.erp.exercise.utils.MarkQuestionUtils;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.service.QuestionService;
import com.xinkao.erp.question.vo.QuestionExercisePageVo;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/exercise-operate")
public class ExerciseOperateController {

    @Autowired
    private ExerciseRecordsService exerciseRecordsService;
    @Autowired
    private QuestionService questionService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private InstantFeedbacksService instantFeedbacksService;
    @Autowired
    private MarkQuestionUtils markQuestionUtils;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private UserService userService;


    @GetMapping("/start/{id}")
    @PrimaryDataSource
    public BaseResponse<?> start(@PathVariable Integer id, HttpServletRequest request) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Question question = questionService.getById(id);
        InstantFeedbacks  feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("question_id", question.getId()).eq("user_id", loginUserAll.getUser().getId()).eq("record_id", 0));
        if (feedbacks == null){
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(0);
            feedbacks.setQuestionId(question.getId());
            feedbacks.setType(question.getType());
            feedbacks.setShape(question.getShape());
            feedbacks.setCreateBy(loginUserAll.getUser().getRealName());
            feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
            feedbacks.setCorrectAnswer(question.getAnswer());
            feedbacks.setUserId(loginUserAll.getUser().getId());
            feedbacks.setCreateTime(new Date());
            feedbacks.setFinishedState(1);
            instantFeedbacksService.save(feedbacks);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("question", question);
        map.put("feedbacks", feedbacks);
        return BaseResponse.ok(map);
    }


    @PostMapping("/saveAnswer")
    @PrimaryDataSource
    public BaseResponse<?> saveAnswer(@RequestBody SubmitOperateParam submitParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer questionId = submitParam.getQuestionId();
        String userAnswer = submitParam.getUserAnswer();
        Question question = questionService.getById(questionId);
        InstantFeedbacks feedbacks = instantFeedbacksService.getOne(
                new QueryWrapper<InstantFeedbacks>().eq("user_id", loginUserAll.getUser().getId()).eq("question_id", questionId));
        if (feedbacks == null) {
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(0);
            feedbacks.setQuestionId(question.getId());
            feedbacks.setType(question.getType());
            feedbacks.setShape(question.getShape());
            feedbacks.setCreateBy(loginUserAll.getUser().getRealName());
            feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
            feedbacks.setCorrectAnswer(question.getAnswer());
            feedbacks.setUserId(loginUserAll.getUser().getId());
            feedbacks.setCreateTime(new Date());
            feedbacks.setFinishedState(1);
            instantFeedbacksService.save(feedbacks);
        }
        feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("user_id", loginUserAll.getUser().getId()).eq("question_id", questionId));
        Integer feedbacksId = feedbacks.getId();
        String answer = feedbacks.getCorrectAnswer();
        int score = 0;
        if(question.getShape() == 500){
            PanJuanParam panJuanParam = markQuestionUtils.checkAnswerCaoZuo(userAnswer, answer, question.getShape(), 5, question.getType());
            feedbacks.setIsCorrect(panJuanParam.getIsCorrect());
            feedbacks.setBiao(panJuanParam.getBiao());
            feedbacks.setCuo(panJuanParam.getCuo());
            feedbacks.setWu(panJuanParam.getWu());
            feedbacks.setShu(panJuanParam.getShu());
            feedbacks.setZong(panJuanParam.getZong());
            feedbacks.setDa(panJuanParam.getDa());
            feedbacks.setAccuracyRate(panJuanParam.getAccuracyRate());
            feedbacks.setCoverageRate(panJuanParam.getCoverageRate());
            feedbacks.setOperationDuration(panJuanParam.getOperationDuration());
            score = panJuanParam.getCoverageRate().multiply(new BigDecimal(5)).setScale(0, RoundingMode.HALF_UP).intValueExact();
        }else {
            score = markQuestionUtils.checkAnswer(userAnswer, answer, question.getShape(),5,question.getType());
            if (score == 5) {
                feedbacks.setIsCorrect(1);
            }else if (score == 0) {
                feedbacks.setIsCorrect(0);
            }else if (score > 0&& score < 5) {
                feedbacks.setIsCorrect(2);
            }
        }
        feedbacks.setUserAnswer(userAnswer);
        feedbacks.setUserScore(score);
        feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        feedbacks.setUpdateTime(new Date());
        long time = feedbacks.getUpdateTime().getTime() - feedbacks.getCreateTime().getTime();
        feedbacks.setOperationDuration(time / 1000);
        feedbacks.setFinishedState(1);
        instantFeedbacksService.updateById(feedbacks);
        return BaseResponse.ok(feedbacks);
    }

    @PostMapping("/submit")
    @PrimaryDataSource
    public BaseResponse<?> submit(@RequestBody SubmitParam submitParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer questionId = submitParam.getQuestionId();
        String userAnswer = submitParam.getUserAnswer();
        Question question = questionService.getById(questionId);
        InstantFeedbacks feedbacks = instantFeedbacksService.getOne(
                new QueryWrapper<InstantFeedbacks>().eq("user_id", loginUserAll.getUser().getId()).eq("question_id", questionId));
        if (feedbacks == null) {
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(0);
            feedbacks.setQuestionId(question.getId());
            feedbacks.setType(question.getType());
            feedbacks.setShape(question.getShape());
            feedbacks.setCreateBy(loginUserAll.getUser().getRealName());
            feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
            feedbacks.setCorrectAnswer(question.getAnswer());
            feedbacks.setUserId(loginUserAll.getUser().getId());
            feedbacks.setCreateTime(new Date());
            feedbacks.setFinishedState(1);
            instantFeedbacksService.save(feedbacks);
        }
        feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("user_id", loginUserAll.getUser().getId()).eq("question_id", questionId));
        Integer feedbacksId = feedbacks.getId();
        String answer = feedbacks.getCorrectAnswer();
        int score = 0;
        if(question.getShape() == 500){
            PanJuanParam panJuanParam = markQuestionUtils.checkAnswerCaoZuo(userAnswer, answer, question.getShape(), 5, question.getType());
            feedbacks.setIsCorrect(panJuanParam.getIsCorrect());
            feedbacks.setBiao(panJuanParam.getBiao());
            feedbacks.setCuo(panJuanParam.getCuo());
            feedbacks.setWu(panJuanParam.getWu());
            feedbacks.setShu(panJuanParam.getShu());
            feedbacks.setZong(panJuanParam.getZong());
            feedbacks.setDa(panJuanParam.getDa());
            feedbacks.setAccuracyRate(panJuanParam.getAccuracyRate());
            feedbacks.setCoverageRate(panJuanParam.getCoverageRate());
            feedbacks.setOperationDuration(panJuanParam.getOperationDuration());
            score = panJuanParam.getCoverageRate().multiply(new BigDecimal(5)).setScale(0, RoundingMode.HALF_UP).intValueExact();
        }else {
            score = markQuestionUtils.checkAnswer(userAnswer, answer, question.getShape(),5,question.getType());
            if (score == 5) {
                feedbacks.setIsCorrect(1);
            }else if (score == 0) {
                feedbacks.setIsCorrect(0);
            }else if (score > 0&& score < 5) {
                feedbacks.setIsCorrect(2);
            }
        }
        feedbacks.setUserAnswer(userAnswer);
        feedbacks.setUserScore(score);
        feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        feedbacks.setUpdateTime(new Date());
        long time = feedbacks.getUpdateTime().getTime() - feedbacks.getCreateTime().getTime();
        feedbacks.setOperationDuration(time / 1000);
        feedbacks.setFinishedState(2);
        instantFeedbacksService.updateById(feedbacks);
        return BaseResponse.ok(feedbacks);
    }


    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<QuestionExercisePageVo>> page(@Valid @RequestBody QuestionQuery query) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Pageable pageable = query.getPageInfo();
        if (loginUserAll.getUser().getRoleId() != 3) {
            return BaseResponse.fail("只有学生才能查看");
        }
        query.setClassId(loginUserAll.getUser().getClassId());
        query.setForExercise(1);
        Page<QuestionExercisePageVo> voPage = questionService.page1(query, pageable);
        voPage.getRecords().forEach(item -> {
            InstantFeedbacks  feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("question_id", item.getId()).eq("user_id", loginUserAll.getUser().getId()).eq("record_id", 0));
            if (feedbacks == null){
                item.setExerciseState(0);
            }else if (feedbacks.getFinishedState() == null || feedbacks.getFinishedState() == 0){
                item.setExerciseState(0);
            }else if (feedbacks.getFinishedState() == 1){
                item.setExerciseState(1);
            }else if (feedbacks.getFinishedState() == 2){
                item.setExerciseState(2);
            }
        });
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/page_summary")
    public BaseResponse<Page<InstantFeedbacks>> pageSummary(@Valid @RequestBody InstantFeedbacksQuery query) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        List<Integer> stuIds = new ArrayList<>();
        if (loginUserAll.getUser().getRoleId()==1){
        }else if (loginUserAll.getUser().getRoleId()==3){
            stuIds.add(loginUserAll.getUser().getId());
        }else if (loginUserAll.getUser().getRoleId()==2){
            List<ClassInfo> classInfoList = classInfoService.lambdaQuery().eq(ClassInfo::getDirectorId, loginUserAll.getUser().getId()).eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list();
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getIsDel, CommonEnum.IS_DEL.NO.getCode());
            wrapper.in(User::getClassId, classInfoList.stream().map(ClassInfo::getId).collect(Collectors.toList()));
            List<User> userList = userService.list(wrapper);
            stuIds = userList.stream().map(User::getId).collect(Collectors.toList());
        }
        query.setUserId(stuIds);
        query.setShape(500);
        Pageable pageable = query.getPageInfo();
        Page<InstantFeedbacks> voPage = instantFeedbacksService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

}
