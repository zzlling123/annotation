package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.ResultUtils;
import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.excel.ExamPageSetImportErrorModel;
import com.xinkao.erp.exam.mapper.ExamPageSetMapper;
import com.xinkao.erp.exam.param.ExamPageSetParam;
import com.xinkao.erp.exam.service.ExamClassService;
import com.xinkao.erp.exam.service.ExamPageSetService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.exam.service.ExamPageSetTypeService;
import com.xinkao.erp.exam.service.ExamPageUserQuestionService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 考试设置表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Slf4j
@Service
public class ExamPageSetServiceImpl extends BaseServiceImpl<ExamPageSetMapper, ExamPageSet> implements ExamPageSetService {

    @Autowired
    private ResultUtils resultUtils;
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private ExamPageSetTypeService examPageSetTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamClassService examClassService;
    @Lazy
    @Autowired
    private ExamPageUserQuestionService examPageStuQuestionService;

    /**
     * 导入分类题目分布
     * @param response
     * @param addExamPageSetTypeMap
     * @param handleResult
     * @param examPageSetImportErrorModelList
     * @param token
     */
    //导入试卷分布
    @Transactional
    @Override
    public void importExamPageSetType(HttpServletResponse response, Map<Integer, List<ExamPageSetType>> addExamPageSetTypeMap, HandleResult handleResult, List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList, String token){
        Integer successCount = handleResult.getSuccessCount();
        List<String> errorList = handleResult.getErrorList();
        if(errorList.isEmpty()){
            if (!addExamPageSetTypeMap.isEmpty()) {
                int rowNum = 0;
                //设置总分
                int allScore = 0;
                //判断所有分数加起来是否等于总分，如果不等于则报错
                //计算规则：题数乘以每题分数后的总和
                //计算试卷题目总数
                ExamPageSet examPageSet = getById(addExamPageSetTypeMap.keySet().iterator().next());
                for (Integer examPageSetTypeId : addExamPageSetTypeMap.keySet()) {
                    //新增题目分类数据
                    List<ExamPageSetType> examPageSetTypes = addExamPageSetTypeMap.get(examPageSetTypeId);
                    for (ExamPageSetType examPageSetType : examPageSetTypes) {
                        //增加总分
                        allScore += examPageSetType.getQuestionNum() * examPageSetType.getScore();
                    }
                }
                if (allScore != examPageSet.getScore()){
                    log.error("导入试卷分布试题计算总分与设置总分不相等");
                    errorList.add(resultUtils.getErrMsg(2,"试题计算总分与设置总分不相等"));
                    redisUtils.set(token, JSONObject.toJSONString(BaseResponse.fail("导入试卷分布试题计算总分与设置总分不相等")), 2, TimeUnit.HOURS);
                    return;
                }else{
                    //删除原题目分类数据
                    examPageSetTypeService.lambdaUpdate()
                            .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                            .remove();
                    for (Integer examPageSetTypeId : addExamPageSetTypeMap.keySet()) {
                        int questionCount = 0;
                        try {
                            //新增题目分类数据
                            List<ExamPageSetType> examPageSetTypes = addExamPageSetTypeMap.get(examPageSetTypeId);
                            //计算试卷题目总数
                            for (ExamPageSetType examPageSetType : examPageSetTypes) {
                                questionCount += examPageSetType.getQuestionNum();
                            }
                            examPageSet.setQuestionCount(questionCount);
                            examPageSet.setQuestionStatus(1);
                            //新增题目分类数据
                            examPageSetTypeService.saveBatch(examPageSetTypes);
                            successCount++;
                        } catch (Exception e) {
                            log.error("出现异常: {}", e);
                            errorList.add(resultUtils.getErrMsg(rowNum + 1,
                                    "新增时出现异常：" + e.getMessage()));
                        }
                        rowNum++;
                    }
                    //修改设置
                    updateById(examPageSet);
                    //清除预览题目数据
//                    examPageReviewService.removePreview(examPageSet.getId());
                }
            }
            resultUtils.getResult(handleResult,successCount,errorList);
        }

        if(!errorList.isEmpty()) {
            redisUtils.set(token, JSONObject.toJSONString(BaseResponse.other("导入失败",examPageSetImportErrorModelList)), 2, TimeUnit.HOURS);
        }else{
            redisUtils.set(token, JSONObject.toJSONString(BaseResponse.ok("成功导入")), 2, TimeUnit.HOURS);
        }
    }

    @Transactional
    @Override
    public BaseResponse<?> saveExamPageSetPoint(String examId, List<ExamPageSetParam> list) {
        //生成固定token
        String token = XinKaoConstant.ROLL_MAKING+examId;
        //验证该token是否有值，如果有则拦截
        if (redisUtil.get(token) != null){
            //如果value ！= 1则返回
            if (!"1".equals(redisUtil.get(token))){
                return BaseResponse.fail("该考试正在制卷中，请勿重复操作！");
            }
        }
        //设置总分
        int allScore = 0;
        //判断所有分数加起来是否等于总分，如果不等于则报错
        //计算规则：题数乘以每题分数后的总和
        //计算试卷题目总数
        ExamPageSet examPageSet = lambdaQuery().eq(ExamPageSet::getExamId,examId).one();
        List<ExamPageSetType> examPageSetTypeList = new ArrayList<>();
        int questionCount = 0;
        for (ExamPageSetParam examPageSetParam : list) {
            //增加总分
            allScore += examPageSetParam.getQuestionNum() * examPageSetParam.getScore();
            ExamPageSetType examPageSetType = BeanUtil.copyProperties(examPageSetParam, ExamPageSetType.class);
            examPageSetType.setExamId(examId);
            examPageSetTypeList.add(examPageSetType);
            questionCount += examPageSetType.getQuestionNum();
        }
        if (allScore != examPageSet.getScore()){
            return BaseResponse.fail("试题计算总分与设置总分不相等");
        }
        //删除原题目分类数据
        examPageSetTypeService.lambdaUpdate()
                .eq(ExamPageSetType::getExamId,examPageSet.getExamId())
                .remove();
        examPageSet.setQuestionCount(questionCount);
        examPageSet.setQuestionStatus(1);
        //新增题目分类数据
        examPageSetTypeService.saveBatch(examPageSetTypeList);
        //修改设置
        updateById(examPageSet);
        //开始制卷
        //获取考试相关班级，然后查询班级下有多少人
        List<Integer> classList = examClassService.lambdaQuery().eq(ExamClass::getExamId, examId).list().stream().map(ExamClass::getClassId).collect(Collectors.toList());
        List<User> userList = userService.lambdaQuery().in(User::getClassId, classList).eq(User::getIsDel, 0).list();
        if (userList.isEmpty()){
            return BaseResponse.fail("该考试下没有考生，请检查关联班级");
        }

        redisUtil.set(token, "0", 2, TimeUnit.HOURS);
        updateById(examPageSet);
        //异步线程执行导入
        @Valid ExamPageSet finalExamPageSet = examPageSet;
        ThreadUtil.execAsync(() -> {
            examPageStuQuestionService.rollMaking(finalExamPageSet,userList,token);
        });
        return BaseResponse.ok("该考试下考生共"+userList.size()+"人,开始制卷......",token);
    }
}
