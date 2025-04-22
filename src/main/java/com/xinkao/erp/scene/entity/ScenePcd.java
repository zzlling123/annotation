package com.xinkao.erp.scene.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
@TableName("scene_pcd")
public class ScenePcd extends DataEntity {

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    @TableField("scene_id")
    private Integer sceneId;


    @TableField("pcd_path")
    @ApiModelProperty("场景路径")
    private String pcdPath;

    /**
     * 状态:0-正常 1-删除
     */
    @ApiModelProperty("状态:0-正常 1-删除")
    @TableField("is_del")
    private Integer isDel;

    //图片集合
    @TableField(exist = false)
    @ApiModelProperty("场景图片集合")
    private List<ScenePcdImg> ScenePcdImgList;


}
