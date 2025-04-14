package com.xinkao.erp.scene.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@Getter
@Setter
@TableName("scene")
public class SceneQuery  extends BasePageQuery implements Serializable {

    /**
     * 场景名称
     */
    @TableField("scene_name")
    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 场景图片
     */
    @TableField("scene_path")
    @ApiModelProperty("场景路径")
    private String scenePath;

    @ApiModelProperty("场景文件")
    private MultipartFile sceneFile;

    /**
     * 场景图片
     */
    @TableField("scene_pic")
    @ApiModelProperty("场景图片")
    private String scenePic;

    @ApiModelProperty("场景图片文件")
    private MultipartFile scenePicFile;

    /**
     * 场景描述
     */
    @TableField("scene_description")
    @ApiModelProperty("场景描述")
    private String sceneDescription;

    /**
     * 场景帧数
     */
    @TableField("scene_frame_num")
    @ApiModelProperty("场景帧数")
    private Integer sceneFrameNum;

    /**
     * 场景帧对应的图片集合
     */
    @TableField("scene_frame_num_pics")
    @ApiModelProperty("场景帧对应的图片集合")
    private String sceneFrameNumPics;

    /**
     * 状态:0-正常 1-删除
     */
    @TableField("is_del")
    @ApiModelProperty("状态:0-正常 1-删除")
    private Integer isDel;

}
