package com.xinkao.erp.manual.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.manual.entity.Manual;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ManualParam implements InputConverter<Manual> {

    @ApiModelProperty("文档ID")
    private Integer id;

    @ApiModelProperty("文件地址")
    @NotBlank(message = "文件地址不能为空")
    private String fileUrl;

    @ApiModelProperty("使用人群类型")
    @NotNull(message = "使用人群类型不能为空")
    private Integer userType;

    @ApiModelProperty("备注")
    private String remark;
} 