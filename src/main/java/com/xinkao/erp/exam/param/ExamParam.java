package com.xinkao.erp.exam.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel("考试参数")
public class ExamParam {

    @ApiModelProperty("考试ID(编辑时存在)")
    private Integer id;

    @ApiModelProperty("考试名称")
    @NotBlank(message = "考试名称不能为空")
    private String examName;

    @ApiModelProperty("开始时间")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty("结束时间")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty("总分")
    @NotBlank(message = "总分不能为空")
    private String score;

    @ApiModelProperty("合格分数")
    @NotBlank(message = "合格分数不能为空")
    private String scorePass;

    @ApiModelProperty("生成方式:0-同题同序 1-同题不同序 2-不同题不同序")
    @NotBlank(message = "生成方式不能为空")
    private String pageMode;

    @ApiModelProperty("班级列表(ID逗号隔开)")
    @NotBlank(message = "班级列表不能为空")
    private String classIds;
}