package com.xinkao.erp.exercise.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ApiModel("练习记录表-数据统计")
public class ExerciseRecordsVo {
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

    private Integer biao;

    private Integer cuo;

    private Integer wu;

    private Integer shu;

    private Integer zong;

    private Integer da;

    private BigDecimal accuracyRate;

    private BigDecimal coverageRate;

}
