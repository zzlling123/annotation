package com.xinkao.erp.exam.service.impl;

import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.exam.mapper.ExamClassMapper;
import com.xinkao.erp.exam.service.ExamClassService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.summary.vo.ExamClVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 考试-班级关联表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:15:00
 */
@Service
public class ExamClassServiceImpl extends BaseServiceImpl<ExamClassMapper, ExamClass> implements ExamClassService {

    @Autowired
    private ExamClassMapper examClassMapper;

    @Override
    public List<ExamClVo> listByClassId(Integer classId) {
        return examClassMapper.listByClassId(classId);
    }
}
