package com.xinkao.erp.summary.param;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SummaryParam {
    //是考试还是练习
    private Integer type;
    //班级id
    private Integer classId;

}
