package com.xinkao.erp.exam.service;

import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.summary.vo.ExamClVo;

import java.util.List;

/**
 * <p>
 * 考试-班级关联表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:15:00
 */
public interface ExamClassService extends BaseService<ExamClass> {

    List<ExamClVo> listByClassId(Integer classId);
    List<ExamClVo> listRSGLy(Integer classId);

}
