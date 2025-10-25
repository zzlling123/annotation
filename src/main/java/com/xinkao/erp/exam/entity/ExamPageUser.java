package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@TableName("exam_page_user")
public class ExamPageUser extends DataSnowIdEntity {


    @TableField("exam_id")
    private Integer examId;


    @TableField("user_id")
    private Integer userId;


    @TableField("class_id")
    private Integer classId;


    @TableField("select_status")
    private Integer selectStatus;


    @TableField("answer_status")
    private Integer answerStatus;


    @TableField("answer_ts")
    private String answerTs;


    @TableField("start_ts")
    private String startTs;



    @TableField("end_ts")
    private String endTs;


    @TableField("score")
    private BigDecimal score;


    @TableField("score_ts")
    private String scoreTs;


    @TableField("pass_status")
    private Integer passStatus;


    @TableField("on_correct")
    private Integer onCorrect;


    @TableField("need_correct")
    private Integer needCorrect;


}
