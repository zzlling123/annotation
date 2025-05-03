package com.xinkao.erp.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.user.entity.Menu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 * 操作题标记类型表
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 21:22:31
 */
@Getter
@Setter
@TableName("mark")
public class Mark extends DataEntity {

    /**
     * 题目分类
     */
    @TableField("type")
    private Integer type;

    /**
     * 题目分类名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 字典名称
     */
    @TableField("mark_name")
    private String markName;

    /**
     * 字典值
     */
    @TableField("value")
    private String value;

    /**
     * 父级菜单
     */
    @TableField("pid")
    private Integer pid;

    @ApiModelProperty("父级ID列表")
    @TableField("parent")
    private String parent;

    @ApiModelProperty("父级名称列表")
    @TableField("parent_route")
    private String parentRoute;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 1开启0禁用
     */
    @TableField("state")
    private Integer state;

    @ApiModelProperty("是否删除0否1是")
    @TableField("is_del")
    private Integer isDel;

    @ApiModelProperty("子集菜单")
    @TableField(exist = false)
    private List<Mark> childMarkList;


}
