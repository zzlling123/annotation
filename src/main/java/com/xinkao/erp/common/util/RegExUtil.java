package com.xinkao.erp.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.xinkao.erp.common.util.pwd.StringCounter;

/**
 * 正则匹配工具类
 **/
public class RegExUtil {

    /**
     * 手机号正则
     */
    public static final String MOBILE_REGEXP = "^1[0-9]{10}$";
    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 判断是否含有特殊字符
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*+=|':;'\\[\\].<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断是否为11位手机号
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile(MOBILE_REGEXP);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断是否为邮箱格式
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        Pattern p = Pattern.compile(EMAIL_REGEXP);
        Matcher m = p.matcher(str);
        return m.find();
    }
    /**
     * 判断用户名是否合规
     * @param str
     * @return
     */
    public static boolean isWeakUsername(String str) {
    	return StringUtils.containsIgnoreCase(str, "admin");
    }
    /**
     * 对密码进行校验判断，弱密码将返回true，强密码返回false
     * 密码强度检查。数字、小写字母、大写字母、特殊字符 4种任意满足3种及以上即可校验成功
     */
    public static boolean isWeekPassword(String password) {
    	// 空对象、空字符串、长度小于8 -> 验证不通过
        if (StringUtils.isBlank(password)) {
        	return true;
        }
        if (password.length() < 8) {
        	return true;
        }
        StringCounter counter = new StringCounter().initInstance(password);
        int kinds = counter.getKinds();
        // 四种字符，需要全部存在
        if(kinds <= 3) {
        	return true;
        }
        return false;
    }
}
