package com.xinkao.erp.manual.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.manual.entity.Manual;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ManualVo {

    @ApiModelProperty("文档ID")
    private Integer id;

    @ApiModelProperty("文件地址")
    private String fileUrl;

    @ApiModelProperty("使用人群类型")
    private Integer userType;

    @ApiModelProperty("使用人群类型名称")
    private String userTypeName;

    @ApiModelProperty("创建用户")
    private String createBy;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("修改用户")
    private String updateBy;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty("备注")
    private String remark;

    public void setUserTypeName() {
        this.userTypeName = Manual.UserTypeEnum.getNameByCode(this.userType);
    }
} 