package com.xinkao.erp.question.service;

import com.alibaba.excel.util.StringUtils;
import com.xinkao.erp.question.entity.DifficultyPoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识点匹配缓存管理器
 * 用于批量导入时的知识点匹配，避免重复数据库查询
 * 
 * @author Ldy
 * @since 2025-09-14
 */
@Slf4j
@Component
public class KnowledgePointCacheManager {
    
    @Autowired
    private DifficultyPointService difficultyPointService;
    
    /**
     * 构建知识点缓存
     * Key: 难度等级, Value: 该难度下的所有知识点列表
     * 
     * @return 知识点缓存Map
     */
    public Map<Integer, List<DifficultyPoint>> buildKnowledgePointCache() {
        log.info("开始构建知识点缓存...");
        
        // 一次性查询所有有效的知识点
        List<DifficultyPoint> allPoints = difficultyPointService.lambdaQuery()
            .eq(DifficultyPoint::getIsDel, 0)
            .list();
        
        // 按难度等级分组
        Map<Integer, List<DifficultyPoint>> cache = allPoints.stream()
            .collect(Collectors.groupingBy(DifficultyPoint::getDifficultyLevel));
        
        log.info("知识点缓存构建完成，时间: {}, 共{}个难度等级，总计{}个知识点", 
            LocalDateTime.now(), cache.size(), allPoints.size());
        
        // 🔥 详细打印每个难度等级的知识点列表
        cache.forEach((level, points) -> {
            log.info("=== 难度等级 {} 包含 {} 个知识点 ===", level, points.size());
            for (int i = 0; i < points.size(); i++) {
                DifficultyPoint point = points.get(i);
                log.info("  {}. [ID:{}] {}", (i + 1), point.getId(), point.getPointName());
            }
            log.info("=== 难度等级 {} 知识点列表结束 ===", level);
        });
        
        return cache;
    }
    
    /**
     * 从缓存中匹配知识点
     * 
     * @param cache 知识点缓存
     * @param difficultyLevel 难度等级
     * @param knowledgePointName 知识点名称
     * @return 匹配结果
     */
    public KnowledgePointMatchResult matchFromCache(
            Map<Integer, List<DifficultyPoint>> cache, 
            Integer difficultyLevel, 
            String knowledgePointName) {
        
        KnowledgePointMatchResult result = new KnowledgePointMatchResult();
        
        // 空值处理 - 允许为空
        if (StringUtils.isBlank(knowledgePointName)) {
            result.setMatched(true);
            result.setDifficultyPointId(null);
            result.setMatchType("EMPTY");
            return result;
        }
        
        // 从缓存中获取该难度等级的知识点
        List<DifficultyPoint> points = cache.getOrDefault(difficultyLevel, Collections.emptyList());
        
        log.info("🔍 开始匹配知识点：用户输入=[{}], 难度等级=[{}], 该等级可用知识点数量=[{}]", 
            knowledgePointName, difficultyLevel, points.size());
        
        if (points.isEmpty()) {
            log.warn("❌ 难度等级 {} 下没有可用的知识点", difficultyLevel);
            result.setMatched(false);
            result.setDifficultyPointId(null);
            result.setMatchType("NO_AVAILABLE_POINTS");
            result.setErrorMessage("难度等级 " + difficultyLevel + " 下没有可用的知识点");
            return result;
        }
        
        // 打印该难度等级下的所有可用知识点
        log.info("📋 难度等级 {} 的可用知识点列表：", difficultyLevel);
        for (int i = 0; i < points.size(); i++) {
            DifficultyPoint point = points.get(i);
            log.info("    {}. [ID:{}] {}", (i + 1), point.getId(), point.getPointName());
        }
        
        String trimmedName = knowledgePointName.trim();
        
        // 1. 精确匹配
        Optional<DifficultyPoint> exactMatch = points.stream()
            .filter(p -> trimmedName.equals(p.getPointName()))
            .findFirst();
        
        if (exactMatch.isPresent()) {
            result.setMatched(true);
            result.setDifficultyPointId(exactMatch.get().getId());
            result.setMatchType("EXACT");
            log.info("✅ 精确匹配成功：用户输入=[{}] -> 匹配到知识点=[{}] (ID:{})", 
                trimmedName, exactMatch.get().getPointName(), exactMatch.get().getId());
            return result;
        }
        
        log.info("⚠️ 精确匹配失败，尝试模糊匹配...");
        
        // 2. 模糊匹配（包含关系）
        Optional<DifficultyPoint> fuzzyMatch = points.stream()
            .filter(p -> p.getPointName().contains(trimmedName) || 
                        trimmedName.contains(p.getPointName()))
            .findFirst();
        
        if (fuzzyMatch.isPresent()) {
            result.setMatched(true);
            result.setDifficultyPointId(fuzzyMatch.get().getId());
            result.setMatchType("FUZZY");
            result.setSuggestion("模糊匹配到: " + fuzzyMatch.get().getPointName());
            log.info("✅ 模糊匹配成功：用户输入=[{}] -> 匹配到知识点=[{}] (ID:{})", 
                trimmedName, fuzzyMatch.get().getPointName(), fuzzyMatch.get().getId());
            return result;
        }
        
        // 3. 未匹配到
        result.setMatched(false);
        result.setDifficultyPointId(null);
        result.setMatchType("NOT_FOUND");
        result.setAvailablePoints(points.stream()
            .map(DifficultyPoint::getPointName)
            .collect(Collectors.toList()));
        
        log.warn("❌ 未匹配到知识点：用户输入=[{}], 难度等级=[{}]", trimmedName, difficultyLevel);
        log.info("💡 该难度等级可选知识点：{}", String.join("、", result.getAvailablePoints()));
        
        return result;
    }
    
    /**
     * 知识点匹配结果
     */
    @Data
    public static class KnowledgePointMatchResult {
        /** 是否匹配成功 */
        private boolean matched;
        
        /** 匹配到的知识点ID */
        private Integer difficultyPointId;
        
        /** 匹配类型：EXACT/FUZZY/NOT_FOUND/EMPTY/NO_AVAILABLE_POINTS */
        private String matchType;
        
        /** 匹配建议（用于模糊匹配时的提示） */
        private String suggestion;
        
        /** 错误信息 */
        private String errorMessage;
        
        /** 该难度等级下可选的知识点列表 */
        private List<String> availablePoints;
    }
}
