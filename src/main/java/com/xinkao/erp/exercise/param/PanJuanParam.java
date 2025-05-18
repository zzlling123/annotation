package com.xinkao.erp.exercise.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PanJuanParam {

    @ApiModelProperty("用户答案是否正确：0-错误 1-正确 2-部分正确 3-未作答")
    private Integer isCorrect;

    @ApiModelProperty("正确标注个数")
    private Integer biao;

    @ApiModelProperty("应该标注未标注个数")
    private Integer cuo;

    @ApiModelProperty("错误标注个数")
    private Integer wu;

    @ApiModelProperty("属性个数")
    private Integer shu;

    @ApiModelProperty("总共需要标注个数")
    private Integer zong;

    @ApiModelProperty("学生标注个数")
    private Integer da;

    @ApiModelProperty("标注准确率 = biao / da")
    private BigDecimal accuracyRate;

    @ApiModelProperty("标注准确覆盖率 = biao / zong")
    private BigDecimal coverageRate;

    @ApiModelProperty("题目操作时长（单位：秒）")
    private Integer operationDuration;
}
