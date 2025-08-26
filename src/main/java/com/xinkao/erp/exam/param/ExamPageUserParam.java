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

/**
 * <p>
 * 考生考试信息表
 * </p>
 *
 */
@Getter
@Setter
public class ExamPageUserParam {

    /**
     * 考试项目主键
     */
    @ApiModelProperty("考试项目主键")
    private Integer examId;

    /**
     * 用户主键
     */
    @ApiModelProperty("用户主键")
    private Integer userId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String realName;

    /**
     * 班级主键
     */
    @ApiModelProperty("班级主键")
    private Integer classId;

    /**
     * 选题状态:0-未选题 1-选题中 2-选题完成
     */
    @ApiModelProperty("题状态:0-未选题 1-选题中 2-选题完成")
    private Integer selectStatus;

    /**
     * 作答状态: 0-未做答 1-进行中 2-已提交
     */
    @ApiModelProperty("作答状态: 0-未做答 1-进行中 2-已提交")
    private Integer answerStatus;

    /**
     * (汇总出分)提交时间
     */
    @ApiModelProperty("(汇总出分)提交时间")
    private String answerTs;

    /**
     * 答题开始时间
     */
    @ApiModelProperty("答题开始时间")
    private String startTs;


    /**
     * 答题结束时间
     */
    @ApiModelProperty("答题结束时间")
    private String endTs;

    /**
     * 最后得分
     */
    @ApiModelProperty("最后得分")
    private BigDecimal score;

    /**
     * 出分时间
     */
    @ApiModelProperty("出分时间")
    private String scoreTs;

    /**
     * 合格状态:0-不合格 1-合格
     */
    @ApiModelProperty("合格状态:0-不合格 1-合格")
    private Integer passStatus;

    /**
     * 是否已经全部批改0否1是
     */
    @ApiModelProperty("是否已经全部批改0否1是")
    private Integer onCorrect;

    /**
     * 是否需要批改0否1是
     */
    @ApiModelProperty("是否需要批改0否1是")
    private Integer needCorrect;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value =  "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 备注
     */
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
