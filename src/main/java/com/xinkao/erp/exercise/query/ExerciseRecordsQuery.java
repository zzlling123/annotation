package com.xinkao.erp.exercise.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.BasePageQuery;
import com.xinkao.erp.common.model.entity.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ApiModel("练习记录表")
public class ExerciseRecordsQuery  extends BasePageQuery implements Serializable {

    private Integer id;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String remark;

    private Integer userId;

    private String realName;

    private Integer moduleId;

    private Integer shape;

    private Integer questionScore;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long duration;

    private Integer completionStatus;

    private Integer score;

    private String feedback;

    private Integer isDel;
}
