package com.xinkao.erp.exam.dto;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.question.entity.QuestionType;
import lombok.Data;

/**
 * 题目分类信息
 * @author Ldy
 */
@Data
public class QuestionTypeListDto implements OutputConverter<QuestionTypeListDto, QuestionType> {
	/**分类主键**/
	private Integer id;
    /**
     * 题目分类名称
     */
    private String typeName;

    //题目类型
    private String shape;

    //题目数量
    private String questionOnNum;
}
