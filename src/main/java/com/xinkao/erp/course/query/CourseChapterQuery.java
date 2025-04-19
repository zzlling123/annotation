package com.xinkao.erp.course.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
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
public class CourseChapterQuery extends BasePageQuery implements Serializable {

    /**
     * 所属课程ID
     */
    @ApiModelProperty("课程ID")
    private Integer courseId;

    /**
     * 章节标题
     */
    @ApiModelProperty("章节标题")
    private String chapterTitle;
}
