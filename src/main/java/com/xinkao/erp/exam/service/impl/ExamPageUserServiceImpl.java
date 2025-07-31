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
        ExamPageUser examPageUser = lambdaQuery().eq(ExamPageUser::getExamId, examId)
                .eq(ExamPageUser::getUserId, userId)
                .one();
        ExamUserVo vo = BeanUtil.copyProperties(examPageUser, ExamUserVo.class);
        vo.setExamName(examService.getById(vo.getExamId()).getExamName());
        //插入题目详情
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
            //查询是否已作答
            ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerMap.get(examPageUserQuestion.getId());
            examPageUserQuestion.setAnswer("");
            examPageUserQuestion.setUserAnswer(examPageUserAnswer == null ? "" : examPageUserAnswer.getUserAnswer());
            //如果是填空题，则去掉数据中的options
            if (examPageUserQuestion.getShape() == 300) {
                examPageUserQuestion.setOptions("");
            }
        }
        vo.setQuestionVoList(BeanUtil.copyToList(questionList, ExamPageUserQuestionVo.class));
        return BaseResponse.ok("成功",vo);
    }

    @Override
    public BaseResponse<ExamPageUserQuestionVo> getUserQuestionInfo(String id) {
        //题目详情
        ExamPageUserQuestion examPageUserQuestion = examPageUserQuestionMapper.selectById(id);
        Map<String, ExamPageUserAnswer> examPageUserAnswerMap = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getQuestionId,id)
                .list().stream().collect(Collectors.toMap(ExamPageUserAnswer::getQuestionId, s->s));
        //查询是否已作答
        ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerMap.get(examPageUserQuestion.getId());
        examPageUserQuestion.setAnswer("");
        examPageUserQuestion.setUserAnswer(examPageUserAnswer == null ? "" : examPageUserAnswer.getUserAnswer());
        ExamPageUserQuestionVo vo = BeanUtil.copyProperties(examPageUserQuestion, ExamPageUserQuestionVo.class);
        //如果为题目单，则查询获取详情进行插入
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
            for (ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle : examPageUserQuestionFormTitleList) {
                ExamPageUserQuestionFormTitleVo examPageUserQuestionFormTitleVo = BeanUtil.copyProperties(examPageUserQuestionFormTitle, ExamPageUserQuestionFormTitleVo.class);
                List<ExamPageUserQuestionChildVo> examPageUserQuestionChildVoList = BeanUtil.copyToList(examPageUserQuestionChildMap.get(examPageUserQuestionFormTitle.getId()), ExamPageUserQuestionChildVo.class);
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
        //查询答题进度
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
        //更新答案
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
        //修改答题开始时间和结束时间，如果开始时间为空则修改，如果已没有未答题，则新增结束时间
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
        //更新答案
        ExamPageUserChildAnswer examPageUserChildAnswer = examPageUserChildAnswerService.getById(param.getChildId());
        if (examPageUserChildAnswer == null){
            return BaseResponse.fail("题目模板不存在");
        }
        examPageUserChildAnswer.setUserAnswer(param.getAnswer());
        examPageUserChildAnswer.setAnswerStatus(1);
        examPageUserChildAnswerService.updateById(examPageUserChildAnswer);
        //如果全部子题已经都提交，则修改该题目为已作答
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
            //如果该题目单做完之后，验证该套试卷是否还有未作答
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
        //获取答题详情
        List<ExamPageUserAnswer> examPageUserAnswers = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .orderByAsc(ExamPageUserAnswer::getNumSort)
                .list();
        int allScores = 0;
        for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswers) {
            //如果是单选题，判断是否正确,//填空题(暂时先按照全部正确才给分)
            if (100 == examPageUserAnswer.getShape() || 300 == examPageUserAnswer.getShape()){
                if (examPageUserAnswer.getUserAnswer().equals(examPageUserAnswer.getRightAnswer())){
                    examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                    allScores += examPageUserAnswer.getScore();
                }
            }else if (200 == examPageUserAnswer.getShape()){
                //如果是多选题，判断是否全部正确或部分正确
                List<String> teaAnswerList = Arrays.asList(examPageUserAnswer.getRightAnswer().split("&%&"));
                List<String> stuAnswerList = Arrays.asList(examPageUserAnswer.getUserAnswer().split("&%&"));
                if (teaAnswerList.containsAll(stuAnswerList)) {
                    if (teaAnswerList.size() == stuAnswerList.size()) {// 全选对
                        examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                        allScores += examPageUserAnswer.getScore();
                    } else {// 部分选对
                        examPageUserAnswer.setUserScore(examPageUserAnswer.getScorePart());
                        allScores += examPageUserAnswer.getScorePart();
                    }
                }
            }else if (400 == examPageUserAnswer.getShape()){
                //问答题
                if (examPageUserAnswer.getNeedCorrect() == 0){
                    if (examPageUserAnswer.getRightAnswer().equals(examPageUserAnswer.getUserAnswer())){
                        examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                        allScores += examPageUserAnswer.getUserScore();
                    }
                }
            }else if (500 == examPageUserAnswer.getShape()){
                //操作题
                Integer score = 0;
                if (examPageUserAnswer.getNeedCorrect() == 0){
                    if (examPageUserAnswer.getType() == 1 || examPageUserAnswer.getType() == 3){
                        //图像标注与OCR标注直接验证与答案完全一致则可以得分
                        if (examPageUserAnswer.getUserAnswer().equals(examPageUserAnswer.getRightAnswer())){
                            examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                            allScores += examPageUserAnswer.getUserScore();
                        }
                    }else if (examPageUserAnswer.getType() == 2 || examPageUserAnswer.getType() == 7){
                        //3D点云标注或者2D+3D标注
                        PanJuanParam dto = pointSubmitUtil.get3DPointScore(examPageUserAnswer);
                        examPageUserAnswer.setUserScore(dto.getScore());
                        //给examPageUserAnswer赋值该题的作答各维度数量
                        examPageUserAnswer.setBiao(dto.getBiao());
                        examPageUserAnswer.setCuo(dto.getCuo());
                        examPageUserAnswer.setWu(dto.getWu());
                        examPageUserAnswer.setShu(dto.getShu());
                        examPageUserAnswer.setZong(dto.getZong());
                        examPageUserAnswer.setDa(dto.getDa());
                        examPageUserAnswer.setAccuracyRate(dto.getAccuracyRate());
                        examPageUserAnswer.setCoverageRate(dto.getCoverageRate());
                        allScores += score;
                    }else if (examPageUserAnswer.getType() == 4){
                        //语音标注
                        if(markQuestionUtils.check_answer_voice(examPageUserAnswer.getUserAnswer(),examPageUserAnswer.getRightAnswer())){
                            score = examPageUserAnswer.getScore();
                        }else {
                            score = 0;
                        }
                        examPageUserAnswer.setUserScore(score);
                        allScores += score;
                    }else if (examPageUserAnswer.getType() == 5 || examPageUserAnswer.getType() == 6){
                        //2D标注、人脸关键点标注
                        PanJuanParam dto = markQuestionUtils.check_answer_2D_xyq(examPageUserAnswer.getUserAnswer(),examPageUserAnswer.getRightAnswer());
                        score = dto.getCoverageRate().multiply(new BigDecimal(score)).setScale(0, RoundingMode.HALF_UP).intValueExact();
                        examPageUserAnswer.setUserScore(score);
                        examPageUserAnswer.setBiao(dto.getBiao());
                        examPageUserAnswer.setCuo(dto.getCuo());
                        examPageUserAnswer.setWu(dto.getWu());
                        examPageUserAnswer.setShu(dto.getShu());
                        examPageUserAnswer.setZong(dto.getZong());
                        examPageUserAnswer.setDa(dto.getDa());
                        examPageUserAnswer.setAccuracyRate(dto.getAccuracyRate());
                        examPageUserAnswer.setCoverageRate(dto.getCoverageRate());
                        allScores += score;
                    }
                }
            }
        }
        //准备更新exam_page_stu表
        ExamPageUser examPageUser = lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getUserId,userId)
                .one();
        examPageUser.setAnswerStatus(2);
        examPageUser.setAnswerTs(DateUtil.now());
        //判断是否需要批改，如果需要则先不赋分
        if (examPageUser.getNeedCorrect() == 1){
            examPageUser.setScore(0);
        }else{
            examPageUser.setScore(allScores);
            //计算是否合格
            ExamPageSet examPageSet = examPageSetService.lambdaQuery()
                    .eq(ExamPageSet::getExamId,examId)
                    .one();
            int passStatus = 0;
            if (allScores >= examPageSet.getScorePass()){
                passStatus = 1;
                examPageUser.setPassStatus(passStatus);
            }
        }

