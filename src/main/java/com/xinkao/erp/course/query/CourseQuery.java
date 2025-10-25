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

@Getter
@Setter
@TableName("course")
public class CourseQuery extends BasePageQuery implements Serializable {

    @Size(min = 1, max = 100, message = "课程名字长度应在1到100个字符之间")
    private String courseName;

    private Integer courseStatus;

    private Integer totalChapters;
}
