package com.xinkao.erp.question.mapper;

import com.xinkao.erp.question.entity.Label;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.question.vo.LabelVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface LabelMapper extends BaseMapper<Label> {

    @Select("SELECT l.* FROM q_label l LEFT JOIN q_question_label ql ON l.id = ql.lid WHERE ql.qid = #{qid}")
    List<LabelVo> getLabelListByQid(Integer qid);
}
