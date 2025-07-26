package com.xinkao.erp.question.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionChildVo extends BaseEntity implements OutputConverter<QuestionChildVo, Question> {

    /**
     * 子题ID
     */
    @ApiModelProperty("子题ID")
    private Integer id;

    /**
     * 所属题目单ID
     */
    @ApiModelProperty("所属题目单ID")
    private Integer questionId;

    /**
     * 所属题目单二级列表ID
     */
    @ApiModelProperty("所属题目单二级列表ID")
    private Integer pid;

    /**
     * 操作题标头
     */
    @ApiModelProperty("操作题标头")
    private String title;

    /**
     * 题干,json列表
     */
    @ApiModelProperty("题干,json列表")
    private String question;

    /**
     * 文本框默认展示文字
     */
    @ApiModelProperty("文本框默认展示文字")
    private String defaultText;

    /**
     * 题干纯文字
     */
    @ApiModelProperty("题干纯文字")
    private String questionText;

    /**
     * 是否上传文件题0否1是
     */
    @ApiModelProperty("是否上传文件题0否1是")
    private Integer isFile;

    /**
     * 文件后缀
     */
    @ApiModelProperty("文件后缀")
    private String fileType;

    /**
     * 答案A
     */
    @ApiModelProperty("答案A")
    private String answer;

    /**
     * 答案说明
     */
    @ApiModelProperty("答案说明")
    private String answerTip;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;
}