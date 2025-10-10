package com.xinkao.erp.common.enums;


public class CommonEnum {
	
    public enum GLOBAL_YN {
        YES(1, "是"),
        NO(0, "否");

        private int code;
        private String name;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        GLOBAL_YN(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }
    
    public enum ManagerType {
    	ADMIN(1, "管理员"),
    	USER(0, "普通用户"),
    	OTHER(-1, "其他")

    	;
    	
    	private int code;
    	private String name;
    	
    	public int getCode() {
    		return code;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	ManagerType(int code, String name) {
    		this.code = code;
    		this.name = name;
    	}
    	
    	public static ManagerType getManagerTypeByCode(Integer code) {
    		if(code == null) {
    			return OTHER;
    		}
    		for (ManagerType mt : ManagerType.values()) {
				if(mt.getCode() == code) {
					return mt;
				}
			}
    		return OTHER;
    	}
    }

    
    public enum LOGIN_STATUS {
        SUCCESS(0, "成功"),
        FAIL(1, "失败");

        private int code;
        private String name;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        LOGIN_STATUS(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    
    public enum IS_DEL {
        YES(1, "已删除"),
        NO(0, "未删除");

        private int code;
        private String name;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        IS_DEL(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    
    public enum STATE {
        YES(1, "已启用"),
        NO(0, "未启用");

        private int code;
        private String name;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        STATE(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
