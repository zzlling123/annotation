package com.xinkao.erp.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

/**
 * <p>
 * 课程表	
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 16:53:36
 */
@Getter
@Setter
@TableName("course")
public class Course extends DataEntity {

    /**
     * 课程名字
     */
    @ApiModelProperty("课程名字")
    @Size(min = 1, max = 100, message = "课程名字长度应在1到100个字符之间")
    @TableField("course_name")
    private String courseName;

    /**
     * 课程状态(1启用0禁用)
     */
    @ApiModelProperty("课程状态(1启用0禁用)")
    @TableField("course_status")
    private Integer courseStatus;

    /**
     * 章节总数
     */
    @TableField("total_chapters")
    @ApiModelProperty("章节总数")
    private Integer totalChapters;

    /**
     * 是否删除0否1是
     */
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("班级id")
    @TableField("class_id")
    private Integer classId;

    @ApiModelProperty("班级名字")
    @TableField(exist = false)
    private String className;

    @ApiModelProperty("教师id")
    @TableField("teacher_id")
    private Integer teacherId;

    //该属性不是表中属性
    @ApiModelProperty("教师名字")
    @TableField(exist = false)
    private String teacherName;

    @ApiModelProperty("封面图片")
    @TableField("cover_image")
    private String coverImage;


}
