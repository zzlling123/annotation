package com.xinkao.erp.question.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class QuestionFormTitleParam implements InputConverter<Question> {

    @ApiModelProperty("题目单二级标题ID")
    private Integer id;

    @ApiModelProperty("所属题目单ID")
    private Integer pid;

    @ApiModelProperty("题目")
    @NotBlank(message = "题目不能为空")
    private String question;

    @ApiModelProperty("排序")
    private Integer sort;
}