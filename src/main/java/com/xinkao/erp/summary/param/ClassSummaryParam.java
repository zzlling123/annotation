package com.xinkao.erp.summary.param;

import com.xinkao.erp.exam.model.vo.ExamPageUserVo;
import lombok.Data;

import java.util.List;

@Data
public class ClassSummaryParam {

    //考试id
    private String examId;

    //平均分
    private String avgScore;

    //最高分
    private String maxScore;

    //最低分
    private String minScore;

    //学生考试详情
    private List<ExamPageUserVo> examPageUserVoList;

}
