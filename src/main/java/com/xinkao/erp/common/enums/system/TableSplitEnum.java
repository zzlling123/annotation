package com.xinkao.erp.common.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum TableSplitEnum {
    SYS_USER_OPT_LOG("sys_user_opt_log", "管理端用户操作日志表"),
    SYS_USER_LOGIN_LOG("sys_user_login_log", "管理端用户登录记录表"),
    KW_SITE_USER_OPT_LOG("kw_site_user_opt_log", "考点端用户操作日志表"),
    KW_SITE_USER_LOGIN_LOG("kw_site_user_login_log", "考点端用户登录记录表"),
    EXAM_STUDENT_SCORE_LOG("exam_student_score_log", "考生成绩查询日志"),
    KW_STUDENT("kw_student", "考生报名信息表"),
    KW_STUDENT_CARD("kw_student_card", "考生评分卡信息表"),
    KW_STUDENT_SCORE("kw_student_score", "考生成绩信息表"),
    KW_STUDENT_SCORE_UPLOAD_LOG("kw_student_score_upload_log", "考生成绩上送记录表"),
    ;

    @EnumValue
    private String tableName;

    @JsonValue
    private String desc;

    TableSplitEnum(String tableName, String desc) {
        this.tableName = tableName;
        this.desc = desc;
    }
}
