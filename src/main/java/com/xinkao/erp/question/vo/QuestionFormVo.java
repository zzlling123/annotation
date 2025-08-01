package com.xinkao.erp.question.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class QuestionFormVo extends BaseEntity implements OutputConverter<QuestionFormVo, Question> {

    @ApiModelProperty("题目二级标题id")
    private Integer id;

    @ApiModelProperty("题干,json列表")
    private String question;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("子题列表")
    private List<QuestionChildVo> questionChildList;
}