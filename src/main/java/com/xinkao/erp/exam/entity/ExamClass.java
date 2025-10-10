package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("exam_class")
public class ExamClass extends BaseEntity {


    @TableField("exam_id")
    private Integer examId;


    @TableField("class_id")
    private Integer classId;


}
