package com.xinkao.erp.question.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class QuestionTypeParam implements InputConverter<Question> {

    @ApiModelProperty("分类ID")
    @NotBlank(message = "分类ID不能为空")
    private String id;

    /**
     * 题目分类名称
     */
    @ApiModelProperty("分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String typeName;

    /**
     * 题目分类文档地址
     */
    @ApiModelProperty("分类文档地址")
    private String fileUrl;
}