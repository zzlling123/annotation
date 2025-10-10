package com.xinkao.erp.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@Accessors(chain = true)
public class DataNoIdEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    
    public static final String CREATE_TIME_PROPERTY_NAME = "createTime";

    
    public static final String CREATE_BY_PROPERTY_NAME = "createBy";

    
    public static final String UPDATE_TIME_PROPERTY_NAME = "updateTime";

    
    public static final String UPDATE_BY_PROPERTY_NAME = "updateBy";

    
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    
    @TableField(value =  "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    
    private String remark;
}
