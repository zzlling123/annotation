package com.xinkao.erp.common.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;


@Getter
public enum OperationType {

    
    OTHER("OTHER", "其它"),

    
    INSERT("INSERT", "新增"),

    
    UPDATE("UPDATE", "修改"),

    
    DELETE("DELETE", "删除"),

    
    GRANT("GRANT", "授权"),

    
    EXPORT("EXPORT", "导出"),

    
    IMPORT("IMPORT", "导入"),

    
    FORCE("FORCE", "强退"),

    
    CLEAN("CLEAN", "清空数据"),

    
    UPLOAD("UPLOAD", "上传"),

    
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
