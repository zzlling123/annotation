package com.xinkao.erp.exam.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class ExamPageUserQuestionChildVo {

	@ApiModelProperty("标题主键")
	private String id;

	
	@ApiModelProperty("所属题目单ID")
	private String questionId;

	
	@ApiModelProperty("所属题目单二级列表ID")
	private String pid;

	
	@ApiModelProperty("用户主键")
	private Integer userId;

	
	@ApiModelProperty("考试项目主键")
	private Integer examId;

	
	@ApiModelProperty("操作题标头")
	private String title;

	
	@ApiModelProperty("题干,json列表")
	private String question;

	
	@ApiModelProperty("文本框默认展示文字")
	private String defaultText;

	
	@ApiModelProperty("题干纯文字")
	private String questionText;

	
	@ApiModelProperty("是否上传文件题0否1是")
	private Integer isFile;

	
	@ApiModelProperty("文件后缀")
	private String fileType;


	
	@ApiModelProperty("排序")
	private Integer sort;

	
	@ApiModelProperty("用户答案")
	private String userAnswer;

	@ApiModelProperty("正确答案")
	private String rightAnswer;

}
