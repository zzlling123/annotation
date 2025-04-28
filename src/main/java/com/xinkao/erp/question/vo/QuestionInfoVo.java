package com.xinkao.erp.question.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.question.entity.Label;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class QuestionInfoVo extends BaseEntity implements OutputConverter<QuestionInfoVo, Question> {

    @ApiModelProperty("题目")
    private String question;

    @ApiModelProperty("题目text")
    private String questionText;

    @ApiModelProperty("题目答案")
    private String answer;

    /**
     * 操作题文件路径JSON
     */
    @TableField("json_url")
    private String jsonUrl;

    /**
     * 选项列表 json["A","B","C"]
     */
    @TableField("options")
    private List<String> options;

    /**
     * 如果是填空题，则为有几个空
     */
    @TableField("answer_count")
    private Integer answerCount;

    /**
     * 答案说明
     */
    @TableField("answer_tip")
    private String answerTip;

    @TableField("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
    private String shape;

    @ApiModelProperty("题型")
    private String type;

    @ApiModelProperty("难度")
    private String difficultyLevel;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty("自定义标签列表")
    private List<LabelVo> labelList;
}