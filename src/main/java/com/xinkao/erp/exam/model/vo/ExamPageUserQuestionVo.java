package com.xinkao.erp.exam.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ExamPageUserQuestionVo {

	@ApiModelProperty("试题主键")
	private String id;
	@ApiModelProperty("试卷主键")
	private Integer userId;

	@ApiModelProperty("考试项目主键")
	private Integer examId;

	@ApiModelProperty("题号的数字格式")
	private Integer numSort;

	@ApiModelProperty("题号")
	private String num;
	@ApiModelProperty("题目分类")
	private Integer type;
	@ApiModelProperty("题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题")
	private String shape;

	@ApiModelProperty("题库来源试题主键")
	private Integer oldQuestionId;

	@TableField("title")
	private String title;

	@ApiModelProperty("题干,json列表")
	private String question;

	@TableField("json_url")
	private String jsonUrl;
	@ApiModelProperty("选项列表 json")
	private String options;

	@TableField("answer_count")
	private Integer answerCount;
	@ApiModelProperty("题目分数")
	private BigDecimal score;

	@ApiModelProperty("部分答对分数(限多选)")
	private BigDecimal scorePart;

	@ApiModelProperty("答案")
	private String answer;

	@ApiModelProperty("用户答案")
	private String userAnswer;
	@ApiModelProperty("用户得分")
	private BigDecimal userScore;

	@ApiModelProperty("批改人ID")
	private String correctId;

	@ApiModelProperty("批改时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date correctTime;

	
	@ApiModelProperty("是否需要批改0否1是")
	private Integer needCorrect;

	@ApiModelProperty("是否题目单，0否1是")
	private Integer isForm;

	@TableField("文档路径")
	private String fileUrl;

	@ApiModelProperty("题目单所属标题及子题")
	private List<ExamPageUserQuestionFormTitleVo> examPageUserQuestionFormTitleVoList;
}
