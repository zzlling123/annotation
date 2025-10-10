package com.xinkao.erp.exam.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamResultVo {

    private Long examId;

    private Long userId;

    private Integer totalScore;

    private Integer obtainedScore;

}