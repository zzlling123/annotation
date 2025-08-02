package com.xinkao.erp.exam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 题目返回实体
 * @author Ldy
 *
 */
@Data
public class ExamPageUserQuestionFormTitleVo {

	@ApiModelProperty("标题主键")
	private String id;

	@ApiModelProperty("试卷主键")
	private Integer userId;

	@ApiModelProperty("考试项目主键")
	private Integer examId;

	@ApiModelProperty("所属题目单ID")
	private String pid;

	@ApiModelProperty("题干,json列表")
	private String question;

	@ApiModelProperty("题干纯文字")
	private String questionText;

	@ApiModelProperty("排序")
	private Integer sort;

	@ApiModelProperty("下属子题")
	private List<ExamPageUserQuestionChildVo> examPageUserQuestionChildVoList;

}
