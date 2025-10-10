package com.xinkao.erp.exam.service.impl;

import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.exam.mapper.ExamClassMapper;
import com.xinkao.erp.exam.service.ExamClassService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.summary.vo.ExamClVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamClassServiceImpl extends BaseServiceImpl<ExamClassMapper, ExamClass> implements ExamClassService {

    @Autowired
    private ExamClassMapper examClassMapper;

    @Override
    public List<ExamClVo> listByClassId(Integer classId) {
        return examClassMapper.listByClassId(classId);
    }

    @Override
    public List<ExamClVo> listRSGLy(Integer classId) {
        return examClassMapper.listRSGLy(classId);
    }
}
