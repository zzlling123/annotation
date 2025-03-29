package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 考试-班级关联表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:15:00
 */
@Getter
@Setter
@TableName("exam_class")
public class ExamClass extends DataEntity {

    /**
     * 考试ID
     */
    @TableField("exam_id")
    private Integer examId;

    /**
     * 班级ID
     */
    @TableField("class_id")
    private Integer classId;


}
