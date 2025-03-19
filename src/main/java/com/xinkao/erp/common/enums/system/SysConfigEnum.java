package com.xinkao.erp.common.enums.system;

import lombok.Getter;
/**
 * 系统内置参数
 */
@Getter
public enum SysConfigEnum {

    INIT_PASSWORD("sys.account.initPassword", "账号初始密码"),
    LOCK_MINUTE("sys.login.failedNumAfterLockMinute", "锁定账号的时间"),
    ALLOW_FAILED_NUM("sys.login.failedNumAfterLockAccount", "登录失败多少次数后锁定账号"),
    CAPTCHA_PIC_ON_OFF("sys.login.captchaPicOnOff", "登录时图形验证码开关"),
    CAPTCHA_LOWERCASE_ON_OFF("sys.login.captchaLowercaseOnOff", "登录时验证码大小写开关"),
    
    KW_CAPTCHA_PIC_ON_OFF("kw.login.captchaPicOnOff", "考点端登录时图形验证码开关"),
    SCORE_CAPTCHA_PIC_ON_OFF("score.login.captchaPicOnOff", "识别端登录时图形验证码开关"),
    CACHE_APP_ENABLE("cache.app.enable", "应用级别缓存开关"),
    //构造
    ;

    private String key;

    private String name;

    SysConfigEnum(String key, String name) {
        this.key = key;
        this.name = name;
    }

}
