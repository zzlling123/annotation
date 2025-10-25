package com.xinkao.erp.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@TableName("course")
public class Course extends DataEntity {

    @Size(min = 1, max = 100, message = "课程名字长度应在1到100个字符之间")
    @TableField("course_name")
    private String courseName;

    @TableField("course_status")
    private Integer courseStatus;

    @TableField("total_chapters")
    private Integer totalChapters;

    @TableField("is_del")
    private Integer isDel;

    @TableField("class_id")
    private Integer classId;

    @TableField(exist = false)
    private String className;

    @TableField("teacher_id")
    private Integer teacherId;

    @TableField(exist = false)
    private String teacherName;

    @TableField("cover_image")
    private String coverImage;


}
