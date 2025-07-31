package com.xinkao.erp.manual.query;

import com.xinkao.erp.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 使用文档查询参数
 * </p>
 *
 * @author Ldy
 * @since 2025-07-26
 */
@Getter
@Setter
@ApiModel("使用文档查询")
public class ManualQuery extends BasePageQuery implements Serializable {

    /**
     * 使用人群类型
     */
    @ApiModelProperty("使用人群类型")
    private Integer userType;

    /**
     * 创建用户
     */
    @ApiModelProperty("创建用户")
    private String createBy;
} 