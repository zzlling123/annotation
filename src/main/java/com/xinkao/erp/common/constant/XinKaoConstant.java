package com.xinkao.erp.common.constant;

import java.io.File;

/**
 *	项目使用到的常量
 **/
public class XinKaoConstant {
	/**
	 * 项目前缀
	 */
	public static final String PROJECT_PREFIX="exptmanage";

    /**
     * user.home
     */
    public static final String USER_HOME = System.getProperty("user.home");

    /**
     * 文件分隔符
     */
    public static final String FILE_SEPARATOR = File.separator;

    /**
     * URL分隔符
     */
    public static final String URL_SEPARATOR = "/";

    /**
     * 文件协议
     */
    public static final String FILE_PROTOCOL = "file:///";

    /**
     * 用户登录token
     */
    public static final String ACCESS_TOKEN = "Authorization";
    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "xk_login_user_key";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;
    /**
     * 微信生成code有效期（分钟）
     */
    public static final Integer WX_CODE_EXPIRATION = 5;

    /**
     * 用户账户分隔符
     */
    public static final String ACCOUNT_SPLIT = "_$:$:$"+PROJECT_PREFIX+"$:$:$_";
    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "fail";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "success";

    /**
     * 登出
     */
    public static final String LOGOUT = "LOGOUT";

    // redisKey相关
    /**
     * 登录失败次数
     */
    public static final String LOGIN_FAIL_KEY = "xk:"+PROJECT_PREFIX+":login:failed:attempts";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "xk:"+PROJECT_PREFIX+":login:tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "xk:"+PROJECT_PREFIX+":captcha:";
    /**
     * 微信token redis key
     */
    public static final String WEIXIN_CODE_KEY = "xk:"+PROJECT_PREFIX+":wx:";
    /**
     * 参数配置 config key
     */
    public static final String SYS_CONFIG_KEY = "xk:"+PROJECT_PREFIX+":config:";
    /**
     * 字典 dict key
     */
    public static final String SYS_DICT_KEY = "xk:"+PROJECT_PREFIX+":dict:";

    /**
     * 重复提交 key
     */
    public static final String REPEAT_SUBMIT_KEY = "xk:"+PROJECT_PREFIX+":repeatSubmit:";
    /**
     * 缓存默认是5天
     */
    public static final int REDIS_COMMON_DAYS = 5;
    /**
     * 通用项目前缀
     */
    public static final String REDIS_COMMON_CACHE = "xk:expt:";

}