//        if (1 == submitParam.getForceDeadline()){
//            examPageUser.setForceDeadline(1);
//        }
        updateById(examPageUser);
        //将心跳停止
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
        //获取记录
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
        //获取答题详情及学生答案
        ExamPageAnswerVo vo = BeanUtil.copyProperties(examPageUser, ExamPageAnswerVo.class);
        Integer examId = examPageUser.getExamId();
        Integer userId = examPageUser.getUserId();
        //插入题目详情
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
            //查询是否已作答
            ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerMap.get(examPageUserQuestionVo.getId());
            examPageUserQuestionVo.setUserAnswer(examPageUserAnswer == null ? "" : examPageUserAnswer.getUserAnswer());
            examPageUserQuestionVo.setUserScore(examPageUserAnswer == null ? 0 : examPageUserAnswer.getUserScore());
            examPageUserQuestionVo.setNeedCorrect(examPageUserAnswer.getNeedCorrect());
            examPageUserQuestionVo.setCorrectId(examPageUserAnswer.getCorrectId().toString());
            examPageUserQuestionVo.setCorrectTime(examPageUserAnswer.getCorrectTime());
            //如果为题目单，则查询获取详情进行插入
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
                for (ExamPageUserQuestionFormTitle examPageUserQuestionFormTitle : examPageUserQuestionFormTitleList) {
                    ExamPageUserQuestionFormTitleVo examPageUserQuestionFormTitleVo = BeanUtil.copyProperties(examPageUserQuestionFormTitle, ExamPageUserQuestionFormTitleVo.class);
                    List<ExamPageUserQuestionChildVo> examPageUserQuestionChildVoList = BeanUtil.copyToList(examPageUserQuestionChildMap.get(examPageUserQuestionFormTitle.getId()), ExamPageUserQuestionChildVo.class);
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
        //判断分数是否超过该题总分
        if (examPageUserAnswer.getScore() < Integer.parseInt(param.getScore())){
            return BaseResponse.fail("分数不能超过该题总分");
        }
        examPageUserAnswer.setCorrectId(loginUser.getUser().getId());
        examPageUserAnswer.setCorrectTime(DateUtil.date());
        examPageUserAnswer.setUserScore(Integer.parseInt(param.getScore()));
        //修改
        examPageUserAnswerService.updateById(examPageUserAnswer);
        //异步执行计算总分
        ThreadUtil.execAsync(()->{
            //计算总分
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
        //判断分数是否超过该题总分
        if (examPageUserChildAnswer.getScore() < Integer.parseInt(param.getScore())){
            return BaseResponse.fail("分数不能超过该题总分");
        }
        examPageUserChildAnswer.setCorrectId(loginUser.getUser().getId());
        examPageUserChildAnswer.setCorrectTime(DateUtil.date());
        examPageUserChildAnswer.setUserScore(Integer.parseInt(param.getScore()));
        //修改
        examPageUserChildAnswerService.updateById(examPageUserChildAnswer);
        //异步执行计算总分
        ThreadUtil.execAsync(()->{
            //计算总分
            sumQuestionFormScore(examPageUserChildAnswer.getQuestionId());
        });
        return BaseResponse.ok("批改成功");
    }

    public void sumQuestionFormScore(String questionFormId){
        //查询表中是否还有待批改的题目
        if (examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionId,questionFormId)
                .eq(ExamPageUserChildAnswer::getNeedCorrect,1)
                .isNull(ExamPageUserChildAnswer::getCorrectId)
                .count() > 0){
            return;
        }
        LoginUser loginUser = redisUtil.getInfoByToken();
        Integer allScore = 0;
        //如果没有，则计算总分进行修改和状态更新
        List<ExamPageUserChildAnswer> examPageUserChildAnswerList = examPageUserChildAnswerService.lambdaQuery()
                .eq(ExamPageUserChildAnswer::getQuestionId,questionFormId)
                .list();
        for (ExamPageUserChildAnswer examPageUserChildAnswer : examPageUserChildAnswerList) {
            allScore += examPageUserChildAnswer.getUserScore();
        }
        ExamPageUserAnswer examPageUserAnswer = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getQuestionId,questionFormId)
                .last("limit 1")
                .one();
        examPageUserAnswer.setUserScore(allScore);
        examPageUserAnswer.setCorrectId(loginUser.getUser().getId());
        examPageUserAnswerService.updateById(examPageUserAnswer);
        //再看是否为最后的题目单的最后一题，如果是则异步执行计算总分
        ThreadUtil.execAsync(()->{
            //计算总分
            sumScore(examPageUserAnswer.getUserId(),examPageUserAnswer.getExamId());
        });
    }

    @Override
    public void sumScore(Integer userId,Integer examId){
        //查询表中是否还有待批改的题目
        if (examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .eq(ExamPageUserAnswer::getNeedCorrect,1)
                .isNull(ExamPageUserAnswer::getCorrectId)
                .count() > 0){
            return;
        }
        Integer allScore = 0;
        //如果没有，则计算总分进行修改和状态更新
        List<ExamPageUserAnswer> examPageUserAnswerList = examPageUserAnswerService.lambdaQuery()
                .eq(ExamPageUserAnswer::getExamId,examId)
                .eq(ExamPageUserAnswer::getUserId,userId)
                .list();
        for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswerList) {
            allScore += examPageUserAnswer.getUserScore();
        }
        //修改分数及状态
        ExamPageUser examPageUser = lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getUserId,userId)
                .last("limit 1")
                .one();
        examPageUser.setOnCorrect(1);
        examPageUser.setScore(allScore);
        examPageUser.setScoreTs(DateUtil.now());
        //计算是否合格
        ExamPageSet examPageSet = examPageSetService.lambdaQuery()
                .eq(ExamPageSet::getExamId,examId)
                .one();
        int passStatus = 0;
        if (allScore >= examPageSet.getScorePass()){
            passStatus = 1;
            examPageUser.setPassStatus(passStatus);
        }
        updateById(examPageUser);
    }

    @Override
    public List<ExamPageUserVo> getExamPageUserName(Integer classId) {
        return examPageUserMapper.getExamPageUserName(classId);
    }
}