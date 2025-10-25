package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExamAndPracticeBarVo extends BaseEntity {

    @ApiModelProperty("分类ID")
    private String type;

    @ApiModelProperty("分类名")
    private String typeName;

    @ApiModelProperty("分类下的总分")
    private String score;

    @ApiModelProperty("分类下的得分")
    private String userScore;

    @ApiModelProperty("得分率--百分比")
    private String scoreRate;
}
