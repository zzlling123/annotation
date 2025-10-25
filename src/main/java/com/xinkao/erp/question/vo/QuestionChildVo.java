package com.xinkao.erp.question.vo;

import com.xinkao.erp.common.model.entity.BaseEntity;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionChildVo extends BaseEntity implements OutputConverter<QuestionChildVo, Question> {

    private Integer id;

    private Integer questionId;

    private Integer pid;

    private String title;

    private String question;

    private String defaultText;

    private String questionText;

    private Integer isFile;

    private String fileType;

    private String answer;

    private String answerTip;

    private Integer sort;
}