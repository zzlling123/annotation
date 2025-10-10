package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.exam.dto.QuestionTypeListDto;
import com.xinkao.erp.exam.entity.*;
import com.xinkao.erp.exam.excel.ExamPageSetVo;
import com.xinkao.erp.exam.mapper.ExamMapper;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.*;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 考试表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Service
public class ExamServiceImpl extends BaseServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private ExamExpertService examExpertService;
    @Autowired
    private ExamClassService examClassService;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private ExamPageSetTypeService examPageSetTypeService;
    @Autowired
    private ExamPageSetService examPageSetService;
    @Autowired
    private UserService userService;


    @Override
    public Page<ExamPageVo> page(ExamQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        LoginUser loginUser = redisUtil.getInfoByToken();
        List<Integer> classIds = new ArrayList<>();
        if (loginUser.getUser().getRoleId() == 2) {
            classIds = classInfoService.lambdaQuery().eq(ClassInfo::getDirectorId, loginUser.getUser().getId()).eq(ClassInfo::getIsDel,0).list().stream().map(ClassInfo::getId).collect(Collectors.toList());
        }

        if (loginUser.getUser().getRoleId() == 19) {
            List<String> role19UserIds = userService.lambdaQuery()
                    .eq(User::getRoleId, 19)
                    .eq(User::getIsDel, 0)
                    .list()
                    .stream()
                    .map(user -> user.getId().toString())
                    .collect(Collectors.toList());
            query.setCreateByList(role19UserIds);
        }

        if (loginUser.getUser().getRoleId() == 18) {
            List<String> role2And18UserIds = userService.lambdaQuery()
                    .in(User::getRoleId, Arrays.asList(2, 18))
                    .eq(User::getIsDel, 0)
                    .list()
                    .stream()
                    .map(user -> user.getId().toString())
                    .collect(Collectors.toList());
            query.setCreateByList(role2And18UserIds);
        }
        
        return examMapper.page(page, query,classIds);
    }

    @Override
    public ExamDetailVo detail(Integer id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试信息不存在");
        }
        ExamDetailVo detailVo = new ExamDetailVo();
        BeanUtil.copyProperties(exam, detailVo);
        ExamPageSet examPageSet = examPageSetService.lambdaQuery().eq(ExamPageSet::getExamId, id).one();
        detailVo.setScore(examPageSet.getScore());
        detailVo.setScorePass(examPageSet.getScorePass());
        detailVo.setPageMode(examPageSet.getPageMode());
        List<Integer> classList = examClassService.lambdaQuery().eq(ExamClass::getExamId, id).list().stream().map(ExamClass::getClassId).collect(Collectors.toList());
        detailVo.setClassList(classList);
        detailVo.setExamPageSetTypeVoList(examPageSetTypeService.lambdaQuery().eq(ExamPageSetType::getExamId, id).list());
        if (exam.getIsExpert() == 1) {
            List<ExamExpert> examExpertList = examExpertService.lambdaQuery().eq(ExamExpert::getExamId, id).list();
            detailVo.setExpertIds(examExpertList.stream().map(ExamExpert::getExpertId).map(String::valueOf).collect(Collectors.joining(",")));
        }
        return detailVo;
    }

    @Override
    @Transactional
    public BaseResponse save(ExamParam examParam) {
        Exam exam = new Exam();
        BeanUtil.copyProperties(examParam, exam);
        if (lambdaQuery().eq(Exam::getExamName, examParam.getExamName()).count() > 0) {
            return BaseResponse.fail("考试名称已存在");
        }
        save(exam);
        ExamPageSet examPageSet = new ExamPageSet();
        examPageSet.setExamId(exam.getId());
        examPageSet.setScore(new BigDecimal(examParam.getScore()));
        examPageSet.setScorePass(new BigDecimal(examParam.getScorePass()));
        examPageSet.setPageMode(Integer.valueOf(examParam.getPageMode()));
        examPageSetService.save(examPageSet);
        String[] classIds = examParam.getClassIds().split(",");
        List<ExamClass> examClassList = new ArrayList<>();
        for (String classId : classIds) {
            ExamClass examClass = new ExamClass();
            examClass.setExamId(exam.getId());
            examClass.setClassId(Integer.parseInt(classId));
            examClassList.add(examClass);
        }
        examClassService.saveBatch(examClassList);
        if ("1".equals(examParam.getIsExpert())){
            if (StrUtil.isNotBlank(examParam.getExpertIds())){
                List<ExamExpert> examExpertList = new ArrayList<>();
                String[] expertIds = examParam.getExpertIds().split(",");
                for (String expertId : expertIds) {
                    ExamExpert examExpert = new ExamExpert();
                    examExpert.setExamId(exam.getId());
                    examExpert.setExpertId(Integer.parseInt(expertId));
                    examExpertList.add(examExpert);
                }
                examExpertService.saveBatch(examExpertList);
            }
        }
        return BaseResponse.ok("新增成功", exam.getId());
    }

    @Override
    @Transactional
    public BaseResponse update(ExamParam examParam) {
        Exam exam = examMapper.selectById(examParam.getId());
        if (exam == null) {
            return BaseResponse.fail("考试信息不存在");
        }
        BeanUtil.copyProperties(examParam, exam);
        if (lambdaQuery().eq(Exam::getExamName, examParam.getExamName())
                .ne(Exam::getId, examParam.getId()).count() > 0) {
            return BaseResponse.fail("考试名称已存在");
        }
        if (exam.getState() > 10) {
            return BaseResponse.fail("考试已开启，不可编辑");
        }
        ExamPageSet examPageSet = examPageSetService.lambdaQuery().eq(ExamPageSet::getExamId, examParam.getId()).one();
        if (examPageSet == null) {
            return BaseResponse.fail("该考试设置信息不存在，请联系管理员");
        }else{
            if (examPageSet.getQuestionStatus() == 1){
                return BaseResponse.fail("该考试已设置题目分布，不可修改");
            }
        }
        if ("1".equals(examParam.getIsExpert())){
            if (StrUtil.isNotBlank(examParam.getExpertIds())){
                List<ExamExpert> examExpertList = new ArrayList<>();
                String[] expertIds = examParam.getExpertIds().split(",");
                for (String expertId : expertIds) {
                    ExamExpert examExpert = new ExamExpert();
                    examExpert.setExamId(exam.getId());
                    examExpert.setExpertId(Integer.parseInt(expertId));
                    examExpertList.add(examExpert);
                }
                examExpertService.lambdaUpdate()
                        .eq(ExamExpert::getExamId, exam.getId())
                        .remove();
                examExpertService.saveBatch(examExpertList);
            }
        }
        examPageSet.setExamId(exam.getId());
        examPageSet.setScore(new BigDecimal(examParam.getScore()));
        examPageSet.setScorePass(new BigDecimal(examParam.getScorePass()));
        examPageSet.setPageMode(Integer.valueOf(examParam.getPageMode()));
        examPageSetService.saveOrUpdate(examPageSet);
        examClassService.lambdaUpdate()
                .eq(ExamClass::getExamId, examParam.getId())
                .remove();
        String[] classIds = examParam.getClassIds().split(",");
        List<ExamClass> examClassList = new ArrayList<>();
        for (String classId : classIds) {
            ExamClass examClass = new ExamClass();
            examClass.setExamId(exam.getId());
            examClass.setClassId(Integer.parseInt(classId));
            examClassList.add(examClass);
        }
        examClassService.saveBatch(examClassList);
        return updateById(exam) ? BaseResponse.ok("更新成功") : BaseResponse.fail("更新失败");
    }

    @Override
    @Transactional
    public BaseResponse del(Integer id) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Exam exam = examMapper.selectById(id);
        if (loginUser.getUser().getRoleId() != 1) {
            if (!exam.getCreateBy().equals(loginUser.getUser().getId().toString())) {
                return BaseResponse.fail("您只能删除自己创建的考试");
            }
        }
        if (exam.getState() > 10) {
            return BaseResponse.fail("考试已开始，不可删除");
        }
        exam.setIsDel(1);
        return examMapper.updateById(exam) > 0 ? BaseResponse.ok("删除成功") : BaseResponse.fail("删除失败");

    }

    @Override
    public List<ExamPageSetVo> getExamPageSetByTypeAndShape(String examId) {
        Exam exam = getById(examId);
        List<String> symbolList = Arrays.asList(exam.getSymbol().split(","));
        List<QuestionTypeListDto> questionTypeListDtos = examMapper.getExamPageSetByTypeAndShape(exam.getDifficultyLevel(),symbolList);
        Map<Integer,List<QuestionTypeListDto>> map = questionTypeListDtos
                .stream().collect(Collectors.groupingBy(QuestionTypeListDto::getId));
        List<ExamPageSetVo> list = new ArrayList<>();
        for (Integer typeId : map.keySet()) {
            List<QuestionTypeListDto> voList = map.get(typeId);
            ExamPageSetVo examPageSetImportModel = new ExamPageSetVo();
            for (QuestionTypeListDto vo : voList) {
                examPageSetImportModel.setType(vo.getId());
                examPageSetImportModel.setTypeStr(vo.getTypeName());
                if ("100".equals(vo.getShape())){
                    examPageSetImportModel.setChoiceSingleCount(vo.getQuestionOnNum());
                }else if ("200".equals(vo.getShape())){
                    examPageSetImportModel.setChoiceMultiCount(vo.getQuestionOnNum());
                }else if ("300".equals(vo.getShape())){
                    examPageSetImportModel.setChoiceFillCount(vo.getQuestionOnNum());
                }else if ("400".equals(vo.getShape())){
                    examPageSetImportModel.setChoiceAnswerCount(vo.getQuestionOnNum());
                }else if ("500".equals(vo.getShape())){
                    examPageSetImportModel.setChoicePracticeCount(vo.getQuestionOnNum());
                }else if ("600".equals(vo.getShape())){
                    examPageSetImportModel.setChoiceFormCount(vo.getQuestionOnNum());
                }else if ("700".equals(vo.getShape())){
                    examPageSetImportModel.setChoiceJudgeCount(vo.getQuestionOnNum());
                }
            }
            list.add(examPageSetImportModel);
        }
        return list;
    }
}