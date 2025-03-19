package com.xinkao.erp.common.enums;

/**
 * 通用常量
 **/
public class CommonEnum {
	/**
	 * 全局
	 */
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
    /**
     * 用户身份常量
     */
    public enum ManagerType {
    	ADMIN(1, "管理员"),
    	USER(0, "普通用户"),
    	OTHER(-1, "其他")
    	
    	//构造
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

    /**
     * 登录状态
     */
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

    /**
     * 是否已删除
     */
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

    /**
     * 是否启用
     */
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
