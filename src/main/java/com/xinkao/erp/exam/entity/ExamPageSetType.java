package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@TableName("exam_page_set_type")
public class ExamPageSetType extends DataEntity {

    
    @TableField("exam_id")
    private String examId;

    
    @TableField("type_id")
    private Integer typeId;

    
    @TableField("type_name")
    private String typeName;

    
    @TableField("shape")
    private Integer shape;

    
    @TableField("question_num")
    private Integer questionNum;

    
    @TableField("score")
    private BigDecimal score;

    
    @TableField("score_part")
    private BigDecimal scorePart;


}
