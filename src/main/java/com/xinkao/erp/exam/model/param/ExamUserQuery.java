package com.xinkao.erp.exam.model.param;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
public class ExamUserQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("考试id")
    @NotBlank(message = "考试id不能为空")
    private String examId;

    @ApiModelProperty("班级id")
    @NotBlank(message = "班级id不能为空")
    private String classId;
}