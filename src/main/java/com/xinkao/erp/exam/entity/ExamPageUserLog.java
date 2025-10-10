package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataSnowIdEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("exam_page_user_log")
public class ExamPageUserLog extends DataSnowIdEntity {

    
    @TableField("user_id")
    private Integer userId;

    
    @TableField("exam_id")
    private Integer examId;

    
    @TableField("exam_length")
    private Integer examLength;

    
    @TableField("start_ts")
    private String startTs;

    
    @TableField("last_update_ts")
    private String lastUpdateTs;

    
    @TableField("submit_status")
    private Integer submitStatus;


}
