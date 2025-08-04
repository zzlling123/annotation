package com.xinkao.erp.exam.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 题目返回实体
 * @author Ldy
 *
 */
@Data
public class ExamPageUserQuestionChildVo {

	@ApiModelProperty("标题主键")
	private String id;

	/**
	 * 所属题目单ID
	 */
	@ApiModelProperty("所属题目单ID")
	private String questionId;

	/**
	 * 所属题目单二级列表ID
	 */
	@ApiModelProperty("所属题目单二级列表ID")
	private String pid;

	/**
	 * 试卷主键
	 */
	@ApiModelProperty("用户主键")
	private Integer userId;

	/**
	 * 考试项目主键
	 */
	@ApiModelProperty("考试项目主键")
	private Integer examId;

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
	 * 排序
	 */
	@ApiModelProperty("排序")
	private Integer sort;

	/**
	 * 用户答案
	 */
	@ApiModelProperty("用户答案")
	private String userAnswer;

	@ApiModelProperty("正确答案")
	private String rightAnswer;

}
