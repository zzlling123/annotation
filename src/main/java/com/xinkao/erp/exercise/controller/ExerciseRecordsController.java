package com.xinkao.erp.exercise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
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
import java.util.HashMap;
import java.util.List;


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


    /**
     * 选择模块并开始练习并返回第一道题的信息
     * @param typeid 模块编号
     *               题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     * */
    @PostMapping("/start/{typeid}/{shape}")
    @ApiOperation("选择模块并开始练习并返回第一道题的信息,hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
    @PrimaryDataSource
    public BaseResponse<?> start(@PathVariable Integer typeid, @PathVariable Integer shape, HttpServletRequest request) {

        ExerciseRecords exerciseRecords = new ExerciseRecords();
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        exerciseRecords.setUserId(loginUserAll.getUser().getId());
        exerciseRecords.setCreateBy(loginUserAll.getUser().getRealName());
        exerciseRecords.setUpdateBy(loginUserAll.getUser().getRealName());
//        exerciseRecords.setUserId(1);
//        exerciseRecords.setCreateBy("admin");
//        exerciseRecords.setUpdateBy("admin");
        exerciseRecords.setStartTime(java.time.LocalDateTime.now());

        //设置createby是当前用户

        exerciseRecords.setModuleId(typeid);

        //随机出题20道题，20道题的id保存到redis中，并返回给前端
        //根据类型首先查出所有符合类型的题的编号
        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Question::getType, typeid).eq(Question::getShape, shape);
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
        //根据feedback存储的题号返回第一道题的信息
        String[] feedback_arr = feedback.split(",");
        Question question = questionService.getById(feedback_arr[0]);
        //创建一个InstantFeedbacks对象并按着信息存储到instantfeedbacks表中
        InstantFeedbacks instantFeedbacks = new InstantFeedbacks();
        instantFeedbacks.setRecordId(exerciseRecords.getId());
        instantFeedbacks.setQuestionId(question.getId());
        instantFeedbacks.setShape(question.getShape());
        instantFeedbacks.setCreateBy(loginUserAll.getUser().getRealName());
        instantFeedbacks.setUpdateBy(loginUserAll.getUser().getRealName());
        instantFeedbacks.setCorrectAnswer(question.getAnswer());
        instantFeedbacksService.save(instantFeedbacks);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("question", question);
        map.put("exerciseRecordsId", exerciseRecords.getId());

        //判断是否有下一题
        if (feedback_arr.length > 1) {
            map.put("hasNext", true);
        } else {
            map.put("hasNext", false);
        }
        return BaseResponse.ok(map);
    }

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
     * @param exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     *                          questionId 当前题的编号，开始练习时候返回的当前题的编号
     * @return hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false
     * @return question 返回当前题的信息
     * @return exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号
     */
    @PostMapping("/submit/{exerciseRecordsId}/{questionId}/{userAnswer}")
    @ApiOperation("提交答案并进行答案的判断，并返回正确答案和是否回答正确，并返回下一道题的信息,hasNext 返回下一道题的信息，如果有下一题则返回true，如果没有下一题则返回false,question 返回当前题的信息,exerciseRecordsId 练习记录编号，开始练习时候返回的练习记录编号")
    @PrimaryDataSource
    public BaseResponse<?> submit(@PathVariable Integer exerciseRecordsId, @PathVariable Integer questionId, @PathVariable String userAnswer) {
        ExerciseRecords exerciseRecords = exerciseRecordsService.getById(exerciseRecordsId);
        //根据题号找到当前题并获取答案
        Question question = questionService.getById(questionId);
        String answer = question.getAnswer();
        int score = checkAnswer(userAnswer, answer, question.getShape(),exerciseRecords.getScore());
        LambdaQueryWrapper<InstantFeedbacks> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(InstantFeedbacks::getRecordId, exerciseRecordsId).eq(InstantFeedbacks::getQuestionId, questionId);
        InstantFeedbacks instantFeedbacks = instantFeedbacksService.getBaseMapper().selectOne(wrapper);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("exerciseRecordsId", exerciseRecordsId);
        if (score == exerciseRecords.getScore()) {
            //正确
            exerciseRecords.setCompletionStatus(exerciseRecords.getCompletionStatus() + 1);
            exerciseRecords.setScore(exerciseRecords.getScore() + score);
            instantFeedbacks.setIsCorrect(1);
            instantFeedbacks.setUserScore(score);
            instantFeedbacks.setUserAnswer(userAnswer);
            map.put("isCorrect", "正确");
        }else if (score == 0) {
            exerciseRecords.setCompletionStatus(exerciseRecords.getCompletionStatus() + 1);
            exerciseRecords.setScore(exerciseRecords.getScore() + score);
            //错误
            instantFeedbacks.setIsCorrect(0);
            instantFeedbacks.setUserScore(score);
            instantFeedbacks.setUserAnswer(userAnswer);
            map.put("isCorrect", "不正确");
        }else if (score > 0&& score < exerciseRecords.getScore()) {
            //部分正确
            exerciseRecords.setCompletionStatus(exerciseRecords.getCompletionStatus() + 1);
            exerciseRecords.setScore(exerciseRecords.getScore() + score);
            instantFeedbacks.setIsCorrect(2);
            instantFeedbacks.setUserScore(score);
            instantFeedbacks.setUserAnswer(userAnswer);
            map.put("isCorrect", "部分正确");
        }
        exerciseRecordsService.updateById(exerciseRecords);
        instantFeedbacksService.updateById(instantFeedbacks);
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
        if (index == feedback_arr.length - 1) {
            return BaseResponse.fail("没有下一道题");
        }
        int id = Integer.parseInt(feedback_arr[index]);
        Question question_next = questionService.getById(id);
        map.put("question", question_next);
        return BaseResponse.ok(map);
    }

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
     * 判断输入的答案是否正确，如果正确，则设置用户得分为题目的得分，否则设置为0
     * */
    public int checkAnswer(String userAnswer, String correctAnswer, int shape, Integer score) {
        //shape题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
        if (shape == 100) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }else if (shape == 200) {
            //duo选题少答的一半分数，错答得0分
            String [] userAnswers = userAnswer.split("");
            String [] correctAnswers = correctAnswer.split("");
            if (userAnswer.length() == correctAnswer.length()) {
              for (int i = 0; i < userAnswers.length; i++) {
                  if (userAnswers[i].equals(correctAnswers[i])) {
                      continue;
                  }else {
                      return 0;
                  }
              }
              return score;
            }else if (userAnswer.length() > correctAnswer.length()) {
                for (int i = 0; i < userAnswers.length; i++) {
                    if (userAnswers[i].equals(correctAnswers[i])) {
                        continue;
                    }else {
                        return 0;
                    }
                }
                return score/2;
            }else if (userAnswer.length() < correctAnswer.length()) {
                return 0;
            }
        }else if (shape == 300) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }else if (shape == 400) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }else if (shape == 500) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }
        return 0;
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

    /**
     * 获取练习记录表
     *
     * @return {@link String}
     */
    @RequestMapping("/get/{id}")
    @ApiOperation("根据id获取练习记录详情")
    public String get() {
        return "根据id获取练习记录详情";
    }


}
