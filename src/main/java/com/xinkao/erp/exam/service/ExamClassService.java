package com.xinkao.erp.exam.service;

import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.summary.vo.ExamClVo;

import java.util.List;

public interface ExamClassService extends BaseService<ExamClass> {

    List<ExamClVo> listByClassId(Integer classId);
    List<ExamClVo> listRSGLy(Integer classId);

}
