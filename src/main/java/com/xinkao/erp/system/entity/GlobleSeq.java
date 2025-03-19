package com.xinkao.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户中心-全局ID增长信息表(业务)
 * </p>
 *
 * @author hanhys
 * @since 2022-05-30 16:11:50
 */
@Getter
@Setter
@TableName("globle_seq")
public class GlobleSeq extends BaseEntity {

    /**业务代码**/
    @TableId("code")
    private String code;

    /**业务名**/
    @TableField("name")
    private String name;

    /**递增值**/
    @TableField("increment")
    private Long increment;

    /**当前值**/
    @TableField("current_no")
    private Long currentNo;

    /**当前值**/
    @TableField("current_value")
    private String currentValue;

}
