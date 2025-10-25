package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.common.util.PointSubmitUtil;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.*;
import com.xinkao.erp.exam.mapper.ExamPageUserMapper;
import com.xinkao.erp.exam.mapper.ExamPageUserQuestionMapper;
import com.xinkao.erp.exam.model.param.ExamPageUserChildAnswerParam;
import com.xinkao.erp.exam.model.vo.*;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.param.ExamCorrectChildParam;
import com.xinkao.erp.exam.param.ExamCorrectParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.service.*;
import com.xinkao.erp.exam.vo.ExamPageAnswerVo;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;
import com.xinkao.erp.exam.vo.ExamPageUserListVo;
import com.xinkao.erp.exercise.param.PanJuanParam;
import com.xinkao.erp.exercise.utils.MarkQuestionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamPageUserServiceImpl extends BaseServiceImpl<ExamPageUserMapper, ExamPageUser> implements ExamPageUserService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PointSubmitUtil pointSubmitUtil;
    @Autowired
    private ExamPageUserMapper examPageUserMapper;
    @Autowired
    private ExamService examService;
    @Autowired
    private ExamPageUserQuestionMapper examPageUserQuestionMapper;
    @Autowired
    private ExamPageUserAnswerService examPageUserAnswerService;
    @Autowired
    private ExamPageUserQuestionFormTitleService examPageUserQuestionFormTitleService;
    @Autowired
    private ExamPageUserChildAnswerService examPageUserChildAnswerService;
    @Autowired
    private ExamPageUserQuestionChildService examPageUserQuestionChildService;
    @Autowired
    private ExamPageUserLogService examPageUserLogService;
    @Autowired
    private ExamPageSetService examPageSetService;
    @Autowired
    private MarkQuestionUtils markQuestionUtils;

    @Override
    public Page<ExamUserVo> page(ExamQuery query, Pageable pageable){
        LoginUser loginUser = redisUtil.getInfoByToken();
        Page page = pageable.toPage();
        return examPageUserMapper.page(page, query, loginUser.getUser().getId());
    }

    @Override
    public BaseResponse<ExamUserVo> getExamUserInfo(ExamUserQuery examUserQuery) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer userId = loginUser.getUser().getId();
        String examId = examUserQuery.getExamId();
        Exam exam = examService.getById(examId);
        ExamPageUser examPageUser = lambdaQuery().eq(ExamPageUser::getExamId, examId)
                .eq(ExamPageUser::getUserId, userId)
                .one();
        ExamUserVo vo = BeanUtil.copyProperties(examPageUser, ExamUserVo.class);
        vo.setExamName(examService.getById(vo.getExamId()).getExamName());
        vo.setStartTime(exam.getStartTime());
        vo.setEndTime(exam.getEndTime());
        vo.setDuration(exam.getDuration().toString());

        LambdaQueryWrapper<ExamPageUserQuestion> examPageUserQuestionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examPageUserQuestionLambdaQueryWrapper.eq(ExamPageUserQuestion::getExamId, examId)
                .eq(ExamPageUserQuestion::getUserId, userId)
                .orderByAsc(ExamPageUserQuestion::getNumSort);
        List<ExamPageUserQuestion> questionList = examPageUserQuestionMapper.selectList(examPageUserQuestionLambdaQueryWrapper);
        Map<String, ExamPageUserAnswer> examPageUserAnswerMap = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .list().stream().collect(Collectors.toMap(ExamPageUserAnswer::getQuestionId, s->s));
        for (ExamPageUserQuestion examPageUserQuestion : questionList) {

            ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerMap.get(examPageUserQuestion.getId());
            examPageUserQuestion.setAnswer("");
            examPageUserQuestion.setUserAnswer(examPageUserAnswer == null ? "" : examPageUserAnswer.getUserAnswer());

            if (examPageUserQuestion.getShape() == 300) {
                examPageUserQuestion.setOptions("");
            }
        }
        vo.setQuestionVoList(BeanUtil.copyToList(questionList, ExamPageUserQuestionVo.class));
        return BaseResponse.ok("成功",vo);
    }

    @Override
    public BaseResponse<ExamPageUserQuestionVo> getUserQuestionInfo(String id) {

        ExamPageUserQuestion examPageUserQuestion = examPageUserQuestionMapper.selectById(id);
        Map<String, ExamPageUserAnswer> examPageUserAnswerMap = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getQuestionId,id)
                .list().stream().collect(Collectors.toMap(ExamPageUserAnswer::getQuestionId, s->s));

        ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerMap.get(examPageUserQuestion.getId());
        examPageUserQuestion.setAnswer("");
        examPageUserQuestion.setUserAnswer(examPageUserAnswer == null ? "" : examPageUserAnswer.getUserAnswer());
        ExamPageUserQuestionVo vo = BeanUtil.copyProperties(examPageUserQuestion, ExamPageUserQuestionVo.class);

        if (vo.getIsForm() == 1){
            List<ExamPageUserQuestionFormTitleVo> examPageUserQuestionFormTitleVoList = new ArrayList<>();
            List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = examPageUserQuestionFormTitleService.lambdaQuery()
                    .eq(ExamPageUserQuestionFormTitle::getPid,id)
                    .orderByAsc(ExamPageUserQuestionFormTitle::getSort)
                    .list();
            Map<String, List<ExamPageUserQuestionChild>> examPageUserQuestionChildMap = examPageUserQuestionChildService.lambdaQuery()
                    .eq(ExamPageUserQuestionChild::getQuestionId,id)
                    .orderByAsc(ExamPageUserQuestionChild::getSort)
                    .list()
                    .stream()
                    .collect(Collectors.groupingBy(ExamPageUserQuestionChild::getPid));
            Map<String, ExamPageUserChildAnswer> examPageUserChildAnswerMap = examPageUserChildAnswerService.lambdaQuery()
                    .eq(ExamPageUserChildAnswer::getQuestionId,id)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(ExamPageUserChildAnswer::getQuestionChildId, s->s));
            for (ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle : examPageUserQuestionFormTitleList) {
                ExamPageUserQuestionFormTitleVo examPageUserQuestionFormTitleVo = BeanUtil.copyProperties(examPageUserQuestionFormTitle, ExamPageUserQuestionFormTitleVo.class);
                List<ExamPageUserQuestionChildVo> examPageUserQuestionChildVoList = BeanUtil.copyToList(examPageUserQuestionChildMap.get(examPageUserQuestionFormTitle.getId()), ExamPageUserQuestionChildVo.class);
                for (ExamPageUserQuestionChildVo examPageUserQuestionChildVo : examPageUserQuestionChildVoList) {
                    examPageUserQuestionChildVo.setUserAnswer(examPageUserChildAnswerMap.get(examPageUserQuestionChildVo.getId()) == null ? "" : examPageUserChildAnswerMap.get(examPageUserQuestionChildVo.getId()).getUserAnswer());
                }
                examPageUserQuestionFormTitleVo.setExamPageUserQuestionChildVoList(examPageUserQuestionChildVoList);
                examPageUserQuestionFormTitleVoList.add(examPageUserQuestionFormTitleVo);
            }
            vo.setExamPageUserQuestionFormTitleVoList(examPageUserQuestionFormTitleVoList);
        }
        return BaseResponse.ok("成功",vo);
    }

    @Override
    public BaseResponse<List<ExamProgressVo>> getExamUserProgress(ExamUserQuery examUserQuery) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer userId = loginUser.getUser().getId();
        String examId = examUserQuery.getExamId();

        List<ExamPageUserAnswer> examPageUserAnswers = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .orderByAsc(ExamPageUserAnswer::getNumSort)
                .list();
        List<ExamProgressVo> voList = BeanUtil.copyToList(examPageUserAnswers, ExamProgressVo.class);
        return BaseResponse.ok("成功",voList);
    }

    @Override
    public BaseResponse<?> submitAnswer(ExamPageUserAnswerParam examPageUserAnswerParam) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer userId = loginUser.getUser().getId();
        String examId = examPageUserAnswerParam.getExamId();

        ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .eq(ExamPageUserAnswer::getQuestionId,examPageUserAnswerParam.getQuestionId())
                .last("limit 1").one();
        if (examPageUserAnswer == null){
            return BaseResponse.fail("题目模板不存在");
        }
        examPageUserAnswer.setUserAnswer(examPageUserAnswerParam.getAnswer());
        examPageUserAnswer.setAnswerStatus(1);
        examPageUserAnswerService.updateById(examPageUserAnswer);

        boolean update = false;
        ExamPageUser examPageUser = lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getUserId,userId)
                .one();
        if (StrUtil.isBlank(examPageUser.getStartTs())){
            examPageUser.setStartTs(DateUtil.now());
            update = true;
        }
        Long noSubmitNum = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .eq(ExamPageUserAnswer::getAnswerStatus,0)
                .count();
        if (noSubmitNum==0){
            examPageUser.setEndTs(DateUtil.now());
            update = true;
        }
        if (update){
            updateById(examPageUser);
        }
        return BaseResponse.ok();
    }

    @Override
    public BaseResponse<?> submitChildAnswer(ExamPageUserChildAnswerParam param) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer userId = loginUser.getUser().getId();

        ExamPageUserChildAnswer examPageUserChildAnswer = examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionChildId,param.getChildId())
                .last("limit 1").one();
        if (examPageUserChildAnswer == null){
            return BaseResponse.fail("题目模板不存在");
        }
        examPageUserChildAnswer.setUserAnswer(param.getAnswer());
        examPageUserChildAnswer.setAnswerStatus(1);
        examPageUserChildAnswerService.updateById(examPageUserChildAnswer);

        Long noChildSubmitNum = examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionId,examPageUserChildAnswer.getQuestionId())
                .eq(ExamPageUserChildAnswer::getUserId,userId)
                .eq(ExamPageUserChildAnswer::getAnswerStatus,0)
                .count();
        if (noChildSubmitNum==0){
            examPageUserAnswerService.lambdaUpdate()
                    .eq(ExamPageUserAnswer::getQuestionId,examPageUserChildAnswer.getQuestionId())
                    .eq(ExamPageUserAnswer::getUserId,userId)
                    .set(ExamPageUserAnswer::getAnswerStatus,1)
                    .update();

            boolean update = false;
            ExamPageUser examPageUser = lambdaQuery()
                    .eq(ExamPageUser::getExamId,examPageUserChildAnswer.getExamId())
                    .eq(ExamPageUser::getUserId,userId)
                    .one();
            if (StrUtil.isBlank(examPageUser.getStartTs())){
                examPageUser.setStartTs(DateUtil.now());
                update = true;
            }
            Long noSubmitNum = examPageUserAnswerService.lambdaQuery()
                    .eq(ExamPageUserAnswer::getExamId,examPageUserChildAnswer.getExamId())
                    .eq(ExamPageUserAnswer::getUserId,userId)
                    .eq(ExamPageUserAnswer::getAnswerStatus,0)
                    .count();
            if (noSubmitNum==0){
                examPageUser.setEndTs(DateUtil.now());
                update = true;
            }
            if (update){
                updateById(examPageUser);
            }
        }
        return BaseResponse.ok();
    }

    @Override
    public BaseResponse<Map<String,Integer>> submitExam(SubmitParam submitParam) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer userId = loginUser.getUser().getId();
        String examId = submitParam.getExamId();

        List<ExamPageUserAnswer> examPageUserAnswers = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .orderByAsc(ExamPageUserAnswer::getNumSort)
                .list();
        BigDecimal allScores = BigDecimal.ZERO;
        for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswers) {
            if (StrUtil.isBlank(examPageUserAnswer.getUserAnswer())){
                examPageUserAnswer.setUserAnswer("");
            }

            if (100 == examPageUserAnswer.getShape() || 300 == examPageUserAnswer.getShape() || 700 == examPageUserAnswer.getShape() ){
                if (examPageUserAnswer.getUserAnswer().equals(examPageUserAnswer.getRightAnswer())){
                    examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                    allScores = allScores.add(examPageUserAnswer.getScore());
                }
            }else if (200 == examPageUserAnswer.getShape()){

                List<String> teaAnswerList = Arrays.asList(examPageUserAnswer.getRightAnswer().split(""));
                List<String> stuAnswerList = Arrays.asList(examPageUserAnswer.getUserAnswer().split(""));
                if (teaAnswerList.containsAll(stuAnswerList)) {
                    if (teaAnswerList.size() == stuAnswerList.size()) {
                        examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                        allScores = allScores.add(examPageUserAnswer.getScore());
                    } else {
                        examPageUserAnswer.setUserScore(examPageUserAnswer.getScorePart());
                        allScores = allScores.add(examPageUserAnswer.getScorePart());
                    }
                }
            }else if (400 == examPageUserAnswer.getShape()){

                if (examPageUserAnswer.getNeedCorrect() == 0){
                    if (examPageUserAnswer.getRightAnswer().equals(examPageUserAnswer.getUserAnswer())){
                        examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                        allScores = allScores.add(examPageUserAnswer.getUserScore());
                    }
                }
            }else if (500 == examPageUserAnswer.getShape()){

                BigDecimal score = BigDecimal.ZERO;
                if (examPageUserAnswer.getNeedCorrect() == 0){
                    if (examPageUserAnswer.getType() == 1 || examPageUserAnswer.getType() == 3){

                        if (examPageUserAnswer.getUserAnswer().equals(examPageUserAnswer.getRightAnswer())){
                            examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                            allScores = allScores.add(examPageUserAnswer.getUserScore());
                        }
                    }else if (examPageUserAnswer.getType() == 2 || examPageUserAnswer.getType() == 7){

                        PanJuanParam dto = pointSubmitUtil.get3DPointScore(examPageUserAnswer);
                        examPageUserAnswer.setUserScore(dto.getScore());

                        examPageUserAnswer.setBiao(dto.getBiao());
                        examPageUserAnswer.setCuo(dto.getCuo());
                        examPageUserAnswer.setWu(dto.getWu());
                        examPageUserAnswer.setShu(dto.getShu());
                        examPageUserAnswer.setZong(dto.getZong());
                        examPageUserAnswer.setDa(dto.getDa());
                        examPageUserAnswer.setAccuracyRate(dto.getAccuracyRate());
                        examPageUserAnswer.setCoverageRate(dto.getCoverageRate());
                        allScores = allScores.add(examPageUserAnswer.getScore());
                    }else if (examPageUserAnswer.getType() == 4){

                        if(markQuestionUtils.check_answer_voice(examPageUserAnswer.getUserAnswer(),examPageUserAnswer.getRightAnswer())){
                            score = examPageUserAnswer.getScore();
                        }else {
                            score = BigDecimal.ZERO;
                        }
                        examPageUserAnswer.setUserScore(score);
                        allScores = allScores.add(score);
                    }else if (examPageUserAnswer.getType() == 5 || examPageUserAnswer.getType() == 6){

                        PanJuanParam dto = markQuestionUtils.check_answer_2D_xyq(examPageUserAnswer.getUserAnswer(),examPageUserAnswer.getRightAnswer());
                        score = BigDecimal.valueOf(dto.getCoverageRate().multiply(score).setScale(0, RoundingMode.HALF_UP).intValueExact());
                        examPageUserAnswer.setUserScore(score);
                        examPageUserAnswer.setBiao(dto.getBiao());
                        examPageUserAnswer.setCuo(dto.getCuo());
                        examPageUserAnswer.setWu(dto.getWu());
                        examPageUserAnswer.setShu(dto.getShu());
                        examPageUserAnswer.setZong(dto.getZong());
                        examPageUserAnswer.setDa(dto.getDa());
                        examPageUserAnswer.setAccuracyRate(dto.getAccuracyRate());
                        examPageUserAnswer.setCoverageRate(dto.getCoverageRate());
                        allScores = allScores.add(score);
                    }
                }
            }else if (600 == examPageUserAnswer.getShape()){

                BigDecimal questionFormScore = BigDecimal.ZERO;

                List<ExamPageUserChildAnswer> childAnswerList = examPageUserChildAnswerService.lambdaQuery()
                        .eq(ExamPageUserChildAnswer::getQuestionId,examPageUserAnswer.getQuestionId())
                        .eq(ExamPageUserChildAnswer::getNeedCorrect,0)
                        .list();
                for (ExamPageUserChildAnswer childAnswer : childAnswerList){
                    if (childAnswer.getRightAnswer().equals(childAnswer.getUserAnswer())){
                        childAnswer.setUserScore(childAnswer.getScore());
                        questionFormScore = questionFormScore.add(childAnswer.getUserScore());
                    }
                }
                examPageUserChildAnswerService.updateBatchById(childAnswerList);
                if (examPageUserAnswer.getNeedCorrect() == 0){
                    examPageUserAnswer.setUserScore(questionFormScore);
                    allScores = allScores.add(questionFormScore);
                }
            }
        }

        ExamPageUser examPageUser = lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getUserId,userId)
                .one();
        examPageUser.setAnswerStatus(2);
        examPageUser.setAnswerTs(DateUtil.now());

        if (examPageUser.getNeedCorrect() == 1){
            examPageUser.setScore(new BigDecimal(0));
        }else{
            examPageUser.setScore(allScores);

            ExamPageSet examPageSet = examPageSetService.lambdaQuery()
                    .eq(ExamPageSet::getExamId,examId)
                    .one();
            int passStatus = 0;
            if (allScores.compareTo(examPageSet.getScorePass()) >= 0){
                passStatus = 1;
                examPageUser.setPassStatus(passStatus);
            }
        }




        updateById(examPageUser);

        examPageUserLogService.lambdaUpdate().eq(ExamPageUserLog::getExamId,examId)
                .eq(ExamPageUserLog::getUserId,userId)
                .set(ExamPageUserLog::getSubmitStatus,1).update();
        return examPageUserAnswerService.updateBatchById(examPageUserAnswers)?BaseResponse.ok("交卷成功"):BaseResponse.fail("交卷失败");
    }

    @Override
    public BaseResponse<?> heartBeat(ExamUserQuery examUserQuery) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer userId = loginUser.getUser().getId();
        String examId = examUserQuery.getExamId();

        ExamPageUserLog examPageUserLog = examPageUserLogService.lambdaQuery()
                .eq(ExamPageUserLog::getExamId,examId)
                .eq(ExamPageUserLog::getUserId,userId)
                .orderByDesc(ExamPageUserLog::getLastUpdateTs)
                .last("limit 1").one();
        if(examPageUserLog == null) {
            return BaseResponse.fail("失败");
        }
        Integer submitStatus = examPageUserLog.getSubmitStatus();
        if(1 == submitStatus) {
            log.error("当前考试["+examId+","+userId+"],已提交");
            return BaseResponse.ok("成功",examPageUserLog.getExamLength());
        }
        String startTs = examPageUserLog.getStartTs();
        String lastUpdateTs = examPageUserLog.getLastUpdateTs();
        Integer examLength = examPageUserLog.getExamLength();
        String now = DateUtil.now();
        if(StringUtils.isBlank(lastUpdateTs)) {
            lastUpdateTs = new String(now);
        }
        int diff = examLength;
        if(StringUtils.isNotBlank(startTs)) {
            Long time = DateUtil.between(DateUtil.parse(lastUpdateTs), DateUtil.parse(startTs), DateUnit.MINUTE,true);
            int difftime = time.intValue();
            if(difftime >= 1) {
                diff = examLength+difftime;
                examPageUserLog.setExamLength(diff);
                examPageUserLog.setStartTs(lastUpdateTs);
            }
            log.error("当前考试["+examId+","+userId+"],已考时长:["+diff+"],时间间隔["+difftime+"],上次时间["+startTs+","+lastUpdateTs+"]");
        }else {
            examPageUserLog.setStartTs(DateUtil.now());
        }
        examPageUserLog.setLastUpdateTs(now);
        examPageUserLog.setUpdateTime(DateUtil.date().toJdkDate());
        return examPageUserLogService.updateById(examPageUserLog)?BaseResponse.ok("成功",diff):BaseResponse.fail("失败");
    }

    @Override
    public Page<ExamPageTeacherVo>pageTeacher(ExamTeacherQuery query, Pageable pageable){
        Page page = pageable.toPage();
        return examPageUserMapper.pageTeacher(page, query);
    }

    @Override
    public Page<ExamPageUserListVo>getExamUserListForExamId(ExamUserQuery query, Pageable pageable){
        Page page = pageable.toPage();
        return examPageUserMapper.getExamUserListForExamId(page, query);
    }

    @Override
    public BaseResponse<ExamPageAnswerVo> getExamUserAnswerInfo(String examPageUserId){
        ExamPageUser examPageUser = getById(examPageUserId);
        if (examPageUser == null){
            return BaseResponse.fail("考试用户不存在");
        }

        ExamPageAnswerVo vo = BeanUtil.copyProperties(examPageUser, ExamPageAnswerVo.class);
        Integer examId = examPageUser.getExamId();
        Integer userId = examPageUser.getUserId();

        LambdaQueryWrapper<ExamPageUserQuestion> examPageUserQuestionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examPageUserQuestionLambdaQueryWrapper.eq(ExamPageUserQuestion::getExamId, examId)
                .eq(ExamPageUserQuestion::getUserId, userId)
                .orderByAsc(ExamPageUserQuestion::getNumSort);
        List<ExamPageUserQuestion> questionList = examPageUserQuestionMapper.selectList(examPageUserQuestionLambdaQueryWrapper);
        Map<String, ExamPageUserAnswer> examPageUserAnswerMap = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .list().stream().collect(Collectors.toMap(ExamPageUserAnswer::getQuestionId, s->s));
        List<ExamPageUserQuestionVo> voList = BeanUtil.copyToList(questionList, ExamPageUserQuestionVo.class);
        for (ExamPageUserQuestionVo examPageUserQuestionVo : voList) {

            ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerMap.get(examPageUserQuestionVo.getId());
            examPageUserQuestionVo.setUserAnswer(examPageUserAnswer == null ? "" : examPageUserAnswer.getUserAnswer());
            examPageUserQuestionVo.setUserScore(examPageUserAnswer == null ? new BigDecimal(0) : examPageUserAnswer.getUserScore());
            examPageUserQuestionVo.setNeedCorrect(examPageUserAnswer.getNeedCorrect());
            examPageUserQuestionVo.setCorrectId(examPageUserAnswer.getCorrectId().toString());
            examPageUserQuestionVo.setCorrectTime(examPageUserAnswer.getCorrectTime());

            if (examPageUserQuestionVo.getIsForm() == 1){
                List<ExamPageUserQuestionFormTitleVo> examPageUserQuestionFormTitleVoList = new ArrayList<>();
                List<ExamPageUserQuestionFormTitle> examPageUserQuestionFormTitleList = examPageUserQuestionFormTitleService.lambdaQuery()
                        .eq(ExamPageUserQuestionFormTitle::getPid,examPageUserQuestionVo.getId())
                        .orderByAsc(ExamPageUserQuestionFormTitle::getSort)
                        .list();
                Map<String, List<ExamPageUserQuestionChild>> examPageUserQuestionChildMap = examPageUserQuestionChildService.lambdaQuery()
                        .eq(ExamPageUserQuestionChild::getQuestionId,examPageUserQuestionVo.getId())
                        .orderByAsc(ExamPageUserQuestionChild::getSort)
                        .list()
                        .stream()
                        .collect(Collectors.groupingBy(ExamPageUserQuestionChild::getPid));
                Map<String, ExamPageUserChildAnswer> examPageUserChildAnswerMap = examPageUserChildAnswerService.lambdaQuery()
                        .eq(ExamPageUserChildAnswer::getQuestionId,examPageUserQuestionVo.getId())
                        .list()
                        .stream()
                        .collect(Collectors.toMap(ExamPageUserChildAnswer::getQuestionChildId, s->s));
                for (ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle : examPageUserQuestionFormTitleList) {
                    ExamPageUserQuestionFormTitleVo examPageUserQuestionFormTitleVo = BeanUtil.copyProperties(examPageUserQuestionFormTitle, ExamPageUserQuestionFormTitleVo.class);
                    List<ExamPageUserQuestionChildVo> examPageUserQuestionChildVoList = BeanUtil.copyToList(examPageUserQuestionChildMap.get(examPageUserQuestionFormTitle.getId()), ExamPageUserQuestionChildVo.class);
                    if (examPageUserQuestionChildVoList != null){
                        for (ExamPageUserQuestionChildVo examPageUserQuestionChildVo : examPageUserQuestionChildVoList) {
                            examPageUserQuestionChildVo.setUserAnswer(examPageUserChildAnswerMap.get(examPageUserQuestionChildVo.getId()) == null ? "" : examPageUserChildAnswerMap.get(examPageUserQuestionChildVo.getId()).getUserAnswer());
                            examPageUserQuestionChildVo.setRightAnswer(examPageUserChildAnswerMap.get(examPageUserQuestionChildVo.getId()) == null ? "" : examPageUserChildAnswerMap.get(examPageUserQuestionChildVo.getId()).getRightAnswer());
                        }
                    }
                    examPageUserQuestionFormTitleVo.setExamPageUserQuestionChildVoList(examPageUserQuestionChildVoList);
                    examPageUserQuestionFormTitleVoList.add(examPageUserQuestionFormTitleVo);
                }
                examPageUserQuestionVo.setExamPageUserQuestionFormTitleVoList(examPageUserQuestionFormTitleVoList);
            }
        }
        vo.setExamPageUserQuestionVoList(voList);
        return BaseResponse.ok("成功",vo);
    }

    @Override
    public BaseResponse<?> correct(ExamCorrectParam param){
        LoginUser loginUser = redisUtil.getInfoByToken();
        ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getQuestionId, param.getUserQuestionId()).last("limit 1")
                .one();

        if (examPageUserAnswer.getScore().compareTo(new BigDecimal(param.getScore())) < 0){
            return BaseResponse.fail("分数不能超过该题总分");
        }
        examPageUserAnswer.setCorrectId(loginUser.getUser().getId());
        examPageUserAnswer.setCorrectTime(DateUtil.date());
        examPageUserAnswer.setUserScore(new BigDecimal(param.getScore()));

        examPageUserAnswerService.updateById(examPageUserAnswer);

        ThreadUtil.execAsync(()->{

            sumScore(examPageUserAnswer.getUserId(),examPageUserAnswer.getExamId());
        });
        return BaseResponse.ok("批改成功");
    }

    @Override
    public BaseResponse<?> correctChild(ExamCorrectChildParam param){
        LoginUser loginUser = redisUtil.getInfoByToken();
        ExamPageUserChildAnswer examPageUserChildAnswer = examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionChildId, param.getChildQuestionId())
                .last("limit 1")
                .one();

        if (examPageUserChildAnswer.getScore().compareTo(new BigDecimal(param.getScore())) < 0 ){
            return BaseResponse.fail("分数不能超过该子题总分");
        }
        examPageUserChildAnswer.setCorrectId(loginUser.getUser().getId());
        examPageUserChildAnswer.setCorrectTime(DateUtil.date());
        examPageUserChildAnswer.setUserScore(new BigDecimal(param.getScore()));

        examPageUserChildAnswerService.updateById(examPageUserChildAnswer);


        sumQuestionFormScore(examPageUserChildAnswer.getQuestionId());
        return BaseResponse.ok("批改成功");
    }

    public void sumQuestionFormScore(String questionFormId){

        if (examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionId,questionFormId)
                .eq(ExamPageUserChildAnswer::getNeedCorrect,1)
                .isNull(ExamPageUserChildAnswer::getCorrectId)
                .count() > 0){
            return;
        }
        LoginUser loginUser = redisUtil.getInfoByToken();
        BigDecimal allScore = new BigDecimal(0);

        List<ExamPageUserChildAnswer> examPageUserChildAnswerList = examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionId,questionFormId)
                .list();
        for (ExamPageUserChildAnswer examPageUserChildAnswer : examPageUserChildAnswerList) {
            allScore = allScore.add(examPageUserChildAnswer.getUserScore());
        }
        ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getQuestionId,questionFormId)
                .last("limit 1")
                .one();
        examPageUserAnswer.setUserScore(allScore);
        examPageUserAnswer.setCorrectId(loginUser.getUser().getId());
        examPageUserAnswer.setCorrectTime(DateUtil.date());
        examPageUserAnswerService.updateById(examPageUserAnswer);


        sumScore(examPageUserAnswer.getUserId(),examPageUserAnswer.getExamId());
    }

    @Override
    public void sumScore(Integer userId,Integer examId){

        if (examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .eq(ExamPageUserAnswer::getNeedCorrect,1)
                .isNull(ExamPageUserAnswer::getCorrectId)
                .count() > 0){
            return;
        }
        BigDecimal allScore = new BigDecimal(0);

        List<ExamPageUserAnswer> examPageUserAnswerList = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .list();
        for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswerList) {
            allScore = allScore.add(examPageUserAnswer.getUserScore());
        }

        ExamPageUser examPageUser = lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getUserId,userId)
                .last("limit 1")
                .one();
        examPageUser.setOnCorrect(1);
        examPageUser.setScore(allScore);
        examPageUser.setScoreTs(DateUtil.now());

        ExamPageSet examPageSet = examPageSetService.lambdaQuery()
                .eq(ExamPageSet::getExamId,examId)
                .one();
        int passStatus = 0;
        if (allScore.compareTo(examPageSet.getScorePass()) >= 0){
            passStatus = 1;
            examPageUser.setPassStatus(passStatus);
        }
        updateById(examPageUser);
    }

    @Override
    public List<ExamPageUserVo> getExamPageUserName(Integer classId) {
        return examPageUserMapper.getExamPageUserName(classId);
    }

    @Override
    public Page<ExamPageUserListVo> getExamUserListForExamIdByUserIds(Integer examId, List<Integer> userIds, Pageable pageable) {
        return examPageUserMapper.getExamUserListForExamIdByUserIds(examId, userIds, pageable.toPage());
    }

}