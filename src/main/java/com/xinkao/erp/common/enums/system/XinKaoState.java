package com.xinkao.erp.common.enums.system;


public enum XinKaoState {

    OK("ok", "成功"),
    FAIL("fail", "失败"),
    OTHER("other", ""),
    UNAUTHORIZED("unauthorized", "未认证"),
    DENIED("denied", "权限不足"),
    EXPIRED("expired", "登陆过期"),
    ;
    private final String value;
    private final String reasonPhrase;

    private XinKaoState(String value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
