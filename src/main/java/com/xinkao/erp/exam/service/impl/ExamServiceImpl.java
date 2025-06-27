package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.exam.dto.QuestionTypeListDto;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.excel.ExamPageSetImportModel;
import com.xinkao.erp.exam.mapper.ExamMapper;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.ExamClassService;
import com.xinkao.erp.exam.service.ExamPageSetService;
import com.xinkao.erp.exam.service.ExamPageSetTypeService;
import com.xinkao.erp.exam.service.ExamService;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.service.ClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private ExamClassService examClassService;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private ExamPageSetTypeService examPageSetTypeService;
    @Autowired
    private ExamPageSetService examPageSetService;

    @Override
    public Page<ExamPageVo> page(ExamQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        LoginUser loginUser = redisUtil.getInfoByToken();
        //如果是教师角色，则只显示分配给自己名下班级的考试
        List<Integer> classIds = new ArrayList<>();
        if (loginUser.getUser().getRoleId() == 2) {
            classIds = classInfoService.lambdaQuery().eq(ClassInfo::getDirectorId, loginUser.getUser().getId()).eq(ClassInfo::getIsDel,0).list().stream().map(ClassInfo::getId).collect(Collectors.toList());
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
        //补充班级管理
        List<Integer> classList = examClassService.lambdaQuery().eq(ExamClass::getExamId, id).list().stream().map(ExamClass::getClassId).collect(Collectors.toList());
        detailVo.setClassList(classList);
        //补充试卷设置
        detailVo.setExamPageSetTypeVoList(examPageSetTypeService.lambdaQuery().eq(ExamPageSetType::getExamId, id).list());
        return detailVo;
    }

    @Override
    @Transactional
    public BaseResponse save(ExamParam examParam) {
        Exam exam = new Exam();
        BeanUtil.copyProperties(examParam, exam);
        // 校验考试名称是否重复
        if (lambdaQuery().eq(Exam::getExamName, examParam.getExamName()).count() > 0) {
            return BaseResponse.fail("考试名称已存在");
        }
        save(exam);
        //保存试卷设置
        ExamPageSet examPageSet = new ExamPageSet();
        examPageSet.setExamId(exam.getId());
        examPageSet.setScore(Integer.valueOf(examParam.getScore()));
        examPageSet.setScorePass(Integer.valueOf(examParam.getScorePass()));
        examPageSet.setPageMode(Integer.valueOf(examParam.getPageMode()));
        examPageSetService.save(examPageSet);
        //保存班级管理
        String[] classIds = examParam.getClassIds().split(",");
        List<ExamClass> examClassList = new ArrayList<>();
        for (String classId : classIds) {
            ExamClass examClass = new ExamClass();
            examClass.setExamId(exam.getId());
            examClass.setClassId(Integer.parseInt(classId));
            examClassList.add(examClass);
        }
        examClassService.saveBatch(examClassList);
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
        // 校验考试名称是否重复
        if (lambdaQuery().eq(Exam::getExamName, examParam.getExamName())
                .ne(Exam::getId, examParam.getId()).count() > 0) {
            return BaseResponse.fail("考试名称已存在");
        }
        //考试状态已开启时不可编辑
        if (exam.getState() > 10) {
            return BaseResponse.fail("考试已开启，不可编辑");
        }
        //保存试卷设置
        ExamPageSet examPageSet = examPageSetService.lambdaQuery().eq(ExamPageSet::getExamId, examParam.getId()).one();
        if (examPageSet == null) {
            return BaseResponse.fail("该考试设置信息不存在，请联系管理员");
        }else{
            //考试如果已经制卷，则不可修改
            if (examPageSet.getQuestionStatus() == 1){
                return BaseResponse.fail("该考试已制卷，不可修改");
            }
        }
        examPageSet.setExamId(exam.getId());
        examPageSet.setScore(Integer.valueOf(examParam.getScore()));
        examPageSet.setScorePass(Integer.valueOf(examParam.getScorePass()));
        examPageSet.setPageMode(Integer.valueOf(examParam.getPageMode()));
        examPageSetService.saveOrUpdate(examPageSet);
        //清除后，保存班级管理
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
        //验证是否管理员，如果不是的话，只能删除自己创建的考试
        LoginUser loginUser = redisUtil.getInfoByToken();
        Exam exam = examMapper.selectById(id);
        if (loginUser.getUser().getRoleId() != 1) {
            if (!exam.getCreateBy().equals(loginUser.getUser().getId().toString())) {
                return BaseResponse.fail("您只能删除自己创建的考试");
            }
        }
        //验证考试是否已开始，只能删除未开始的考试
        if (exam.getState() > 10) {
            return BaseResponse.fail("考试已开始，不可删除");
        }
        exam.setIsDel(1);
        return examMapper.updateById(exam) > 0 ? BaseResponse.ok("删除成功") : BaseResponse.fail("删除失败");

    }


    /**
     * 根据题目类型和试卷形状获取试卷填充题库数量
     *
     */
    @Override
    public List<ExamPageSetImportModel> getExamPageSetByTypeAndShape(String examId) {
        List<QuestionTypeListDto> questionTypeListDtos = examMapper.getExamPageSetByTypeAndShape();
        Map<Integer,List<QuestionTypeListDto>> map = questionTypeListDtos
                .stream().collect(Collectors.groupingBy(QuestionTypeListDto::getId));
        List<ExamPageSetImportModel> list = new ArrayList<>();
        //按照分类，题型进行循环插入
        for (Integer typeId : map.keySet()) {
            //增加内容
            List<QuestionTypeListDto> voList = map.get(typeId);
            ExamPageSetImportModel examPageSetImportModel = new ExamPageSetImportModel();
            for (QuestionTypeListDto vo : voList) {
                examPageSetImportModel.setType(vo.getTypeName());
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
                }
            }
            list.add(examPageSetImportModel);
        }
        return list;
    }
}