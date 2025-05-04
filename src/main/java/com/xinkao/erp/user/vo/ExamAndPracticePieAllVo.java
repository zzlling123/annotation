package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *     统计饼状图
 * </p>
 *
 * @author Ldy
 * @since 2023-05-09 14:02:05
 */
@Getter
@Setter
public class ExamAndPracticePieAllVo extends BaseEntity {

    @ApiModelProperty("分类名")
    private String typeName;

    @ApiModelProperty("分类下的用户已作答数量")
    private String userTeaNum;
}
