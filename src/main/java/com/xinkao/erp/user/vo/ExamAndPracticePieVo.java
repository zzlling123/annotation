package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

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
public class ExamAndPracticePieVo extends BaseEntity {

    @ApiModelProperty("做题总数")
    private String allNum;

    @ApiModelProperty("各题型分布数量")
    private Map<String, String> typeNum;
}
