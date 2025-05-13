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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


/**
 * 练习记录表
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
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


    /**
     * 选择模块创建练习练习,题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     * @param
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     * */
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
//        exerciseRecords.setUserId(1);
//        exerciseRecords.setCreateBy("admin");
//        exerciseRecords.setUpdateBy("admin");
        exerciseRecords.setStartTime(java.time.LocalDateTime.now());

        //设置createby是当前用户
        exerciseRecords.setShape(shape);

        exerciseRecords.setModuleId(moduleId);

        //随机出题20道题，20道题的id保存到redis中，并返回给前端
        //根据类型首先查出所有符合类型的题的编号
        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Question::getType, moduleId).eq(Question::getShape, shape);
        List<Question> list_question = questionService.getBaseMapper().selectList(wrapper);
        String feedback = "" ;
        if (list_question.size() <= 20) {
            //把所有的题号存到redis中
            for (Question question : list_question) {
                int q_id = question.getId();
                feedback += q_id + ",";
            }
        }else if (list_question.size() > 20) {
            //随机出20道题
            for (int i = 0; i < 20; i++) {
                int index = (int) (Math.random() * list_question.size());
                Question question = list_question.get(index);
                int q_id = question.getId();
                feedback += q_id + ",";
            }
        }
        feedback = feedback.substring(0, feedback.length() - 1);
        exerciseRecords.setFeedback(feedback);
        exerciseRecordsService.save(exerciseRecords);
        return BaseResponse.ok();
    }
