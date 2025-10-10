package com.xinkao.erp.question.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionImportResultVO {

    private Integer totalCount;

    private Integer successCount;

    private Integer failCount;

    private List<String> errorMessages;

    private String token;

    private List<RowError> rowErrors;

    @Data
    public static class RowError {
        private Integer rowNum;
        private String message;
        private String warningType;
        private Boolean isWarning;
    }
}