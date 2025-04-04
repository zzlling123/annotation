package com.xinkao.erp.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.excel.ExamPageSetImportModel;
import com.xinkao.erp.exam.mapper.ExamMapper;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.service.ExamClassService;
import com.xinkao.erp.exam.service.ExamPageSetTypeService;
import com.xinkao.erp.exam.service.ExamService;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private ExamPageSetTypeService examPageSetTypeService;

    @Override
    public Page<ExamPageVo> page(ExamQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return examMapper.page(page, query);
    }

    @Override
    public ExamDetailVo detail(Integer id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试信息不存在");
        }
        ExamDetailVo detailVo = new ExamDetailVo();
        BeanUtil.copyProperties(exam, detailVo);
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
        return BaseResponse.ok("新增成功");
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


    /**
     * 根据题目类型和试卷形状获取试卷填充题库数量
     *
     */
    @Override
    public List<ExamPageSetImportModel> getExamPageSetByTypeAndShape(String examId) {
        List<ExamPageSetImportModel> examPageSetImportModels = new ArrayList<>();
        return examPageSetImportModels;
    }
}