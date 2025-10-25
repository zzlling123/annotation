package com.xinkao.erp.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class HandleResult {

    private Integer totalCount = 0;

    private Integer successCount = 0;

    private Integer errorCount = 0;

    private List<String> errorList = new ArrayList<>();

    private Map<String, Object> result = new HashMap<>();
}
