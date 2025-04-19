package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.*;
import com.xinkao.erp.exam.mapper.ExamPageUserMapper;
import com.xinkao.erp.exam.mapper.ExamPageUserQuestionMapper;
import com.xinkao.erp.exam.model.vo.ExamPageUserQuestionVo;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.model.vo.ExamProgressVo;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.param.ExamPageUserAnswerParam;
import com.xinkao.erp.exam.model.param.SubmitParam;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.service.*;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;
import com.xinkao.erp.exam.vo.ExamPageUserListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamPageUserServiceImpl extends BaseServiceImpl<ExamPageUserMapper, ExamPageUser> implements ExamPageUserService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ExamPageUserMapper examPageUserMapper;
    @Autowired
    private ExamService examService;
    @Autowired
    private ExamPageUserQuestionMapper examPageUserQuestionMapper;
    @Autowired
    private ExamPageUserAnswerService examPageUserAnswerService;
    @Autowired
    private ExamPageUserLogService examPageUserLogService;
    @Autowired
    private ExamPageSetService examPageSetService;

    @Override
    public Page<ExamUserVo> page(BasePageQuery query, Pageable pageable){
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
        }
        vo.setQuestionVoList(BeanUtil.copyToList(questionList, ExamPageUserQuestionVo.class));
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
            //如果是单选题或判断题，判断是否正确
            if (100 == examPageUserAnswer.getShape()|| 300 == examPageUserAnswer.getShape()){
                if (examPageUserAnswer.getUserAnswer().equals(examPageUserAnswer.getRightAnswer())){
                    examPageUserAnswer.setUserScore(examPageUserAnswer.getScore());
                    allScores += examPageUserAnswer.getScore();
                }
            }else if (200 == examPageUserAnswer.getShape()){
                //如果是多选题，判断是否全部正确或部分正确
                List<String> teaAnswerList = Arrays.asList(examPageUserAnswer.getRightAnswer().split(""));
                List<String> stuAnswerList = Arrays.asList(examPageUserAnswer.getUserAnswer().split(""));
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
            }else if (500 == examPageUserAnswer.getShape()){
                //操作题
            }
        }
        //准备更新exam_page_stu表
        ExamPageUser examPageUser = lambdaQuery()
                .eq(ExamPageUser::getExamId,examId)
                .eq(ExamPageUser::getUserId,userId)
                .one();
        examPageUser.setAnswerStatus(2);
        examPageUser.setAnswerTs(DateUtil.now());
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
//        if (1 == submitParam.getForceDeadline()){
//            examPageUser.setForceDeadline(1);
//        }
        updateById(examPageUser);
        //将心跳停止
        examPageUserLogService.lambdaUpdate().eq(ExamPageUserLog::getExamId,examId)
                .eq(ExamPageUserLog::getUserId,userId)
                .set(ExamPageUserLog::getSubmitStatus,1).update();
        Map<String,Integer>map = new HashMap<>();
        map.put("allScores", allScores);
        map.put("passStatus", passStatus);
        return examPageUserAnswerService.updateBatchById(examPageUserAnswers)?BaseResponse.ok("成功",map):BaseResponse.fail("失败");
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
}