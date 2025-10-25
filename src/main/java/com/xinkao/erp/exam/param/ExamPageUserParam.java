package com.xinkao.erp.exam.param;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class ExamPageUserParam {

    
    @ApiModelProperty("考试项目主键")
    private Integer examId;

    
    @ApiModelProperty("用户主键")
    private Integer userId;

    
    @ApiModelProperty("用户名称")
    private String realName;

    
    @ApiModelProperty("班级主键")
    private Integer classId;

    
    @ApiModelProperty("题状态:0-未选题 1-选题中 2-选题完成")
    private Integer selectStatus;

    
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    private Integer answerStatus;

    
    @ApiModelProperty("(汇总出分)提交时间")
    private String answerTs;

    
    @ApiModelProperty("答题开始时间")
    private String startTs;


    
    @ApiModelProperty("答题结束时间")
    private String endTs;

    
    @ApiModelProperty("最后得分")
    private BigDecimal score;

    
    @ApiModelProperty("出分时间")
    private String scoreTs;

    
    @ApiModelProperty("合格状态:0-不合格 1-合格")
    private Integer passStatus;

    
    @ApiModelProperty("是否已经全部批改0否1是")
    private Integer onCorrect;

    
    @ApiModelProperty("是否需要批改0否1是")
    private Integer needCorrect;

    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    
    @TableField(value =  "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    
    private String remark;

    @ApiModelProperty("正确标注个数")
    private Integer biao;

    @ApiModelProperty("应该标注未标注个数")
    private Integer cuo;

    @ApiModelProperty("错误标注个数")
    private Integer wu;

    @ApiModelProperty("属性个数")
    private Integer shu;

    @ApiModelProperty("总共需要标注个数")
    private Integer zong;

    @ApiModelProperty("学生标注个数")
    private Integer da;

    @ApiModelProperty("标注准确率 = biao / da")
    private BigDecimal accuracyRate;

    @ApiModelProperty("标注准确覆盖率 = biao / zong")
    private BigDecimal coverageRate;
}
