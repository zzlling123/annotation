package com.xinkao.erp.exam.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 考试表 Mapper 接口
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    Page<ExamPageVo> page(Page page, ExamQuery query);
}
