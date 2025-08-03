package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("exam_expert")
public class ExamExpert {

    @TableField("expert_id")
    private Integer expertId;
    @TableField("exam_id")
    private Integer examId;
}
