package com.xinkao.erp.manual.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.manual.entity.Manual;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 使用文档参数类
 * </p>
 *
 * @author Ldy
 * @since 2025-07-26
 */
@Getter
@Setter
public class ManualParam implements InputConverter<Manual> {

    @ApiModelProperty("文档ID")
    private Integer id;

    /**
     * 文件地址
     */
    @ApiModelProperty("文件地址")
    @NotBlank(message = "文件地址不能为空")
    private String fileUrl;

    /**
     * 使用人群类型:1-管理员 2-学校管理员 3-社保局管理员 4-学生用户 5-老师用户 6-评审专家 7-社会考生
     */
    @ApiModelProperty("使用人群类型")
    @NotNull(message = "使用人群类型不能为空")
    private Integer userType;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
} 