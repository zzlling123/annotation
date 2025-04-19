package com.xinkao.erp.course.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;

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
public class CourseQuery extends BasePageQuery implements Serializable {

    /**
     * 课程名字
     */
    @ApiModelProperty("课程名字")
    @Size(min = 1, max = 100, message = "课程名字长度应在1到100个字符之间")
    private String courseName;

    /**
     * 课程状态(0:禁用 1:启用)
     */
    @ApiModelProperty("课程状态(0启用: 1:禁用)")
    private Integer courseStatus;
}
