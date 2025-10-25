package com.xinkao.erp.common.constant;

import java.io.File;

public class XinKaoConstant {
	public static final String PROJECT_PREFIX="exptmanage";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String FILE_SEPARATOR = File.separator;

    public static final String URL_SEPARATOR = "/";

    public static final String FILE_PROTOCOL = "file:///";

    public static final String ACCESS_TOKEN = "Authorization";
    public static final String LOGIN_USER_KEY = "xk_login_user_key";

    public static final Integer CAPTCHA_EXPIRATION = 2;
    public static final Integer WX_CODE_EXPIRATION = 5;

    public static final String ACCOUNT_SPLIT = "_$:$:$"+PROJECT_PREFIX+"$:$:$_";
    public static final String LOGIN_FAIL = "fail";

    public static final String LOGIN_SUCCESS = "success";

    public static final String LOGOUT = "LOGOUT";

    public static final String LOGIN_FAIL_KEY = "xk:"+PROJECT_PREFIX+":login:failed:attempts";

    public static final String LOGIN_TOKEN_KEY = "xk:"+PROJECT_PREFIX+":login:tokens:";

    public static final String CAPTCHA_CODE_KEY = "xk:"+PROJECT_PREFIX+":captcha:";
    public static final String WEIXIN_CODE_KEY = "xk:"+PROJECT_PREFIX+":wx:";
    public static final String SYS_CONFIG_KEY = "xk:"+PROJECT_PREFIX+":config:";
    public static final String SYS_DICT_KEY = "xk:"+PROJECT_PREFIX+":dict:";
    public static final String REPEAT_SUBMIT_KEY = "xk:"+PROJECT_PREFIX+":repeatSubmit:";
    public static final int REDIS_COMMON_DAYS = 5;
    public static final String REDIS_COMMON_CACHE = "xk:expt:";
    public static final String ROLL_MAKING = "xk:bz:rollMaking:";

}
