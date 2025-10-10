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

    private Integer id;

    private String question;

    private Integer sort;

    private List<QuestionChildVo> questionChildList;
}