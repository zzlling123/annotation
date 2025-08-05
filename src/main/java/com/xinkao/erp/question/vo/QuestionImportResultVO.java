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
    
    /**
     * 总数
     */
    private Integer totalCount;
    
    /**
     * 成功数
     */
    private Integer successCount;
    
    /**
     * 失败数
     */
    private Integer failCount;
    
    /**
     * 错误信息列表
     */
    private List<String> errorMessages;
    
    /**
     * 导入token（用于下载错误文件）
     */
    private String token;
}