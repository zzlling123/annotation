package com.xinkao.erp.course.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 章节资源表
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
@Getter
@Setter
@TableName("course_resource")
public class CourseResourceQuery extends BasePageQuery implements Serializable {

    /**
     * 所属章节ID
     */
    @ApiModelProperty("所属章节ID")
    @TableField("chapter_id")
    private Long chapterId;

    /**
     * 资源类型(video/ppt/pdf/doc) 
     */
    @ApiModelProperty("资源类型(video/ppt/pdf/doc) ")
    @TableField("resource_type")
    private String resourceType;

    /**
     * 原始文件名
     */
    @ApiModelProperty("原始文件名")
    @TableField("file_name")
    private String fileName;

    /**
     * 存储路径
     */
    @ApiModelProperty("存储路径")
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小(bytes)
     */
    @ApiModelProperty("文件大小(bytes)")
    @TableField("file_size")
    private Long fileSize;


    /**
     * 逻辑删除：0在用，1删除
     */
    @ApiModelProperty("逻辑删除：0在用，1删除")
    @TableField("is_del")
    private Integer isDel;


}
