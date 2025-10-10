package com.xinkao.erp.summary.controller;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import com.xinkao.erp.exam.model.vo.ExamPageUserVo;
import com.xinkao.erp.exam.param.ExamPageUserParam;
import com.xinkao.erp.exam.service.ExamClassService;
import com.xinkao.erp.exam.service.ExamPageUserAnswerService;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.exam.service.ExamService;
import com.xinkao.erp.exercise.entity.ExerciseRecords;
import com.xinkao.erp.exercise.entity.InstantFeedbacks;
import com.xinkao.erp.exercise.query.ExerciseRecordsQuery;
import com.xinkao.erp.exercise.query.ExerciseRecordsVo;
import com.xinkao.erp.exercise.service.ExerciseRecordsService;
import com.xinkao.erp.exercise.service.InstantFeedbacksService;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.service.QuestionTypeService;
import com.xinkao.erp.summary.entity.Shape;
import com.xinkao.erp.summary.param.ClassSummaryParam;
import com.xinkao.erp.summary.param.SummaryParam;
import com.xinkao.erp.summary.param.SummaryStuIDParam;
import com.xinkao.erp.summary.param.SummaryStuParam;
import com.xinkao.erp.summary.service.ShapeService;
import com.xinkao.erp.summary.vo.ExamClVo;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    private ExerciseRecordsService exerciseRecordsService;
    @Autowired
    private ExamPageUserService examPageUserService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private InstantFeedbacksService instantFeedbacksService;
    @Autowired
    private ExamPageUserAnswerService examPageUserAnswerService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private ShapeService shapeService;
    @Autowired
    private ExamClassService examClassService;
    @Autowired
    private ExamService examService;

    @RequestMapping("/stuSummary")
    @PrimaryDataSource
    public BaseResponse<?> stuSummary(@RequestBody SummaryStuParam  summaryStuParam) {
        List<Integer> stuIds = new ArrayList<>();
        if (summaryStuParam.getStuId() == null|| summaryStuParam.getStuId().size() == 0) {
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll.getUser().getRoleId()==1){
            }else if (loginUserAll.getUser().getRoleId()==3){
                stuIds.add(loginUserAll.getUser().getId());
                summaryStuParam.setStuId(stuIds);
            }else if (loginUserAll.getUser().getRoleId()==2){
                List<ClassInfo> classInfoList = classInfoService.lambdaQuery().eq(ClassInfo::getDirectorId, loginUserAll.getUser().getId()).eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list();
                LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(User::getIsDel, CommonEnum.IS_DEL.NO.getCode());
                wrapper.in(User::getClassId, classInfoList.stream().map(ClassInfo::getId).collect(Collectors.toList()));
                List<User> userList = userService.list(wrapper);
                stuIds = userList.stream().map(User::getId).collect(Collectors.toList());
                summaryStuParam.setStuId(stuIds);
            }
        }
        if (summaryStuParam.getType() == 0){
            List<InstantFeedbacks> instantFeedbacksList = instantFeedbacksService.list(new LambdaQueryWrapper<InstantFeedbacks>().eq(InstantFeedbacks::getShape, 500).in(InstantFeedbacks::getUserId,summaryStuParam.getStuId() ));
            instantFeedbacksList.forEach(instantFeedbacks -> {
                instantFeedbacks.setRealName(userService.getById(instantFeedbacks.getUserId()).getRealName());
            });
            return BaseResponse.ok(instantFeedbacksList);
        }else if (summaryStuParam.getType() == 1){
            LambdaQueryWrapper<ExamPageUser> wrapper = Wrappers.lambdaQuery();
            if (summaryStuParam.getStuId() != null&& summaryStuParam.getStuId().size()>0){
                wrapper.in(ExamPageUser::getUserId,summaryStuParam.getStuId());
            }
            wrapper.orderByAsc(ExamPageUser::getCreateTime);
            List<ExamPageUser> examPageUserList = examPageUserService.list(wrapper);
            List<ExamPageUserParam> examPageUserVoList = new ArrayList<>();
            for (ExamPageUser examPageUser : examPageUserList) {
                ExamPageUserParam examPageUserParam = new ExamPageUserParam();
                BeanUtils.copyProperties(examPageUser, examPageUserParam);
                examPageUserParam.setRealName(userService.getById(examPageUser.getUserId()).getRealName());
                examPageUserVoList.add(examPageUserParam);
            }
            int zong = 0;
            int biao = 0;
            int cuo = 0;
            int wu = 0;
            int shu = 0;
            int da = 0;
            double accuracyRate = 0;
            double coverageRate = 0;
            for (ExamPageUserParam examPageUserParam : examPageUserVoList) {
                List<ExamPageUserAnswer> examPageUserQuestionList = examPageUserAnswerService.list(new LambdaQueryWrapper<ExamPageUserAnswer>().eq(ExamPageUserAnswer::getShape,500).eq(ExamPageUserAnswer::getExamId, examPageUserParam.getExamId()));
                for (ExamPageUserAnswer examPageUserAnswer : examPageUserQuestionList){
                    zong = zong + examPageUserAnswer.getZong();
                    da = da + examPageUserAnswer.getDa();
                    biao = biao + examPageUserAnswer.getBiao();
                    wu = wu + examPageUserAnswer.getWu();
                    cuo = cuo + examPageUserAnswer.getCuo();
                    shu = shu + examPageUserAnswer.getShu();
                    if (examPageUserAnswer.getAccuracyRate() != null) {
                        accuracyRate += examPageUserAnswer.getAccuracyRate().doubleValue();
                    }
                    if (examPageUserAnswer.getCoverageRate() != null) {
                        coverageRate += examPageUserAnswer.getCoverageRate().doubleValue();
                    }
                }
                int size = examPageUserQuestionList.size();
                examPageUserParam.setDa(size!=0?da / size:0);
                examPageUserParam.setBiao(size!=0?biao / size:0);
                examPageUserParam.setCuo(size!=0?cuo / size:0);
                examPageUserParam.setWu(size!=0?wu / size:0);
                examPageUserParam.setShu(size!=0?shu / size:0);
                examPageUserParam.setZong(size!=0?zong / size:0);
                if (size!=0){
                    examPageUserParam.setAccuracyRate(new BigDecimal(accuracyRate/size).setScale(2, RoundingMode.HALF_UP));
                    examPageUserParam.setCoverageRate(new BigDecimal(coverageRate/size).setScale(2, RoundingMode.HALF_UP));
                }else {
                    examPageUserParam.setAccuracyRate(new BigDecimal(0));
                    examPageUserParam.setCoverageRate(new BigDecimal(0));
                }

            }
            return BaseResponse.ok(examPageUserVoList);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    @RequestMapping("/stuSummaryByUserRole/{type}")
    @PrimaryDataSource
    public BaseResponse<?> stuSummaryByUserRole(@PathVariable int type) {
        List<Integer> userIds = null;
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        if (loginUserAll.getUser().getRoleId() == 1){
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getRoleId,2);
            List<User> userList = userService.list(wrapper);
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        }else if (loginUserAll.getUser().getRoleId() == 2){
            LambdaQueryWrapper<ClassInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ClassInfo::getDirectorId,loginUserAll.getUser().getId());
            List<Integer> classIds = classInfoService.list(wrapper).stream().map(ClassInfo::getId).collect(Collectors.toList());
            LambdaQueryWrapper<User> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.in(User::getClassId,classIds);
            wrapper1.eq(User::getRoleId,3);
            List<User> userList = userService.list(wrapper1);
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        }else {
            return BaseResponse.fail("参数错误");
        }
        if (userIds != null&&type == 0){
            LambdaQueryWrapper<ExerciseRecords> wrapper = Wrappers.lambdaQuery();
            wrapper.in(ExerciseRecords::getUserId,userIds);
            wrapper.orderByAsc(ExerciseRecords::getCreateTime);
            List<ExerciseRecords> exerciseRecordsList = exerciseRecordsService.list(wrapper);
            List<ExerciseRecordsVo> exerciseRecordsVoList = new ArrayList<>();
            for (ExerciseRecords exerciseRecords : exerciseRecordsList) {
                ExerciseRecordsVo exerciseRecordsVo = new ExerciseRecordsVo();
                BeanUtils.copyProperties(exerciseRecords, exerciseRecordsVo);
                exerciseRecordsVoList.add(exerciseRecordsVo);
            }
            int zong = 0;
            int biao = 0;
            int cuo = 0;
            int wu = 0;
            int shu = 0;
            int da = 0;
            double accuracyRate = 0;
            double coverageRate = 0;
            for (ExerciseRecordsVo exerciseRecordsVo : exerciseRecordsVoList) {
                List<InstantFeedbacks> instantFeedbacksList = instantFeedbacksService.list(new LambdaQueryWrapper<InstantFeedbacks>().eq(InstantFeedbacks::getShape, 500).eq(InstantFeedbacks::getRecordId, exerciseRecordsVo.getId()));
                for (InstantFeedbacks instantFeedbacks : instantFeedbacksList) {
                    zong = zong + instantFeedbacks.getZong();
                    da = da + instantFeedbacks.getDa();
                    biao = biao + instantFeedbacks.getBiao();
                    wu = wu + instantFeedbacks.getWu();
                    cuo = cuo + instantFeedbacks.getCuo();
                    shu = shu + instantFeedbacks.getShu();
                    if (instantFeedbacks.getAccuracyRate() != null) {
                        accuracyRate += instantFeedbacks.getAccuracyRate().doubleValue();
                    }
                    if (instantFeedbacks.getCoverageRate() != null) {
                        coverageRate += instantFeedbacks.getCoverageRate().doubleValue();
                    }
                }
                int size = instantFeedbacksList.size();
                exerciseRecordsVo.setDa(size!=0?da / size:0);
                exerciseRecordsVo.setBiao(size!=0?biao / size:0);
                exerciseRecordsVo.setCuo(size!=0?cuo / size:0);
                exerciseRecordsVo.setWu(size!=0?wu / size:0);
                exerciseRecordsVo.setShu(size!=0?shu / size:0);
                exerciseRecordsVo.setZong(size!=0?zong / size:0);
                if (size!=0){
                    exerciseRecordsVo.setAccuracyRate(new BigDecimal(accuracyRate/size).setScale(2, RoundingMode.HALF_UP));
                    exerciseRecordsVo.setCoverageRate(new BigDecimal(coverageRate/size).setScale(2, RoundingMode.HALF_UP));
                }else {
                    exerciseRecordsVo.setAccuracyRate(new BigDecimal(0));
                    exerciseRecordsVo.setCoverageRate(new BigDecimal(0));
                }
            }
            return BaseResponse.ok(exerciseRecordsVoList);
        }else if (userIds != null&&type == 1){
            LambdaQueryWrapper<ExamPageUser> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ExamPageUser::getUserId,userIds);
            wrapper.orderByAsc(ExamPageUser::getCreateTime);
            List<ExamPageUser> examPageUserList = examPageUserService.list(wrapper);
            List<ExamPageUserParam> examPageUserVoList = new ArrayList<>();
            for (ExamPageUser examPageUser : examPageUserList) {
                ExamPageUserParam examPageUserParam = new ExamPageUserParam();
                BeanUtils.copyProperties(examPageUser, examPageUserParam);
                examPageUserVoList.add(examPageUserParam);
            }
            int zong = 0;
            int biao = 0;
            int cuo = 0;
            int wu = 0;
            int shu = 0;
            int da = 0;
            double accuracyRate = 0;
            double coverageRate = 0;
            for (ExamPageUserParam examPageUserParam : examPageUserVoList) {
                List<ExamPageUserAnswer> examPageUserQuestionList = examPageUserAnswerService.list(new LambdaQueryWrapper<ExamPageUserAnswer>().eq(ExamPageUserAnswer::getShape,500).eq(ExamPageUserAnswer::getExamId, examPageUserParam.getExamId()));
                for (ExamPageUserAnswer examPageUserAnswer : examPageUserQuestionList){
                    zong = zong + examPageUserAnswer.getZong();
                    da = da + examPageUserAnswer.getDa();
                    biao = biao + examPageUserAnswer.getBiao();
                    wu = wu + examPageUserAnswer.getWu();
                    cuo = cuo + examPageUserAnswer.getCuo();
                    shu = shu + examPageUserAnswer.getShu();
                    if (examPageUserAnswer.getAccuracyRate() != null) {
                        accuracyRate += examPageUserAnswer.getAccuracyRate().doubleValue();
                    }
                    if (examPageUserAnswer.getCoverageRate() != null) {
                        coverageRate += examPageUserAnswer.getCoverageRate().doubleValue();
                    }
                }
                int size = examPageUserQuestionList.size();
                examPageUserParam.setDa(size!=0?da / size:0);
                examPageUserParam.setBiao(size!=0?biao / size:0);
                examPageUserParam.setCuo(size!=0?cuo / size:0);
                examPageUserParam.setWu(size!=0?wu / size:0);
                examPageUserParam.setShu(size!=0?shu / size:0);
                examPageUserParam.setZong(size!=0?zong / size:0);
                if (size!=0){
                    examPageUserParam.setAccuracyRate(new BigDecimal(accuracyRate/size).setScale(2, RoundingMode.HALF_UP));
                    examPageUserParam.setCoverageRate(new BigDecimal(coverageRate/size).setScale(2, RoundingMode.HALF_UP));
                }else {
                    examPageUserParam.setAccuracyRate(new BigDecimal(0));
                    examPageUserParam.setCoverageRate(new BigDecimal(0));
                }

            }
            return BaseResponse.ok(examPageUserVoList);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    @RequestMapping("/classSummary")
    public BaseResponse<?> classSummary(@RequestBody SummaryParam  summaryParam) {
        List<User> userList = null;
        if (summaryParam.getClassId() == null){
            return BaseResponse.fail("参数错误");
        }else {
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getClassId,summaryParam.getClassId());
            userList = userService.list(wrapper);
        }
        if (userList.isEmpty()){
            return BaseResponse.fail("该班级下没有学生");
        }
        List<Integer> userIds = userList.stream().map(User::getId).collect(Collectors.toList());
        if (summaryParam.getType() == 0){
            List<InstantFeedbacks> instantFeedbacksList = instantFeedbacksService.list(new LambdaQueryWrapper<InstantFeedbacks>().eq(InstantFeedbacks::getShape, 500).in(InstantFeedbacks::getUserId,userIds));
            instantFeedbacksList.forEach(instantFeedbacks -> {
                instantFeedbacks.setRealName(userService.getById(instantFeedbacks.getUserId()).getRealName());
            });
            return BaseResponse.ok(instantFeedbacksList);
        }
        else if (summaryParam.getType() == 1){
            List<ExamPageUserVo> examPageUserList = examPageUserService.getExamPageUserName(summaryParam.getClassId());
            List<Integer> examIds = examPageUserList.stream().map(ExamPageUserVo::getExamId).distinct().collect(Collectors.toList());
            ClassSummaryParam classSummaryParam = new ClassSummaryParam();
            for (Integer examId : examIds){
                List<ExamPageUserVo> examPageUserList1 = examPageUserList.stream().filter(examPageUser -> examPageUser.getExamId().equals(examId)).collect(Collectors.toList());
                BigDecimal maxScore = examPageUserList1.stream()
                        .map(ExamPageUserVo::getScore)
                        .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal minScore = examPageUserList1.stream().map(ExamPageUserVo::getScore).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal avgScore = new BigDecimal(0);
                for (ExamPageUserVo examPageUserVo : examPageUserList1) {
                    avgScore = avgScore.add(examPageUserVo.getScore());
                }
                avgScore = NumberUtil.div(avgScore,examPageUserList1.size());
                classSummaryParam.setAvgScore(avgScore+"");
                classSummaryParam.setMaxScore(maxScore+"");
                classSummaryParam.setMinScore(minScore+"");
                classSummaryParam.setExamPageUserVoList(examPageUserList1);
            }
            return BaseResponse.ok(classSummaryParam);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    @RequestMapping("/getClassScoreByQuestionType")
    @PrimaryDataSource
    public BaseResponse<?> getClassScoreByQuestionType(SummaryParam summaryParam) {
        List<ClassSummaryParam> classSummaryParamList = new ArrayList<>();
        List<ClassSummaryParam> classSummaryExamOperateParamList = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<>();
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        int roleId = loginUserAll.getUser().getRoleId();
        Integer classId = 0;
        Integer examId = 0;
        if(summaryParam.getType()==0){
            if (summaryParam.getClassId()!=null){
                classId = summaryParam.getClassId();
            }else{
                if (roleId == 1){
                    classId  = classInfoService.list().get(0).getId();
                }else if (roleId == 2){
                    classId = classInfoService.list().stream().filter(classInfo -> classInfo.getId().equals(loginUserAll.getUser().getClassId())).findFirst().get().getId();
                }else {
                    return BaseResponse.fail("无权限显示");
                }
            }
            List<Integer> userIds = null;
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            List<Integer> roleIds = new ArrayList<Integer>();
            roleIds.add(3);
            roleIds.add(21);
            wrapper.in(User::getRoleId, roleIds);
            wrapper.in(User::getClassId,classId);
            List<User> userList = userService.list(wrapper);
            if (userList==null || userList.size()==0){
                return BaseResponse.fail("该班级没有学生");
            }
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
            List<InstantFeedbacks> instantFeedbacksList =
                    instantFeedbacksService.lambdaQuery().in(InstantFeedbacks::getUserId,userIds)
                            .eq(InstantFeedbacks::getIsDel,0)
                            .eq(InstantFeedbacks::getFinishedState,2)
                            .eq(InstantFeedbacks::getShape,500)
                            .list();
            if(instantFeedbacksList==null){
                return BaseResponse.fail("无操作题数据");
            }else if (instantFeedbacksList.size()==0){
                return BaseResponse.fail("无操作题数据");
            }else {
                ClassSummaryParam classSummaryParam = new ClassSummaryParam();
                classSummaryParam.setExamId(null);
                questionTypeService.list().forEach(questionType -> {
                    List<InstantFeedbacks> instantFeedbacks_type =
                            instantFeedbacksList.stream().filter(instantFeedbacks -> instantFeedbacks.getType().equals(questionType.getId())).collect(Collectors.toList());
                    Integer maxScore = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getUserScore).max().orElse(0);
                    Integer minScore = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getUserScore).min().orElse(0);
                    Integer avgScore = 0;
                    if (instantFeedbacks_type.isEmpty()||instantFeedbacks_type.size()==0){
                    }else{
                        avgScore = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getUserScore).sum()/instantFeedbacks_type.size();
                        double sumAccuracy = instantFeedbacks_type.stream()
                                .mapToDouble(feedback -> feedback.getAccuracyRate() != null ? feedback.getAccuracyRate().doubleValue() : 0)
                                .sum();
                        Double avgAccuracy = instantFeedbacks_type.isEmpty() ? 0.0 : sumAccuracy / instantFeedbacks_type.size();
                        double sumCoverage = instantFeedbacks_type.stream()
                                .mapToDouble(feedback -> feedback.getCoverageRate() != null ? feedback.getCoverageRate().doubleValue() : 0)
                                .sum();
                        Double avgCoverage = instantFeedbacks_type.isEmpty() ? 0.0 : sumCoverage / instantFeedbacks_type.size();
                        double avgDuration = instantFeedbacks_type.stream().mapToDouble(InstantFeedbacks::getOperationDuration).sum()/instantFeedbacks_type.size();
                        Integer avgBiao = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getBiao).sum()/instantFeedbacks_type.size();
                        Integer avgCuo = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getCuo).sum()/instantFeedbacks_type.size();
                        Integer avgWu = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getWu).sum()/instantFeedbacks_type.size();
                        Integer avgShu = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getShu).sum()/instantFeedbacks_type.size();
                        Integer avgZong = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getZong).sum()/instantFeedbacks_type.size();
                        Integer avgDa = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getDa).sum()/instantFeedbacks_type.size();
                        classSummaryParam.setAvgBiao(avgBiao);
                        classSummaryParam.setAvgCuo(avgCuo);
                        classSummaryParam.setAvgWu(avgWu);
                        classSummaryParam.setAvgShu(avgShu);
                        classSummaryParam.setAvgZong(avgZong);
                        classSummaryParam.setAvgDa(avgDa);
                        classSummaryParam.setType(questionType.getId());
                        classSummaryParam.setAvgAccuracy(avgAccuracy);
                        classSummaryParam.setAvgCoverage(avgCoverage);
                        classSummaryParam.setAvgDuration(avgDuration);
                        classSummaryParam.setMaxScore(maxScore+"");
                        classSummaryParam.setMinScore(minScore+"");
                        classSummaryParam.setAvgScore(avgScore+"");
                        classSummaryParam.setInstantFeedbacksVoList(instantFeedbacks_type);
                        classSummaryParam.setExamPageUserVoList(null);
                        classSummaryParamList.add(classSummaryParam);
                    }
                });
            }
            return BaseResponse.ok(classSummaryParamList);
        }else if(summaryParam.getType()==1){
            if (summaryParam.getClassId()!=null){
                classId = summaryParam.getClassId();
            }else{
                if (roleId == 1){
                    classId  = classInfoService.list().get(0).getId();
                }else if (roleId == 2){
                    classId = classInfoService.list().stream().filter(classInfo -> classInfo.getId().equals(loginUserAll.getUser().getClassId())).findFirst().get().getId();
                }else {
                    return BaseResponse.fail("无权限显示");
                }
            }
            List<Integer> userIds = null;
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            List<Integer> roleIds = new ArrayList<Integer>();
            roleIds.add(3);
            roleIds.add(21);
            wrapper.in(User::getRoleId,roleIds);
            wrapper.in(User::getClassId,classId);
            List<User> userList = userService.list(wrapper);
            if (userList==null || userList.size()==0){
                return BaseResponse.fail("该班级没有学生");
            }
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
            if (summaryParam.getExamId()!=null){
                examId = summaryParam.getExamId();
            }else{
                examId = examPageUserService.list(new LambdaQueryWrapper<ExamPageUser>().eq(ExamPageUser::getClassId,classId)).get(0).getExamId();
            }
            List<ExamPageUserAnswer> examPageUserAnswerList = examPageUserAnswerService.list(
                    new LambdaQueryWrapper<ExamPageUserAnswer>()
                            .eq(ExamPageUserAnswer::getExamId,examId)
                            .in(ExamPageUserAnswer::getUserId,userIds)
            );
            //查询所有的shape
            List<Shape> shapeList = shapeService.list();
            for (Shape shape : shapeList){
                ClassSummaryParam classSummaryParam = new ClassSummaryParam();
                List<ExamPageUserAnswer> examPageUserAnswer_shape = examPageUserAnswerList.stream().filter(examPageUserAnswer -> examPageUserAnswer.getShape()==Integer.parseInt(shape.getShapeCode())).collect(Collectors.toList());
                BigDecimal maxScore = examPageUserAnswer_shape.stream().map(ExamPageUserAnswer::getUserScore).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal minScore = examPageUserAnswer_shape.stream().map(ExamPageUserAnswer::getUserScore).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal avgScore = new BigDecimal(0);
                if (examPageUserAnswer_shape.isEmpty()||examPageUserAnswer_shape.size()==0){
                }else {
                    for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswer_shape) {
                        avgScore = avgScore.add(examPageUserAnswer.getUserScore());
                    }
                    avgScore = NumberUtil.div(avgScore,examPageUserAnswer_shape.size());
                    classSummaryParam.setExamId(examPageUserAnswer_shape.get(0).getExamId());
                    classSummaryParam.setShape(shape.getShapeCode());
                    classSummaryParam.setMaxScore(maxScore+"");
                    classSummaryParam.setMinScore(minScore+"");
                    classSummaryParam.setAvgScore(avgScore+"");
                    classSummaryParam.setExamPageUserAnswerList(examPageUserAnswer_shape);
                    classSummaryParam.setExamPageUserVoList(null);
                    classSummaryParamList.add(classSummaryParam);
                }
            }
            questionTypeService.list().forEach(questionType -> {
                ClassSummaryParam classSummaryParam = new ClassSummaryParam();
                //筛选出来操作题
                List<ExamPageUserAnswer> examPageUserAnswer_type =
                        examPageUserAnswerList.stream().filter(examPageUserAnswer -> examPageUserAnswer.getType().equals(questionType.getId())&&examPageUserAnswer.getShape()==500).collect(Collectors.toList());
                BigDecimal maxScore = examPageUserAnswer_type.stream().map(ExamPageUserAnswer::getUserScore).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal minScore = examPageUserAnswer_type.stream().map(ExamPageUserAnswer::getUserScore).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal avgScore = new BigDecimal(0);
                if (examPageUserAnswer_type.isEmpty()||examPageUserAnswer_type.size()==0){
                }else {
                    for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswer_type) {
                        avgScore = avgScore.add(examPageUserAnswer.getUserScore());
                    }
                    avgScore = NumberUtil.div(avgScore,examPageUserAnswer_type.size());
                    double sumAccuracy = examPageUserAnswer_type.stream()
                            .mapToDouble(feedback -> feedback.getAccuracyRate() != null ? feedback.getAccuracyRate().doubleValue() : 0)
                            .sum();
                    Double avgAccuracy = examPageUserAnswer_type.isEmpty() ? 0.0 : sumAccuracy / examPageUserAnswer_type.size();
                    double sumCoverage = examPageUserAnswer_type.stream()
                            .mapToDouble(feedback -> feedback.getCoverageRate() != null ? feedback.getCoverageRate().doubleValue() : 0)
                            .sum();
                    Double avgCoverage = examPageUserAnswer_type.isEmpty() ? 0.0 : sumCoverage / examPageUserAnswer_type.size();
                    Integer avgBiao = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getBiao).sum()/examPageUserAnswer_type.size();
                    Integer avgCuo = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getCuo).sum()/examPageUserAnswer_type.size();
                    Integer avgWu = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getWu).sum()/examPageUserAnswer_type.size();
                    Integer avgShu = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getShu).sum()/examPageUserAnswer_type.size();
                    Integer avgZong = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getZong).sum()/examPageUserAnswer_type.size();
                    Integer avgDa = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getDa).sum()/examPageUserAnswer_type.size();
                    classSummaryParam.setExamId(examPageUserAnswer_type.get(0).getExamId());
                    classSummaryParam.setAvgAccuracy(avgAccuracy);
                    classSummaryParam.setAvgCoverage(avgCoverage);
                    classSummaryParam.setAvgBiao(avgBiao);
                    classSummaryParam.setAvgCuo(avgCuo);
                    classSummaryParam.setAvgWu(avgWu);
                    classSummaryParam.setAvgShu(avgShu);
                    classSummaryParam.setAvgZong(avgZong);
                    classSummaryParam.setAvgDa(avgDa);
                    classSummaryParam.setType(questionType.getId());
                    classSummaryParam.setMaxScore(maxScore+"");
                    classSummaryParam.setMinScore(minScore+"");
                    classSummaryParam.setAvgScore(avgScore+"");
                    classSummaryParam.setExamPageUserAnswerList(examPageUserAnswer_type);
                    classSummaryParam.setExamPageUserVoList(null);
                    classSummaryExamOperateParamList.add(classSummaryParam);
                }
            });
            map.put("classSummaryParamList",classSummaryParamList);
            map.put("classSummaryExamOperateParamList",classSummaryExamOperateParamList);
            return BaseResponse.ok(map);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    @RequestMapping("/getStuScoreByQuestionType")
    @PrimaryDataSource
    public BaseResponse<?> getStuScoreByQuestionType(SummaryStuIDParam summaryStuIDParam) {
        List<ClassSummaryParam> classSummaryParamList = new ArrayList<>();
        List<ClassSummaryParam> classSummaryExamOperateParamList = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<>();
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        int roleId = loginUserAll.getUser().getRoleId();
        Integer classId = 0;
        Integer examId = 0;
        if(summaryStuIDParam.getType()==0){
            if (summaryStuIDParam.getClassId()!=null){
                classId = summaryStuIDParam.getClassId();
            }else{
                if (roleId == 1){
                    classId  = classInfoService.list().get(0).getId();
                }else if (roleId == 2){
                    classId = classInfoService.list().stream().filter(classInfo -> classInfo.getId().equals(loginUserAll.getUser().getClassId())).findFirst().get().getId();
                }else {
                    return BaseResponse.fail("无权限显示");
                }
            }
            List<Integer> userIds = null;
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getRoleId,3);
            wrapper.in(User::getClassId,classId);
            List<User> userList = userService.list(wrapper);
            if (userList==null || userList.size()==0){
                return BaseResponse.fail("该班级没有学生");
            }
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
            List<InstantFeedbacks> instantFeedbacksList =
                    instantFeedbacksService.lambdaQuery().in(InstantFeedbacks::getUserId,userIds)
                            .eq(InstantFeedbacks::getIsDel,0)
                            .eq(InstantFeedbacks::getFinishedState,2)
                            .eq(InstantFeedbacks::getShape,500)
                            .list();
            if(instantFeedbacksList==null){
                return BaseResponse.fail("无操作题数据");
            }else if (instantFeedbacksList.size()==0){
                return BaseResponse.fail("无操作题数据");
            }else {
                if (summaryStuIDParam.getStuId()==null||summaryStuIDParam.getStuId()==0){
                    InstantFeedbacks instantFeedbacks_first = instantFeedbacksList.get(0);
                    summaryStuIDParam.setStuId(instantFeedbacks_first.getUserId());
                }
                List<InstantFeedbacks>  instantFeedbacksList_student = instantFeedbacksList.stream().filter(instantFeedbacks -> instantFeedbacks.getUserId().equals(summaryStuIDParam.getStuId())).collect(Collectors.toList());
                ClassSummaryParam classSummaryParam = new ClassSummaryParam();
                classSummaryParam.setExamId(null);
                questionTypeService.list().forEach(questionType -> {
                    List<InstantFeedbacks> instantFeedbacks_type =
                            instantFeedbacksList_student.stream().filter(instantFeedbacks -> instantFeedbacks.getType().equals(questionType.getId())).collect(Collectors.toList());
                    Integer maxScore = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getUserScore).max().orElse(0);
                    Integer minScore = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getUserScore).min().orElse(0);
                    Integer avgScore = 0;
                    if (instantFeedbacks_type.isEmpty()||instantFeedbacks_type.size()==0){
                    }else{
                        avgScore = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getUserScore).sum()/instantFeedbacks_type.size();
                        double sumAccuracy = instantFeedbacks_type.stream()
                                .mapToDouble(feedback -> feedback.getAccuracyRate() != null ? feedback.getAccuracyRate().doubleValue() : 0)
                                .sum();
                        Double avgAccuracy = instantFeedbacks_type.isEmpty() ? 0.0 : sumAccuracy / instantFeedbacks_type.size();
                        double sumCoverage = instantFeedbacks_type.stream()
                                .mapToDouble(feedback -> feedback.getCoverageRate() != null ? feedback.getCoverageRate().doubleValue() : 0)
                                .sum();
                        Double avgCoverage = instantFeedbacks_type.isEmpty() ? 0.0 : sumCoverage / instantFeedbacks_type.size();
                        double avgDuration = instantFeedbacks_type.stream().mapToDouble(InstantFeedbacks::getOperationDuration).sum()/instantFeedbacks_type.size();
                        Integer avgBiao = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getBiao).sum()/instantFeedbacks_type.size();
                        Integer avgCuo = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getCuo).sum()/instantFeedbacks_type.size();
                        Integer avgWu = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getWu).sum()/instantFeedbacks_type.size();
                        Integer avgShu = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getShu).sum()/instantFeedbacks_type.size();
                        Integer avgZong = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getZong).sum()/instantFeedbacks_type.size();
                        Integer avgDa = instantFeedbacks_type.stream().mapToInt(InstantFeedbacks::getDa).sum()/instantFeedbacks_type.size();
                        classSummaryParam.setAvgBiao(avgBiao);
                        classSummaryParam.setAvgCuo(avgCuo);
                        classSummaryParam.setAvgWu(avgWu);
                        classSummaryParam.setAvgShu(avgShu);
                        classSummaryParam.setAvgZong(avgZong);
                        classSummaryParam.setAvgDa(avgDa);
                        classSummaryParam.setType(questionType.getId());
                        classSummaryParam.setAvgAccuracy(avgAccuracy);
                        classSummaryParam.setAvgCoverage(avgCoverage);
                        classSummaryParam.setAvgDuration(avgDuration);
                        classSummaryParam.setMaxScore(maxScore+"");
                        classSummaryParam.setMinScore(minScore+"");
                        classSummaryParam.setAvgScore(avgScore+"");
                        classSummaryParam.setInstantFeedbacksVoList(instantFeedbacks_type);
                        classSummaryParam.setExamPageUserVoList(null);
                        classSummaryParamList.add(classSummaryParam);
                    }
                });
            }
            return BaseResponse.ok(classSummaryParamList);
        }else if(summaryStuIDParam.getType()==1){
            if (summaryStuIDParam.getClassId()!=null){
                classId = summaryStuIDParam.getClassId();
            }else{
                if (roleId == 1){
                    classId  = classInfoService.list().get(0).getId();
                }else if (roleId == 2){
                    classId = classInfoService.list().stream().filter(classInfo -> classInfo.getId().equals(loginUserAll.getUser().getClassId())).findFirst().get().getId();
                    //classId = 3;
                }else {
                    return BaseResponse.fail("无权限显示");
                }
            }
            List<Integer> userIds = null;
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getRoleId,3);
            wrapper.in(User::getClassId,classId);
            List<User> userList = userService.list(wrapper);
            if (userList==null || userList.size()==0){
                return BaseResponse.fail("该班级没有学生");
            }
            userIds = userList.stream().map(User::getId).collect(Collectors.toList());
            if (summaryStuIDParam.getExamId()!=null){
                examId = summaryStuIDParam.getExamId();
            }else{
                examId = examPageUserService.list(new LambdaQueryWrapper<ExamPageUser>().eq(ExamPageUser::getClassId,classId)).get(0).getExamId();
            }
            List<ExamPageUserAnswer> examPageUserAnswerList = examPageUserAnswerService.list(
                    new LambdaQueryWrapper<ExamPageUserAnswer>()
                            .eq(ExamPageUserAnswer::getExamId,examId)
                            .in(ExamPageUserAnswer::getUserId,userIds)
            );
            if (summaryStuIDParam.getStuId()==null||summaryStuIDParam.getStuId()==0){
                ExamPageUserAnswer examPageUserAnswer_first = examPageUserAnswerList.get(0);
                summaryStuIDParam.setStuId(examPageUserAnswer_first.getUserId());
            }
            List<ExamPageUserAnswer> examPageUserAnswerList_stu = examPageUserAnswerList.stream().filter(examPageUserAnswer -> examPageUserAnswer.getUserId().equals(summaryStuIDParam.getStuId())).collect(Collectors.toList());
            List<Shape> shapeList = shapeService.list();
            for (Shape shape : shapeList){
                ClassSummaryParam classSummaryParam = new ClassSummaryParam();
                List<ExamPageUserAnswer> examPageUserAnswer_shape = examPageUserAnswerList_stu.stream().filter(examPageUserAnswer -> examPageUserAnswer.getShape()==Integer.parseInt(shape.getShapeCode())).collect(Collectors.toList());
                BigDecimal maxScore = examPageUserAnswer_shape.stream().map(ExamPageUserAnswer::getUserScore).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal minScore = examPageUserAnswer_shape.stream().map(ExamPageUserAnswer::getUserScore).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal avgScore = new BigDecimal(0);

                if (examPageUserAnswer_shape.isEmpty()||examPageUserAnswer_shape.size()==0){
                }else {
                    for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswer_shape) {
                        avgScore = avgScore.add(examPageUserAnswer.getUserScore());
                    }
                    avgScore = NumberUtil.div(avgScore,examPageUserAnswer_shape.size());
                    classSummaryParam.setExamId(examPageUserAnswer_shape.get(0).getExamId());
                    classSummaryParam.setShape(shape.getShapeCode());
                    classSummaryParam.setMaxScore(maxScore+"");
                    classSummaryParam.setMinScore(minScore+"");
                    classSummaryParam.setAvgScore(avgScore+"");
                    classSummaryParam.setExamPageUserAnswerList(examPageUserAnswer_shape);
                    classSummaryParam.setExamPageUserVoList(null);
                    classSummaryParamList.add(classSummaryParam);
                }
            }
            questionTypeService.list().forEach(questionType -> {
                ClassSummaryParam classSummaryParam = new ClassSummaryParam();
                List<ExamPageUserAnswer> examPageUserAnswer_type =
                        examPageUserAnswerList_stu.stream().filter(examPageUserAnswer -> examPageUserAnswer.getType().equals(questionType.getId())&&examPageUserAnswer.getShape()==500).collect(Collectors.toList());
                BigDecimal maxScore = examPageUserAnswer_type.stream().map(ExamPageUserAnswer::getUserScore).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal minScore = examPageUserAnswer_type.stream().map(ExamPageUserAnswer::getUserScore).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal avgScore = new BigDecimal(0);
                if (examPageUserAnswer_type.isEmpty()||examPageUserAnswer_type.size()==0){
                }else {
                    for (ExamPageUserAnswer examPageUserAnswer : examPageUserAnswer_type) {
                        avgScore = avgScore.add(examPageUserAnswer.getUserScore());
                    }
                    avgScore = NumberUtil.div(avgScore,examPageUserAnswer_type.size());
                    double sumAccuracy = examPageUserAnswer_type.stream()
                            .mapToDouble(feedback -> feedback.getAccuracyRate() != null ? feedback.getAccuracyRate().doubleValue() : 0)
                            .sum();
                    Double avgAccuracy = examPageUserAnswer_type.isEmpty() ? 0.0 : sumAccuracy / examPageUserAnswer_type.size();
                    double sumCoverage = examPageUserAnswer_type.stream()
                            .mapToDouble(feedback -> feedback.getCoverageRate() != null ? feedback.getCoverageRate().doubleValue() : 0)
                            .sum();
                    Double avgCoverage = examPageUserAnswer_type.isEmpty() ? 0.0 : sumCoverage / examPageUserAnswer_type.size();
                    Integer avgBiao = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getBiao).sum()/examPageUserAnswer_type.size();
                    Integer avgCuo = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getCuo).sum()/examPageUserAnswer_type.size();
                    Integer avgWu = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getWu).sum()/examPageUserAnswer_type.size();
                    Integer avgShu = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getShu).sum()/examPageUserAnswer_type.size();
                    Integer avgZong = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getZong).sum()/examPageUserAnswer_type.size();
                    Integer avgDa = examPageUserAnswer_type.stream().mapToInt(ExamPageUserAnswer::getDa).sum()/examPageUserAnswer_type.size();
                    classSummaryParam.setExamId(examPageUserAnswer_type.get(0).getExamId());
                    classSummaryParam.setAvgAccuracy(avgAccuracy);
                    classSummaryParam.setAvgCoverage(avgCoverage);
                    classSummaryParam.setAvgBiao(avgBiao);
                    classSummaryParam.setAvgCuo(avgCuo);
                    classSummaryParam.setAvgWu(avgWu);
                    classSummaryParam.setAvgShu(avgShu);
                    classSummaryParam.setAvgZong(avgZong);
                    classSummaryParam.setAvgDa(avgDa);
                    classSummaryParam.setType(questionType.getId());
                    classSummaryParam.setMaxScore(maxScore+"");
                    classSummaryParam.setMinScore(minScore+"");
                    classSummaryParam.setAvgScore(avgScore+"");
                    classSummaryParam.setExamPageUserAnswerList(examPageUserAnswer_type);
                    classSummaryParam.setExamPageUserVoList(null);
                    classSummaryExamOperateParamList.add(classSummaryParam);
                }
            });
            map.put("classSummaryParamList",classSummaryParamList);
            map.put("classSummaryExamOperateParamList",classSummaryExamOperateParamList);
            return BaseResponse.ok(map);
        }else {
            return BaseResponse.fail("参数错误");
        }
    }

    @RequestMapping("/getExamListByClass/{classId}")
    public BaseResponse<?> getExamListByClass(@PathVariable Integer classId) {
        //获取登录用户
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        if (loginUserAll == null || loginUserAll.getUser() == null) {
            return BaseResponse.fail("用户未登录");
        }else if (loginUserAll.getUser().getRoleId() == 19) {
            List<ExamClVo> examClassList = examClassService.listRSGLy(classId) ;
            return BaseResponse.ok(examClassList);
        }else{
            List<ExamClVo> examClassList = examClassService.listByClassId(classId);
            return BaseResponse.ok(examClassList);
        }
    }
}
