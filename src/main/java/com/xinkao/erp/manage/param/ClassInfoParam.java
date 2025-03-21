package com.xinkao.erp.manage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ClassInfoParam {

    @ApiModelProperty("班级ID")
    private Integer id;

    @ApiModelProperty("班级名称")
    @NotBlank(message = "班级名称不能为空")
    @Size(min = 1, max = 100, message = "班级名称长度应在1到100个字符之间")
    private String className;

    @ApiModelProperty("班级描述")
    @Size(max = 255, message = "班级描述长度应在255个字符以内")
    private String description;
    
    @ApiModelProperty("负责人")
    private Integer directorId;
}