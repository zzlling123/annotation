package com.xinkao.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户相关字典(两级)
 * </p>
 *
 * @author hanhys
 * @since 2022-05-30 16:11:50
 */
@Getter
@Setter
@TableName("globle_dict")
public class Dict extends DataEntity {

    /**分类主键**/
    @TableField("dict_type")
    private String dictType;

    /**字典名称**/
    @TableField("dict_label")
    private String dictLabel;

    /**字典值**/
    @TableField("dict_value")
    private String dictValue;

    /**排序**/
    @TableField("sort")
    private Integer sort;

}
