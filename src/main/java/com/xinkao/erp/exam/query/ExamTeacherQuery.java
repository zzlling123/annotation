package com.xinkao.erp.exam.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@ApiModel("批改查询")
public class ExamTeacherQuery extends BasePageQuery implements Serializable {

    @ApiModelProperty("班级ID")
    @NotBlank(message = "班级ID不能为空")
    private String classId;
}