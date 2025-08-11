package com.xinkao.erp.question.vo;

import lombok.Data;

import java.util.List;

/**
 * 题目导入结果
 * @author Ldy
 * @since 2025-01-25
 */
@Data
public class QuestionImportResultVO {
    /** 总数 */
    private Integer totalCount;
    /** 成功数 */
    private Integer successCount;
    /** 失败数 */
    private Integer failCount;
    /** 错误信息列表（保留向后兼容） */
    private List<String> errorMessages;
    /** 导入token（用于下载错误文件） */
    private String token;

    /** 结构化错误明细（推荐使用） */
    private List<RowError> rowErrors;

    @Data
    public static class RowError {
        private Integer rowNum;   // Excel 行号（可能为null）
        private String message;   // 错误描述
    }
}