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
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.param.CreateParam;
import com.xinkao.erp.exercise.param.PanJuanParam;
import com.xinkao.erp.exercise.param.SubmitAllParam;
import com.xinkao.erp.exercise.param.SubmitParam;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
import com.xinkao.erp.exercise.utils.MarkQuestionUtils;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.service.QuestionService;
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


@RestController
@RequestMapping("/exercise-records")
public class ExerciseRecordsController {

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


    @PostMapping("/create")
    @ApiOperation("选择模块创建练习练习")
    @PrimaryDataSource
    public BaseResponse<?> create( @RequestBody CreateParam createParam) {
        int moduleId = createParam.getModuleId();
        int shape = createParam.getShape();

        ExerciseRecords exerciseRecords = new ExerciseRecords();
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        exerciseRecords.setUserId(loginUserAll.getUser().getId());
        exerciseRecords.setCreateBy(loginUserAll.getUser().getRealName());
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
        exerciseRecords.setCompletionStatus(0);
        exerciseRecords.setScore(0);
        exerciseRecords.setStartTime(java.time.LocalDateTime.now());
        exerciseRecords.setShape(shape);
        exerciseRecords.setModuleId(moduleId);
        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Question::getType, moduleId).eq(Question::getShape, shape);
        List<Question> list_question = questionService.getBaseMapper().selectList(wrapper);
        if (list_question==null ||list_question.size() == 0) {
            return BaseResponse.fail("没有题目");
        }

        String feedback = "" ;
        if (list_question.size() <= 20) {
            for (Question question : list_question) {
                int q_id = question.getId();
                feedback += q_id + ",";
            }
            feedback = feedback.substring(0, feedback.length() - 1);
        }else if (list_question.size() > 20) {
            List<Integer> selectedQuestionIds = new ArrayList<>();
            Collections.shuffle(list_question);
            int limit = Math.min(20, list_question.size());
            for (int i = 0; i < limit; i++) {
                selectedQuestionIds.add(list_question.get(i).getId());
            }
            feedback = String.join(",", selectedQuestionIds.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        exerciseRecords.setFeedback(feedback);
        exerciseRecordsService.save(exerciseRecords);
        return BaseResponse.ok();
    }

    @GetMapping("/detail/{exerciseRecordsId}")
    @ApiOperation("查看练习记录表,list_question是试题集合，instantFeedbacksList是学生的练习记录")
    public BaseResponse<?> detail(@PathVariable Integer exerciseRecordsId) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        if (!loginUserAll.getUser().getId().equals(exerciseRecords.getUserId())) {
            return BaseResponse.fail("您没有权限查看练习记录！");
        }
        if (exerciseRecords == null) {
            return BaseResponse.fail("练习记录不存在！");
        }
        if (exerciseRecords.getIsDel() == 1) {
            return BaseResponse.fail("练习记录不存在！");
        }
        String[] feedback_arr =exerciseRecords.getFeedback().split(",");
        List<String> feedback_list = new ArrayList<>();
        feedback_list.addAll(Arrays.asList(feedback_arr));
        List<InstantFeedbacks> instantFeedbacksList = instantFeedbacksService.list(new LambdaQueryWrapper<InstantFeedbacks>().eq(InstantFeedbacks::getRecordId, exerciseRecordsId).in(InstantFeedbacks::getQuestionId, feedback_list));
        List<Question> list_question = questionService.list(new LambdaQueryWrapper<Question>().in(Question::getId, feedback_list));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("instantFeedbacksList", instantFeedbacksList);
        map.put("exerciseRecords", exerciseRecords);
        map.put("list_question", list_question);
        return BaseResponse.ok(map);
    }

    @GetMapping("/start/{id}")
    @ApiOperation("开始练习并返回所有题的信息，feedbacks_list是练习详情记录，有userAnswer答案是做了，没有答案是没有完成的，list_question所有题的信息。")
    @PrimaryDataSource
    public BaseResponse<?> start(@PathVariable Integer id, HttpServletRequest request) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(id);
        if (exerciseRecords == null){
            return BaseResponse.fail("练习记录不存在！");
        }else if (exerciseRecords.getIsDel() == 1){
            return BaseResponse.fail("练习记录不存在！");
        }
        if (!loginUserAll.getUser().getId().equals(exerciseRecords.getUserId())){
            return BaseResponse.fail("您没有权限开始练习！");
        }
        if(exerciseRecords.getCompletionStatus()==2){

        }else if (exerciseRecords.getCompletionStatus() == 1){
            return BaseResponse.fail("您已经完成了练习！");
        }else if (exerciseRecords.getCompletionStatus() == 0){
            exerciseRecords.setStartTime(java.time.LocalDateTime.now());
            exerciseRecords.setCompletionStatus(2);
        }
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
        exerciseRecordsService.updateById(exerciseRecords);

