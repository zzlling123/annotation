package com.xinkao.erp.summary.param;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SummaryStuParam {
    private Integer type;
    private List<Integer> stuId;
}
