package com.xinkao.erp.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.entity.ExamExpert;
import com.xinkao.erp.exam.mapper.ExamExpertMapper;
import com.xinkao.erp.exam.mapper.ExamMapper;
import com.xinkao.erp.exam.service.ExamExpertService;
import com.xinkao.erp.exam.vo.ExamPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamExpertServiceImpl extends ServiceImpl<ExamExpertMapper, ExamExpert> implements ExamExpertService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<ExamExpert> getExpertsByExamId(Integer examId) {
        return this.list(new LambdaQueryWrapper<ExamExpert>()
                .eq(ExamExpert::getExamId, examId));
    }

    @Override
    public List<ExamExpert> getExamsByExpertId(Integer expertId) {
        return this.list(new LambdaQueryWrapper<ExamExpert>()
                .eq(ExamExpert::getExpertId, expertId));
    }


    public Page<ExamPageVo> getExamByExamId(List<Integer> examIds, Pageable pageable) {
        Page page = pageable.toPage();
        return examMapper.page1(page,examIds);
    }
} 