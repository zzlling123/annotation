package com.xinkao.erp.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("class_info")
public class ClassInfo extends DataEntity {

    
    @TableField("class_name")
    private String className;

    
    @TableField("description")
    private String description;

    
    @TableField("state")
    private Integer state;

    
    @TableField("director_id")
    private Integer directorId;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;


}
