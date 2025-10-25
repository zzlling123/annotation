package com.xinkao.erp.user.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class ExamAndPracticePieVo extends BaseEntity {

    @ApiModelProperty("分类ID")
    private String type;

    @ApiModelProperty("分类名")
    private String typeName;

    @ApiModelProperty("分类下的总题目数量")
    private String teaNum;

    @ApiModelProperty("分类下的用户已作答数量")
    private String userTeaNum;
}
