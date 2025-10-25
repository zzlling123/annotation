package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("exam")
public class Exam extends DataEntity {

    
    @TableField("exam_name")
    private String examName;

    
    @TableField("start_time")
    private Date startTime;

    
    @TableField("end_time")
    private Date endTime;

    
    @TableField("duration")
    private Integer duration;

    
    @TableField("state")
    private Integer state;

    
    @TableField("difficulty_level")
    private Integer difficultyLevel;

    
    @TableField("symbol")
    private String symbol;

    
    @TableField("roll_make_over")
    private Integer rollMakeOver;

    
    @TableField("is_del")
    private Integer isDel;

    
    @TableField("is_expert")
    private Integer isExpert;


}
