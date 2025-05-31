package com.xinkao.erp.exercise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.param.*;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.query.InstantFeedbacksQuery;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
import com.xinkao.erp.exercise.utils.MarkQuestionUtils;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.service.QuestionService;
import com.xinkao.erp.question.vo.QuestionExercisePageVo;
import com.xinkao.erp.question.vo.QuestionPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 练习操作题记录表
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
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


    /**
     * 开始练习并返回第一道题的信息
     * @param id 模块编号
     *               题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     * */
    @GetMapping("/start/{id}")
    @ApiOperation("开始操作题练习,返回题目信息")
    @PrimaryDataSource
    public BaseResponse<?> start(@PathVariable Integer id, HttpServletRequest request) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        //根据id获取题目信息
        Question question = questionService.getById(id);
        InstantFeedbacks  feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("question_id", question.getId()).eq("user_id", loginUserAll.getUser().getId()).eq("record_id", 0));
        if (feedbacks == null){
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(0);
            feedbacks.setQuestionId(question.getId());
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
    @ApiOperation("保存单个题答案")
    @PrimaryDataSource
    public BaseResponse<?> saveAnswer(@RequestBody SubmitOperateParam submitParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer questionId = submitParam.getQuestionId();
        String userAnswer = submitParam.getUserAnswer();
        //根据题号找到当前题并获取答案
        Question question = questionService.getById(questionId);
        //根据questionId和exerciseRecordsId找到InstantFeedbacks的记录，要是不存在就创建
        InstantFeedbacks feedbacks = instantFeedbacksService.getOne(
                new QueryWrapper<InstantFeedbacks>().eq("user_id", loginUserAll.getUser().getId()).eq("question_id", questionId));
        if (feedbacks == null) {
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(0);
            feedbacks.setQuestionId(question.getId());
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
                //正确
                feedbacks.setIsCorrect(1);
            }else if (score == 0) {
                //错误
                feedbacks.setIsCorrect(0);
            }else if (score > 0&& score < 5) {
                //部分正确
                feedbacks.setIsCorrect(2);
            }
        }
        feedbacks.setUserAnswer(userAnswer);
        feedbacks.setUserScore(score);
        feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        feedbacks.setUpdateTime(new Date());
        //计算createTime和updateTime的时间差多少秒
        long time = feedbacks.getUpdateTime().getTime() - feedbacks.getCreateTime().getTime();
        feedbacks.setOperationDuration(time / 1000);
        instantFeedbacksService.updateById(feedbacks);
        return BaseResponse.ok(feedbacks);
    }

    @PostMapping("/submit")
    @ApiOperation("提交单个题答案")
    @PrimaryDataSource
    public BaseResponse<?> submit(@RequestBody SubmitParam submitParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer questionId = submitParam.getQuestionId();
        String userAnswer = submitParam.getUserAnswer();
        //根据题号找到当前题并获取答案
        Question question = questionService.getById(questionId);
        //根据questionId和exerciseRecordsId找到InstantFeedbacks的记录，要是不存在就创建
        InstantFeedbacks feedbacks = instantFeedbacksService.getOne(
                new QueryWrapper<InstantFeedbacks>().eq("user_id", loginUserAll.getUser().getId()).eq("question_id", questionId));
        if (feedbacks == null) {
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(0);
            feedbacks.setQuestionId(question.getId());
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
                //正确
                feedbacks.setIsCorrect(1);
            }else if (score == 0) {
                //错误
                feedbacks.setIsCorrect(0);
            }else if (score > 0&& score < 5) {
                //部分正确
                feedbacks.setIsCorrect(2);
            }
        }
        feedbacks.setUserAnswer(userAnswer);
        feedbacks.setUserScore(score);
        feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        feedbacks.setUpdateTime(new Date());
        //计算createTime和updateTime的时间差多少秒
        long time = feedbacks.getUpdateTime().getTime() - feedbacks.getCreateTime().getTime();
        feedbacks.setOperationDuration(time / 1000);
        feedbacks.setFinishedState(2);
        instantFeedbacksService.updateById(feedbacks);
        return BaseResponse.ok(feedbacks);
    }


    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询练习操作的题目")
    public BaseResponse<Page<QuestionExercisePageVo>> page(@Valid @RequestBody QuestionQuery query) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Pageable pageable = query.getPageInfo();
        //判断当前用户是不是学生
        if (loginUserAll.getUser().getRoleId() != 3) {
            return BaseResponse.fail("只有学生才能查看");
        }
        query.setClassId(loginUserAll.getUser().getClassId());
        query.setForExercise(1);
        Page<QuestionExercisePageVo> voPage = questionService.page1(query, pageable);
        voPage.getRecords().forEach(item -> {
            InstantFeedbacks  feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("question_id", item.getId()).eq("user_id", loginUserAll.getUser().getId()).eq("record_id", 0));
            //练习状态:0未开始，1进行中，2已完成
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

    /**
     * 分页查询统计
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page_summary")
    @ApiOperation("分页查询已经完成练习操作的题目数据")
    public BaseResponse<Page<InstantFeedbacks>> pageSummary(@Valid @RequestBody InstantFeedbacksQuery query) {
        //获取当前用户
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        query.setUserId(loginUserAll.getUser().getId());
        query.setShape(500);
        Pageable pageable = query.getPageInfo();
        Page<InstantFeedbacks> voPage = instantFeedbacksService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

}
