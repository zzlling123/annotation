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

@Getter
@Setter
@TableName("course_chapter")
public class CourseChapter extends DataEntity implements Serializable {

    @TableField("course_id")
    private Integer courseId;

    @NotBlank(message = "章节标题不能为空")
    @Size(min = 1, max = 100, message = "章节标题长度应在1到100个字符之间")
    @TableField("chapter_title")
    private String chapterTitle;

    @TableField("chapter_order")
    private Integer chapterOrder;

    @Size(max = 255, message = "章节描述长度应在255个字符以内")
    @TableField("description")
    private String description;

    @TableField("is_del")
    private Integer isDel;

    @TableField("cover_image")
    private String coverImage;

}
