package com.xinkao.erp.question.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.vo.QuestionExercisePageVo;
import com.xinkao.erp.question.vo.QuestionPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 题库表 Mapper 接口
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    Page<QuestionPageVo> page(Page pg , @Param("query") QuestionQuery questionQuery);

    Page<QuestionExercisePageVo> page1(Page pg , @Param("query") QuestionQuery questionQuery);

    List<Question> getRandQuestion(@Param("examPageSetType") ExamPageSetType examPageSetType, @Param("difficultyLevel") Integer difficultyLevel,@Param("symbol") String symbol);
}