//    public BaseResponse<?> create(@PathVariable Integer typeid, @PathVariable Integer shape, HttpServletRequest request) {
//
//        ExerciseRecords exerciseRecords = new ExerciseRecords();
//        LoginUser loginUserAll = redisUtil.getInfoByToken();
//        exerciseRecords.setUserId(loginUserAll.getUser().getId());
//        exerciseRecords.setCreateBy(loginUserAll.getUser().getRealName());
//        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
////        exerciseRecords.setUserId(1);
////        exerciseRecords.setCreateBy("admin");
////        exerciseRecords.setUpdateBy("admin");
//        exerciseRecords.setStartTime(java.time.LocalDateTime.now());
//
//        //设置createby是当前用户
//
//        exerciseRecords.setModuleId(typeid);
//
//        //随机出题20道题，20道题的id保存到redis中，并返回给前端
//        //根据类型首先查出所有符合类型的题的编号
//        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
//        wrapper.eq(Question::getType, typeid).eq(Question::getShape, shape);
//        List<Question> list_question = questionService.getBaseMapper().selectList(wrapper);
//        String feedback = "" ;
//        if (list_question.size() <= 20) {
//            //把所有的题号存到redis中
//            for (Question question : list_question) {
//                int q_id = question.getId();
//                feedback += q_id + ",";
//            }
//        }else if (list_question.size() > 20) {
//            //随机出20道题
//            for (int i = 0; i < 20; i++) {
//                int index = (int) (Math.random() * list_question.size());
//                Question question = list_question.get(index);
//                int q_id = question.getId();
//                feedback += q_id + ",";
//            }
//        }
//        feedback = feedback.substring(0, feedback.length() - 1);
//        exerciseRecords.setFeedback(feedback);
//        exerciseRecordsService.save(exerciseRecords);
//        //根据feedback存储的题号返回第一道题的信息
//        String[] feedback_arr = feedback.split(",");
//        Question question = questionService.getById(feedback_arr[0]);
//        //创建一个InstantFeedbacks对象并按着信息存储到instantfeedbacks表中
//        InstantFeedbacks instantFeedbacks = new InstantFeedbacks();
//        instantFeedbacks.setRecordId(exerciseRecords.getId());
//        instantFeedbacks.setQuestionId(question.getId());
//        instantFeedbacks.setShape(question.getShape());
//        instantFeedbacks.setCreateBy(loginUserAll.getUser().getRealName());
//        instantFeedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
//        instantFeedbacks.setCorrectAnswer(question.getAnswer());
//        instantFeedbacksService.save(instantFeedbacks);
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("question", question);
//        map.put("exerciseRecordsId", exerciseRecords.getId());
//
//        //判断是否有下一题
//        if (feedback_arr.length > 1) {
//            map.put("hasNext", true);
//        } else {
//            map.put("hasNext", false);
//        }
//        return BaseResponse.ok(map);
//    }

    //查看练习记录
    @GetMapping("/detail/{exerciseRecordsId}")
    @ApiOperation("查看练习记录表,list_question是试题集合，instantFeedbacksList是学生的练习记录")
    //@PrimaryDataSource
    public BaseResponse<?> detail(@PathVariable Integer exerciseRecordsId) {
        //获取当前用户
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        //判断当前用户是否是练习记录的创建人
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
        //查询练习记录
        List<InstantFeedbacks> instantFeedbacksList = instantFeedbacksService.list(new LambdaQueryWrapper<InstantFeedbacks>().eq(InstantFeedbacks::getRecordId, exerciseRecordsId).in(InstantFeedbacks::getQuestionId, feedback_list));
        List<Question> list_question = questionService.list(new LambdaQueryWrapper<Question>().in(Question::getId, feedback_list));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("instantFeedbacksList", instantFeedbacksList);
        map.put("list_question", list_question);
        return BaseResponse.ok(map);
    }

    /**
     * 开始练习并返回第一道题的信息
     * @param id 模块编号
     *               题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     * */
    @PostMapping("/start/{id}")
    @ApiOperation("开始练习并返回所有题的信息，feedbacks_list是练习详情记录，有userAnswer答案是做了，没有答案是没有完成的，list_question所有题的信息。")
    @PrimaryDataSource
    public BaseResponse<?> start(@PathVariable Integer id, HttpServletRequest request) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(id);
        exerciseRecords.setStartTime(java.time.LocalDateTime.now());
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
        exerciseRecords.setCompletionStatus(2);
        exerciseRecordsService.updateById(exerciseRecords);

        //根据feedback存储的题号返回第一道题的信息
        String[] feedback_arr = exerciseRecords.getFeedback().split(",");
        List<Question> list_question = new ArrayList<Question>();
        for (int i = 0; i < feedback_arr.length; i++){
            Question question = questionService.getById(feedback_arr[i]);
            list_question.add(question);
            //创建一个InstantFeedbacks对象并按着信息存储到instantfeedbacks表中
            InstantFeedbacks instantFeedbacks = new InstantFeedbacks();
            instantFeedbacks.setRecordId(exerciseRecords.getId());
            instantFeedbacks.setQuestionId(question.getId());
            instantFeedbacks.setShape(question.getShape());
            instantFeedbacks.setCreateBy(loginUserAll.getUser().getRealName());
            instantFeedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
            instantFeedbacks.setCorrectAnswer(question.getAnswer());
            instantFeedbacksService.save(instantFeedbacks);
        }
        List<InstantFeedbacks> feedbacks_list = instantFeedbacksService.list(new QueryWrapper<InstantFeedbacks>().eq("record_id", exerciseRecords.getId()));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("list_question", list_question);
        map.put("exerciseRecordsId", exerciseRecords.getId());
        map.put("feedbacks_list", feedbacks_list);
        return BaseResponse.ok(map);
    }

    @PostMapping("/submit")
    @ApiOperation("提交答案并进行答案的判断，并返回正确答案和是否回答正确，并返回下一道题的信息,hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
    @PrimaryDataSource
    public BaseResponse<?> submit(@RequestBody SubmitParam submitParam) {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        Integer exerciseRecordsId = submitParam.getExerciseRecordsId();
        Integer feedbacksId = submitParam.getFeedbacksId();
        String userAnswer = submitParam.getUserAnswer();

        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        //根据题号找到当前题并获取答案
        InstantFeedbacks feedbacks = instantFeedbacksService.getById(feedbacksId);
        String answer = feedbacks.getCorrectAnswer();
        int score = markQuestionUtils.checkAnswer(userAnswer, answer, exerciseRecords.getShape(),exerciseRecords.getScore(),exerciseRecords.getModuleId());
        InstantFeedbacks instantFeedbacks = instantFeedbacksService.getById(feedbacksId);
        instantFeedbacks.setUserAnswer(userAnswer);
        instantFeedbacks.setUserScore(score);
        exerciseRecords.setScore(exerciseRecords.getScore() + score);
        //如果是最后一道题就更新练习记录的完成状态和分数
        String[] feedback_arr = exerciseRecords.getFeedback().split(",");
        if (feedbacksId == Integer.parseInt(feedback_arr[feedback_arr.length-1])) {
            exerciseRecords.setCompletionStatus(1);
            exerciseRecords.setEndTime(java.time.LocalDateTime.now());
            //计算练习时长
            exerciseRecords.setDuration(java.time.Duration.between(exerciseRecords.getStartTime(), exerciseRecords.getEndTime()).toMinutes());
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("exerciseRecordsId", exerciseRecordsId);
        map.put("questionId", feedbacks.getQuestionId());
        map.put("score", score);
        if (score == exerciseRecords.getScore()) {
            //正确
            instantFeedbacks.setIsCorrect(1);
            map.put("isCorrect", "正确");
        }else if (score == 0) {
            //错误
            instantFeedbacks.setIsCorrect(0);
            map.put("isCorrect", "不正确");
        }else if (score > 0&& score < exerciseRecords.getScore()) {
            //部分正确
            instantFeedbacks.setIsCorrect(2);
            map.put("isCorrect", "部分正确");
        }
        instantFeedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        instantFeedbacks.setUpdateTime(new Date());
        exerciseRecords.setUpdateTime(new Date());
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
        instantFeedbacksService.updateById(instantFeedbacks);
        exerciseRecordsService.updateById(exerciseRecords);
        return BaseResponse.ok(map);
    }

//    @PostMapping("/start/{id}")
//    @ApiOperation("开始练习并返回第一道题的信息，hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
//    @PrimaryDataSource
//    public BaseResponse<?> start(@PathVariable Integer id, HttpServletRequest request) {
//        LoginUser loginUserAll = redisUtil.getInfoByToken();
//        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(id);
//        exerciseRecords.setStartTime(java.time.LocalDateTime.now());
//        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
//        exerciseRecords.setCompletionStatus(2);
//        exerciseRecordsService.updateById(exerciseRecords);
//        //根据feedback存储的题号返回第一道题的信息
//        String[] feedback_arr = exerciseRecords.getFeedback().split(",");
//        Question question = questionService.getById(feedback_arr[0]);
//        //创建一个InstantFeedbacks对象并按着信息存储到instantfeedbacks表中
//        InstantFeedbacks instantFeedbacks = new InstantFeedbacks();
//        instantFeedbacks.setRecordId(exerciseRecords.getId());
//        instantFeedbacks.setQuestionId(question.getId());
//        instantFeedbacks.setShape(question.getShape());
//        instantFeedbacks.setCreateBy(loginUserAll.getUser().getRealName());
//        instantFeedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
//        instantFeedbacks.setCorrectAnswer(question.getAnswer());
//        instantFeedbacksService.save(instantFeedbacks);
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("question", question);
//        map.put("exerciseRecordsId", exerciseRecords.getId());
//        //判断是否有下一题
//        if (feedback_arr.length > 1) {
//            map.put("hasNext", true);
//        } else {
//            map.put("hasNext", false);
//        }
//        return BaseResponse.ok(map);
//    }



    /**
     * 按着模块一次出题一次一道题，并返回是否有下一道题，此信息中包含题的内容答案和提示信息
     * @param exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     *                          questionId 当前题的编号，开始练习时候返回的当前题的编号
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     */
    //@PostMapping("/next/{exerciseRecordsId}/{questionId}")
    @ApiOperation("按着模块一次出题一次一道题，hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
    @PrimaryDataSource
    public BaseResponse<?> next(@PathVariable Integer exerciseRecordsId, @PathVariable Integer questionId) {
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        //查询当前题号是第几题并查询下一题
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
        //判断是否有下一题
        if (feedback_arr.length > index + 1) {
            map.put("hasNext", true);
        } else {
            map.put("hasNext", false);
        }
        return BaseResponse.ok(map);
    }


    /**
     * 按着模块一次出题一次一道题，并返回正确答案和是否回答正确，并返回是否有下一道题，此信息中包含题的内容答案和提示信息
     * @param  练习记录编号，开始练习时候返回的练习记录编号
     *                          questionId 当前题的编号，开始练习时候返回的当前题的编号
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     */
//    @PostMapping("/submit")
//    @ApiOperation("提交答案并进行答案的判断，并返回正确答案和是否回答正确，并返回下一道题的信息,hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
//    @PrimaryDataSource
//    public BaseResponse<?> submit(@RequestBody SubmitParam submitParam) {
//        Integer exerciseRecordsId = submitParam.getExerciseRecordsId();
//        Integer questionId = submitParam.getQuestionId();
//        String userAnswer = submitParam.getUserAnswer();
//
//        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
//        //根据题号找到当前题并获取答案
//        Question question = questionService.getById(questionId);
//        String answer = question.getAnswer();
//        int score = checkAnswer(userAnswer, answer, question.getShape(),exerciseRecords.getScore());
//        LambdaQueryWrapper<InstantFeedbacks> wrapper = Wrappers.lambdaQuery();
//        wrapper.eq(InstantFeedbacks::getRecordId, exerciseRecordsId).eq(InstantFeedbacks::getQuestionId, questionId);
//        InstantFeedbacks instantFeedbacks = instantFeedbacksService.getBaseMapper().selectOne(wrapper);
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("exerciseRecordsId", exerciseRecordsId);
//        if (score == exerciseRecords.getScore()) {
//            //正确
//            exerciseRecords.setCompletionStatus(exerciseRecords.getCompletionStatus() + 1);
//            exerciseRecords.setScore(exerciseRecords.getScore() + score);
//            instantFeedbacks.setIsCorrect(1);
//            instantFeedbacks.setUserScore(score);
//            instantFeedbacks.setUserAnswer(userAnswer);
//            map.put("isCorrect", "正确");
//        }else if (score == 0) {
//            exerciseRecords.setCompletionStatus(exerciseRecords.getCompletionStatus() + 1);
//            exerciseRecords.setScore(exerciseRecords.getScore() + score);
//            //错误
//            instantFeedbacks.setIsCorrect(0);
//            instantFeedbacks.setUserScore(score);
//            instantFeedbacks.setUserAnswer(userAnswer);
//            map.put("isCorrect", "不正确");
//        }else if (score > 0&& score < exerciseRecords.getScore()) {
//            //部分正确
//            exerciseRecords.setCompletionStatus(exerciseRecords.getCompletionStatus() + 1);
//            exerciseRecords.setScore(exerciseRecords.getScore() + score);
//            instantFeedbacks.setIsCorrect(2);
//            instantFeedbacks.setUserScore(score);
//            instantFeedbacks.setUserAnswer(userAnswer);
//            map.put("isCorrect", "部分正确");
//        }
//        exerciseRecordsService.updateById(exerciseRecords);
//        instantFeedbacksService.updateById(instantFeedbacks);
//        String feedback = exerciseRecords.getFeedback();
//        String[] feedback_arr = feedback.split(",");
//        int index = 0;
//        for (int i = 0; i < feedback_arr.length; i++) {
//            if (feedback_arr[i].equals(questionId.toString())) {
//                index = i + 1;
//                break;
//            }
//            if (i == feedback_arr.length - 1) {
//                index = -1;
//                break;
//            }
//        }
//        if (index == -1) {
//            return BaseResponse.fail("没有下一道题");
//        }
//        if (index == feedback_arr.length - 1) {
//            return BaseResponse.fail("没有下一道题");
//        }
//        int id = Integer.parseInt(feedback_arr[index]);
//        Question question_next = questionService.getById(id);
//        map.put("question", question_next);
//        return BaseResponse.ok(map);
//    }

    /**
     * 完成测试
     */
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

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
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

    /**
     * 新增课程章节信息
     *
     * @param exerciseRecords 课程章节信息参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("添加练习记录表")
    @Log(content = "添加练习记录表", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody ExerciseRecords exerciseRecords) {
        return exerciseRecordsService.save1(exerciseRecords);
    }

    /**
     * 更新练习记录表
     *
     * @param exerciseRecords 练习记录参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("更新练习记录表,结束练习")
    @Log(content = "更新练习记录表", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody ExerciseRecords exerciseRecords) {
        return exerciseRecordsService.update(exerciseRecords);
    }

    /**
     * 删除练习记录表
     *
     * @param id 练习记录id
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除练习记录表")
    @Log(content = "删除练习记录表", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return exerciseRecordsService.delete(id);
    }

}
