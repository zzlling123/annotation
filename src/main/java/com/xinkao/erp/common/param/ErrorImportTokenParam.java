package com.xinkao.erp.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel("错误导入文件下载参数")
public class ErrorImportTokenParam {

    @ApiModelProperty("token")
    @NotBlank(message = "token不能为空")
    private String token;
}