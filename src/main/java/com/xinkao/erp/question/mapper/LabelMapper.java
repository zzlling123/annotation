package com.xinkao.erp.question.mapper;

import com.xinkao.erp.question.entity.Label;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.question.vo.LabelVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 题库自定义标签 Mapper 接口
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Mapper
public interface LabelMapper extends BaseMapper<Label> {

    @Select("SELECT l.* FROM q_label l LEFT JOIN q_question_label ql ON l.id = ql.lid WHERE ql.qid = #{qid}")
    List<LabelVo> getLabelListByQid(Integer qid);
}
