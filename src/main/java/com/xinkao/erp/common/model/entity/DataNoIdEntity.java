package com.xinkao.erp.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 数据实体基类
 **/
@Data
@Accessors(chain = true)
public class DataNoIdEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * "创建日期"属性名称
     */
    public static final String CREATE_TIME_PROPERTY_NAME = "createTime";

    /**
     * "创建人"属性名称
     */
    public static final String CREATE_BY_PROPERTY_NAME = "createBy";

    /**
     * "最后修改日期"属性名称
     */
    public static final String UPDATE_TIME_PROPERTY_NAME = "updateTime";

    /**
     * "修改人"属性名称
     */
    public static final String UPDATE_BY_PROPERTY_NAME = "updateBy";

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value =  "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}