        String[] feedback_arr = exerciseRecords.getFeedback().split(",");
        List<Question> list_question = new ArrayList<Question>();
        for (int i = 0; i < feedback_arr.length; i++){
            Question question = questionService.getById(feedback_arr[i]);
            list_question.add(question);
        }
        List<InstantFeedbacks> feedbacks_list = instantFeedbacksService.list(new QueryWrapper<InstantFeedbacks>().eq("record_id", exerciseRecords.getId()));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("list_question", list_question);
        map.put("exerciseRecords", exerciseRecords);
        map.put("instantFeedbacksList", feedbacks_list);
        return BaseResponse.ok(map);
    }


    @PostMapping("/submitAll")
    @ApiOperation("提交总练习")
    public BaseResponse<?> submitAll(@RequestBody SubmitAllParam submitAllParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(submitAllParam.getExerciseRecordsId());
        if (exerciseRecords == null) {
            return BaseResponse.fail("练习记录不存在！");
        }else if (exerciseRecords.getIsDel() == 1) {
            return BaseResponse.fail("练习记录不存在！");
        }
        exerciseRecords.setCompletionStatus(1);
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
        exerciseRecords.setEndTime(java.time.LocalDateTime.now());
        exerciseRecords.setDuration(java.time.Duration.between(exerciseRecords.getStartTime(), exerciseRecords.getEndTime()).toMinutes());
        exerciseRecordsService.updateById(exerciseRecords);
        return BaseResponse.ok();
    }

    @PostMapping("/submit")
    @ApiOperation("提交单个题答案")
    @PrimaryDataSource
    public BaseResponse<?> submit(@RequestBody SubmitParam submitParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer exerciseRecordsId = submitParam.getExerciseRecordsId();
        Integer questionId = submitParam.getQuestionId();
        String userAnswer = submitParam.getUserAnswer();

        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        Question question = questionService.getById(questionId);
        InstantFeedbacks feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("record_id", exerciseRecordsId).eq("question_id", questionId));
        int score_exercise = exerciseRecords.getScore();
        if (feedbacks == null) {
            feedbacks = new InstantFeedbacks();
            feedbacks.setRecordId(exerciseRecordsId);
            feedbacks.setUserId(loginUserAll.getUser().getId());
            feedbacks.setQuestionId(questionId);
            feedbacks.setType(question.getType());
            feedbacks.setShape(question.getShape());
            feedbacks.setCreateBy(loginUserAll.getUser().getRealName());
            feedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
            feedbacks.setCorrectAnswer(question.getAnswer());
            instantFeedbacksService.save(feedbacks);
        }else {
            score_exercise = score_exercise - feedbacks.getUserScore();
        }
        feedbacks = instantFeedbacksService.getOne(new QueryWrapper<InstantFeedbacks>().eq("record_id", exerciseRecordsId).eq("question_id", questionId));
        Integer feedbacksId = feedbacks.getId();
        String answer = feedbacks.getCorrectAnswer();
        InstantFeedbacks instantFeedbacks = instantFeedbacksService.getById(feedbacksId);
        int score = 0;
        if(exerciseRecords.getShape() == 500){
            PanJuanParam panJuanParam = markQuestionUtils.checkAnswerCaoZuo(userAnswer, answer, exerciseRecords.getShape(), 5, exerciseRecords.getModuleId());
            instantFeedbacks.setIsCorrect(panJuanParam.getIsCorrect());
            instantFeedbacks.setBiao(panJuanParam.getBiao());
            instantFeedbacks.setCuo(panJuanParam.getCuo());
            instantFeedbacks.setWu(panJuanParam.getWu());
            instantFeedbacks.setShu(panJuanParam.getShu());
            instantFeedbacks.setZong(panJuanParam.getZong());
            instantFeedbacks.setDa(panJuanParam.getDa());
            instantFeedbacks.setAccuracyRate(panJuanParam.getAccuracyRate());
            instantFeedbacks.setCoverageRate(panJuanParam.getCoverageRate());
            instantFeedbacks.setOperationDuration(panJuanParam.getOperationDuration());
            score = panJuanParam.getCoverageRate().multiply(new BigDecimal(5)).setScale(0, RoundingMode.HALF_UP).intValueExact();
        }else {
            score = markQuestionUtils.checkAnswer(userAnswer, answer, exerciseRecords.getShape(),5,exerciseRecords.getModuleId());
            if (score == 5) {
                instantFeedbacks.setIsCorrect(1);
            }else if (score == 0) {
                instantFeedbacks.setIsCorrect(0);
            }else if (score > 0&& score < 5) {
                instantFeedbacks.setIsCorrect(2);
            }
        }
        instantFeedbacks.setUserAnswer(userAnswer);
        instantFeedbacks.setUserScore(score);
        exerciseRecords.setScore(score_exercise + score);
        String[] feedback_arr = exerciseRecords.getFeedback().split(",");
        if (feedbacksId == Integer.parseInt(feedback_arr[feedback_arr.length-1])) {
            exerciseRecords.setCompletionStatus(1);
            exerciseRecords.setEndTime(java.time.LocalDateTime.now());
            exerciseRecords.setDuration(java.time.Duration.between(exerciseRecords.getStartTime(), exerciseRecords.getEndTime()).toMinutes());
        }
        instantFeedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        instantFeedbacks.setUpdateTime(new Date());
        exerciseRecords.setUpdateTime(new Date());
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
        instantFeedbacksService.updateById(instantFeedbacks);
        exerciseRecordsService.updateById(exerciseRecords);
        return BaseResponse.ok(instantFeedbacks);
    }




    @ApiOperation("按着模块一次出题一次一道题，hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
    @PrimaryDataSource
    public BaseResponse<?> next(@PathVariable Integer exerciseRecordsId, @PathVariable Integer questionId) {
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        String feedback = exerciseRecords.getFeedback();
        String[] feedback_arr = feedback.split(",");
        int index = 0;
        for (int i = 0; i < feedback_arr.length; i++) {
            if (feedback_arr[i].equals(questionId.toString())) {
                index = i + 1;
                break;
            }
            if (i == feedback_arr.length - 1) {
                index = -1;
                break;
            }
        }
        if (index == -1) {
            return BaseResponse.fail("没有下一道题");
        }
        int id = Integer.parseInt(feedback_arr[index]);
        Question question = questionService.getById(id);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("question", question);
        map.put("exerciseRecordsId", exerciseRecordsId);
        if (feedback_arr.length > index + 1) {
            map.put("hasNext", true);
        } else {
            map.put("hasNext", false);
        }
        return BaseResponse.ok(map);
    }



    @PostMapping("/finish/{exerciseRecordsId}")
    @ApiOperation("完成测试")
    @PrimaryDataSource
    public BaseResponse<?> finish(@PathVariable Integer exerciseRecordsId) {
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        exerciseRecords.setEndTime(LocalDateTime.now());
        exerciseRecords.setDuration(Duration.between(exerciseRecords.getStartTime(), LocalDateTime.now()).toMinutes());
        exerciseRecordsService.updateById(exerciseRecords);
        return BaseResponse.ok("完成测试，满分100分，得分"+exerciseRecords.getScore());
    }

    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询练习记录表")
    public BaseResponse<Page<ExerciseRecords>> page(@Valid @RequestBody ExerciseRecordsQuery query) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        query.setUserId(loginUserAll.getUser().getId());
        Pageable pageable = query.getPageInfo();
        Page<ExerciseRecords> voPage = exerciseRecordsService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("添加练习记录表")
    @Log(content = "添加练习记录表", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody ExerciseRecords exerciseRecords) {
        return exerciseRecordsService.save1(exerciseRecords);
    }

    @PostMapping("/update")
    @ApiOperation("更新练习记录表,结束练习")
    @Log(content = "更新练习记录表", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody ExerciseRecords exerciseRecords) {
        return exerciseRecordsService.update(exerciseRecords);
    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除练习记录表")
    @Log(content = "删除练习记录表", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return exerciseRecordsService.delete(id);
    }

}
