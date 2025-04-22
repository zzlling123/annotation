package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *     统计树状图
 * </p>
 *
 * @author Ldy
 * @since 2023-05-09 14:02:05
 */
@Getter
@Setter
public class ExamAndPracticeBarVo extends BaseEntity {

    @ApiModelProperty("分类ID")
    private String type;

    @ApiModelProperty("用户名")
    private String typeName;

    @ApiModelProperty("得分率--百分比")
    private String scoreRate;
}
