package com.xinkao.erp.manual.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.manual.entity.Manual;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 使用文档VO
 * </p>
 *
 * @author Ldy
 * @since 2025-07-26
 */
@Getter
@Setter
public class ManualVo {

    @ApiModelProperty("文档ID")
    private Integer id;

    /**
     * 文件地址
     */
    @ApiModelProperty("文件地址")
    private String fileUrl;

    /**
     * 使用人群类型
     */
    @ApiModelProperty("使用人群类型")
    private Integer userType;

    /**
     * 使用人群类型名称
     */
    @ApiModelProperty("使用人群类型名称")
    private String userTypeName;

    /**
     * 创建用户
     */
    @ApiModelProperty("创建用户")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改用户
     */
    @ApiModelProperty("修改用户")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 设置用户类型名称
     */
    public void setUserTypeName() {
        this.userTypeName = Manual.UserTypeEnum.getNameByCode(this.userType);
    }
} 