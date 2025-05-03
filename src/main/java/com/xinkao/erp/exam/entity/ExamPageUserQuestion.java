package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 试卷表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:06:02
 */
@Getter
@Setter
@TableName("exam_page_user_question")
public class ExamPageUserQuestion extends DataSnowIdEntity {

    /**
     * 试卷主键
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 考试项目主键
     */
    @TableField("exam_id")
    private Integer examId;

    /**
     * 题号的数字格式
     */
    @TableField("num_sort")
    private Integer numSort;

    /**
     * 题号
     */
    @TableField("num")
    private String num;

    /**
     * 题目分类
     */
    @TableField("type")
    private Integer type;

    /**
     * 题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
     */
    @TableField("shape")
    private Integer shape;

    /**
     * 题库来源试题主键
     */
    @TableField("old_question_id")
    private Integer oldQuestionId;

    /**
     * 题干,json列表
     */
    @TableField("question")
    private String question;

    /**
     * 操作题文件路径JSON
     */
    @TableField("json_url")
    private String jsonUrl;

    /**
     * 选项列表 json
     */
    @TableField("options")
    private String options;

    /**
     * 如果是填空题，则为有几个空
     */
    @TableField("answer_count")
    private Integer answerCount;

    /**
     * 题目分数
     */
    @TableField("score")
    private Integer score;

    /**
     * 部分答对分数(限多选)
     */
    @TableField("score_part")
    private Integer scorePart;

    /**
     * 答案
     */
    @TableField("answer")
    private String answer;

    /**
     * 是否需要批改0否1是
     */
    @TableField("need_correct")
    private Integer needCorrect;

    /**
     * 用户答案
     */
    @TableField(exist = false)
    private String userAnswer;


}
