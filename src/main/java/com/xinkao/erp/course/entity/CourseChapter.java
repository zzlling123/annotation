package com.xinkao.erp.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * <p>
 * 课程章节表
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
@Getter
@Setter
@TableName("course_chapter")
public class CourseChapter extends DataEntity implements Serializable {

    /**
     * 所属课程ID
     */
    @ApiModelProperty("课程ID")
    @TableField("course_id")
    private Integer courseId;

    /**
     * 章节标题
     */
    @ApiModelProperty("章节标题")
    @NotBlank(message = "章节标题不能为空")
    @Size(min = 1, max = 100, message = "章节标题长度应在1到100个字符之间")
    @TableField("chapter_title")
    private String chapterTitle;

    /**
     * 章节排序
     */
    @ApiModelProperty("章节排序")
    @TableField("chapter_order")
    private Integer chapterOrder;

    /**
     * 章节描述
     */
    @ApiModelProperty("章节描述")
    @Size(max = 255, message = "章节描述长度应在255个字符以内")
    @TableField("description")
    private String description;

    /**
     * 删除：0在用，1删除
     */
    @ApiModelProperty("删除：0在用，1删除")
    @TableField("is_del")
    private Integer isDel;

}
