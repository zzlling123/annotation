package com.xinkao.erp.exam.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.exam.dto.QuestionTypeListDto;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.vo.ExamPageVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;



@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    Page<ExamPageVo> page(Page page, ExamQuery query,List<Integer> classIds);

    Page<ExamPageVo> page1(Page page,List<Integer> examIds);

    List<QuestionTypeListDto> getExamPageSetByTypeAndShape(Integer difficultyLevel, List<String> symbol);
}
