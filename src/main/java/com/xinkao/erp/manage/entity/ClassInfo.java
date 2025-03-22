package com.xinkao.erp.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 班级表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-21 14:32:24
 */
@Getter
@Setter
@TableName("class_info")
public class ClassInfo extends DataEntity {

    /**
     * 班级名称
     */
    @TableField("class_name")
    private String className;

    /**
     * 是否显示1显示0隐藏
     */
    @TableField("state")
    private Integer state;

    /**
     * 负责人Id
     */
    @TableField("director_id")
    private Integer directorId;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;


}
