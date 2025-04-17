package com.xinkao.erp.exam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 题目返回实体
 * @author Ldy
 *
 */
@Data
public class ExamPageUserQuestionVo {

	/**
	 * 试题主键
	 */
	@ApiModelProperty("试题主键")
	private String id;

	/**
	 * 试卷主键
	 */
	@ApiModelProperty("试卷主键")
	private Integer userId;

	/**
	 * 考试项目主键
	 */
	@ApiModelProperty("考试项目主键")
	private Integer examId;

	/**
	 * 题号的数字格式
	 */
	@ApiModelProperty("题号的数字格式")
	private Integer numSort;

	/**
	 * 题号
	 */
	@ApiModelProperty("题号")
	private String num;

	/**
	 * 题目分类
	 */
	@ApiModelProperty("题目分类")
	private Integer type;

	/**
	 * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
	 */
	@ApiModelProperty("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
	private String shape;

	/**
	 * 题库来源试题主键
	 */
	@ApiModelProperty("题库来源试题主键")
	private Integer oldQuestionId;

	/**
	 * 题干,json列表
	 */
	@ApiModelProperty("题干,json列表")
	private String question;

	/**
	 * 选项列表 json
	 */
	@ApiModelProperty("选项列表 json")
	private String options;

	/**
	 * 题目分数
	 */
	@ApiModelProperty("题目分数")
	private Integer score;

	/**
	 * 部分答对分数(限多选)
	 */
	@ApiModelProperty("部分答对分数(限多选)")
	private Integer scorePart;

	/**
	 * 答案
	 */
	@ApiModelProperty("答案")
	private String answer;

	/**
	 * 用户答案
	 */
	@ApiModelProperty("用户答案")
	private String userAnswer;


}
