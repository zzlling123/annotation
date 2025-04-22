package com.xinkao.erp.scene.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzl
 * @since 2025-04-20 16:23:50
 */
@Getter
@Setter
@TableName("scene_pcd_img")
public class ScenePcdImgQuery  extends BasePageQuery implements Serializable {

    /**
     * pcd主键
     */
    @TableField("pcd_id")
    @ApiModelProperty("pcd主键")
    private Integer pcdId;

    /**
     * 图片路径
     */
    @ApiModelProperty("图片路径")
    @TableField("img_path")
    private String imgPath;

    @ApiModelProperty("图片方向")
    @TableField("img_direction")
    private String imgDirection;

    /**
     * 状态:0-正常 1-删除
     */
    @ApiModelProperty("状态:0-正常 1-删除")
    @TableField("is_del")
    private Integer isDel;


}
