package com.xinkao.erp.common.enums.busi;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;


public class LoginEnum {

	@Getter
	public enum LoginMethodEnum {
		
	    ACCOUNT_PASSWORD(0, "账号密码登录"),
	    
	    MINAPP(1, "小程序登录"),
	    
	    OTHER(-1, "不确定"),

	    ;
	    @EnumValue
	    private Integer code;
	    @JsonValue
	    private String name;

	    LoginMethodEnum(Integer code, String name) {
	        this.code = code;
	        this.name = name;
	    }
	    
	    public static LoginMethodEnum getLoginMethodEnumByCode(Integer code) {
    		if(code == null) {
    			return OTHER;
    		}
    		for (LoginMethodEnum mt : LoginMethodEnum.values()) {
				if(mt.getCode() == code) {
					return mt;
				}
			}
    		return OTHER;
    	}
	}
}
