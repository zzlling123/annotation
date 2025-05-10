package com.xinkao.erp.exam.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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
	 * 操作题标头
	 */
	@TableField("title")
	private String title;

	/**
	 * 题干,json列表
	 */
	@ApiModelProperty("题干,json列表")
	private String question;

	/**
	 * 操作题文件路径JSON
	 */
	@TableField("json_url")
	private String jsonUrl;

	/**
	 * 选项列表 json
	 */
	@ApiModelProperty("选项列表 json")
	private String options;

	/**
	 * 如果是填空题，则为有几个空
	 */
	@TableField("answer_count")
	private Integer answerCount;

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

	/**
	 * 用户得分
	 */
	@ApiModelProperty("用户得分")
	private Integer userScore;

	/**
	 * 批改人ID
	 */
	@ApiModelProperty("批改人ID")
	private String correctId;

	/**
	 * 批改时间
	 */
	@ApiModelProperty("批改时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date correctTime;

	/**
	 * 是否需要批改0否1是
	 */
	@ApiModelProperty("是否需要批改0否1是")
	private Integer needCorrect;
}
