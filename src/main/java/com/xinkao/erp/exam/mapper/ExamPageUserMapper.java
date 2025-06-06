package com.xinkao.erp.exam.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exam.model.param.ExamUserQuery;
import com.xinkao.erp.exam.model.vo.ExamPageUserVo;
import com.xinkao.erp.exam.model.vo.ExamUserVo;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.query.ExamTeacherQuery;
import com.xinkao.erp.exam.vo.ExamPageTeacherVo;
import com.xinkao.erp.exam.vo.ExamPageUserListVo;
import com.xinkao.erp.exam.vo.ExamPageVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 考生表 Mapper 接口
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Mapper
public interface ExamPageUserMapper extends BaseMapper<ExamPageUser> {

    Page<ExamUserVo> page(Page page, ExamQuery query, Integer userId);

    Page<ExamPageTeacherVo> pageTeacher(Page page, ExamTeacherQuery query);

    Page<ExamPageUserListVo> getExamUserListForExamId(Page page, ExamUserQuery query);

    List<ExamPageUserVo> getExamPageUserName(Integer classId);
}
