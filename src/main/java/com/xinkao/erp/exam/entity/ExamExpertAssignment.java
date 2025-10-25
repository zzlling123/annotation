package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("exam_expert_assignment")
public class ExamExpertAssignment extends DataEntity {

    @ApiModelProperty("考试ID")
    @TableField("exam_id")
    private Integer examId;

    @ApiModelProperty("专家ID")
    @TableField("expert_id")
    private Integer expertId;

    @ApiModelProperty("学生ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("学生姓名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("分配状态：0-未判卷，2-已判卷")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("是否删除：0-否，1-是")
    @TableField("is_del")
    private Integer isDel;
} 