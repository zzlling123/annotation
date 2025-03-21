package com.xinkao.erp.manage.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ClassInfoVo {

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("班级描述")
    private String description;
    
    @ApiModelProperty("负责人ID")
    private Integer directorId;
    
    @ApiModelProperty("负责人姓名")
    private String directorName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}