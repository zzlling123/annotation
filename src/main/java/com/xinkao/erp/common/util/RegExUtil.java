package com.xinkao.erp.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.xinkao.erp.common.util.pwd.StringCounter;


public class RegExUtil {

    
    public static final String MOBILE_REGEXP = "^1[0-9]{10}$";
    
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*+=|':;'\\[\\].<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    
    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile(MOBILE_REGEXP);
        Matcher m = p.matcher(str);
        return m.find();
    }

    
    public static boolean isEmail(String str) {
        Pattern p = Pattern.compile(EMAIL_REGEXP);
        Matcher m = p.matcher(str);
        return m.find();
    }
    
    public static boolean isWeakUsername(String str) {
    	return StringUtils.containsIgnoreCase(str, "admin");
    }
    
    public static boolean isWeekPassword(String password) {

        if (StringUtils.isBlank(password)) {
        	return true;
        }
        if (password.length() < 8) {
        	return true;
        }
        StringCounter counter = new StringCounter().initInstance(password);
        int kinds = counter.getKinds();

        if(kinds <= 3) {
        	return true;
        }
        return false;
    }
}
