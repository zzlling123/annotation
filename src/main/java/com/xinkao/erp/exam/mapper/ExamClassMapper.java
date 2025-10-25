package com.xinkao.erp.exam.mapper;

import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.summary.vo.ExamClVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ExamClassMapper extends BaseMapper<ExamClass> {
    List<ExamClVo> listByClassId(@Param("classId") Integer classId);

    List<ExamClVo> listRSGLy(@Param("classId") Integer classId);
}
