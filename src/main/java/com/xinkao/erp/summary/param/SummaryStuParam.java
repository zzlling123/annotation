package com.xinkao.erp.summary.param;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SummaryStuParam {
    //是考试还是练习
    private Integer type;
    //学生id
    private List<Integer> stuId;
}
