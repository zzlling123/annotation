package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@TableName("exam_page_set")
public class ExamPageSet extends DataEntity {

    
    @TableField("exam_id")
    private Integer examId;

    
    @TableField("score")
    private BigDecimal score;

    
    @TableField("score_pass")
    private BigDecimal scorePass;

    
    @TableField("page_mode")
    private Integer pageMode;

    
    @TableField("flip_type")
    private Integer flipType;

    
    @TableField("question_count")
    private Integer questionCount;

    
    @TableField("question_status")
    private Integer questionStatus;


}
