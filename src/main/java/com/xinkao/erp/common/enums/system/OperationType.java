package com.xinkao.erp.common.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 业务操作类型
 */
@Getter
public enum OperationType {

    /**
     * 其它
     */
    OTHER("OTHER", "其它"),

    /**
     * 新增
     */
    INSERT("INSERT", "新增"),

    /**
     * 修改
     */
    UPDATE("UPDATE", "修改"),

    /**
     * 删除
     */
    DELETE("DELETE", "删除"),

    /**
     * 授权
     */
    GRANT("GRANT", "授权"),

    /**
     * 导出
     */
    EXPORT("EXPORT", "导出"),

    /**
     * 导入
     */
    IMPORT("IMPORT", "导入"),

    /**
     * 强退
     */
    FORCE("FORCE", "强退"),

    /**
     * 清空数据
     */
    CLEAN("CLEAN", "清空数据"),

    /**
     * 上传
     */
    UPLOAD("UPLOAD", "上传"),

    /**
     * 下载
     */
    DOWNLOAD("DOWNLOAD", "下载"),
    ;

    @EnumValue
    private String code;

    @JsonValue
    private String name;

    OperationType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
