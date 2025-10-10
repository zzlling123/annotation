package com.xinkao.erp.exam.dto;

import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.question.entity.QuestionType;
import lombok.Data;


@Data
public class QuestionTypeListDto implements OutputConverter<QuestionTypeListDto, QuestionType> {
	
	private Integer id;
    
    private String typeName;

    private String shape;

    private String questionOnNum;
}
