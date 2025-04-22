package com.xinkao.erp.question.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class QuestionParam {

    @ApiModelProperty("题库ID")
    private Integer id;

    @ApiModelProperty("题目")
    @NotBlank(message = "题目不能为空")
    @Size(min = 1, max = 255, message = "题目长度应在1到255个字符之间")
    private String question;

    @ApiModelProperty("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    @NotBlank(message = "题目类型不能为空")
    private String shape;

    @ApiModelProperty("题型分类")
    @NotBlank(message = "题型分类不能为空")
    private String type;

    @ApiModelProperty("难度")
    @NotBlank(message = "难度不能为空")
    private String difficultyLevel;

    /**
     * 选项列表 json["A","B","C"]
     */
    @ApiModelProperty("选项列表")
    private List<String> options;

    @ApiModelProperty("答案")
    @NotBlank(message = "答案不能为空")
    private String answer;

    @ApiModelProperty("解析")
    private String answerTip;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;

    @ApiModelProperty("自定义标签ID列表")
    private List<Integer> labels;

    @ApiModelProperty("markId列表")
    private List<Integer> markIds;
}