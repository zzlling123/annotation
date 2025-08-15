package com.xinkao.erp.exam.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.service.*;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 试卷管理
 *
 * @author Ldy
 * @since 2025-03-29 16:06:02
 */
@RestController
@RequestMapping("/exam-page-user-question")
public class ExamPageUserQuestionController {


    @Autowired
    private ExamPageUserQuestionService examPageStuQuestionService;
    @Autowired
    private ExamPageSetService examPageSetService;
    @Autowired
    private ExamService examService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamClassService examClassService;
    @Resource
    protected RedisUtil redisUtil;

    /**
     * 制卷
     *
     * @return
     */
    @PostMapping("/rollMaking/{examId}")
    @Log(content = "制卷",operationType = OperationType.INSERT)
    public BaseResponse<String> rollMaking(@PathVariable("examId") String examId) {
        if (StrUtil.isBlank(examId)){
            return BaseResponse.fail("编辑失败,试卷分布ID为空！");
        }
        Exam exam = examService.getById(examId);
        ExamPageSet examPageSet = examPageSetService.lambdaQuery().eq(ExamPageSet::getExamId,examId).one();
        if (exam.getState() > 10){
            return BaseResponse.fail("该考试当前状态不允许制卷！");
        }
        if(examPageSet.getQuestionStatus() != 1){
            return BaseResponse.fail("该尚未导入试题分布设置，不可组卷！");
        }
        //获取考试相关班级，然后查询班级下有多少人
        List<Integer> classList = examClassService.lambdaQuery().eq(ExamClass::getExamId, examId).list().stream().map(ExamClass::getClassId).collect(Collectors.toList());
        List<User> userList = userService.lambdaQuery().in(User::getClassId, classList).eq(User::getIsDel, 0).list();
        if (userList.isEmpty()){
            return BaseResponse.fail("该考试下没有考生，请检查关联班级");
        }
        //生成固定token
        String token = XinKaoConstant.ROLL_MAKING+examId;
        //验证该token是否有值，如果有则拦截
        if (redisUtil.get(token) != null){
            //如果value ！= 1则返回
            if (!"1".equals(redisUtil.get(token))){
                return BaseResponse.fail("该考试正在制卷中，请勿重复操作！");
            }
        }
        redisUtil.set(token, "0", 2, TimeUnit.HOURS);
        examPageSetService.updateById(examPageSet);
        //异步线程执行导入
        @Valid ExamPageSet finalExamPageSet = examPageSet;
//        ThreadUtil.execAsync(() -> {
            examPageStuQuestionService.rollMaking(examPageSet,userList,token);
//        });
        return BaseResponse.ok("该考试下考生共"+userList.size()+"人,开始制卷......",token);
    }

    /**
     *
     * 获取制卷进度
     */
    @PostMapping("/getProgress/{examId}/{token}")
    public BaseResponse<Map<String,Integer>> getProgress(@PathVariable("examId") String examId, @PathVariable("token") String token) {
        if (StrUtil.isBlank(examId)){
            return BaseResponse.fail("获取失败,考试ID为空！");
        }
        return examPageStuQuestionService.getProgress(examId,token);
    }
}
