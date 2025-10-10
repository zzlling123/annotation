package com.xinkao.erp.common.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.xinkao.erp.common.util.RedisUtil;

import cn.hutool.core.util.IdcardUtil;

@Controller
public class BaseController {
	@Value("${spring.application.name}")
	protected String contextName;
	@Value("${file.save.root}")
	protected String fileSaveRoot;
	@Value("${file.template.root}")
	protected String fileTemplateRoot;
	@Value("${file.image.default}")
	protected String imageDefault;
	@Resource
	protected RedisUtil redisUtil;
	

    public String hideMobile(String mobile) {
        int length = StringUtils.length(mobile);
        if (length >= 11) {
            return StringUtils.left(mobile, 3) + "****" + StringUtils.right(mobile, 4);
        }
        int encryptCharLength = 11 - length;
        String encryptChar = "";
        for (int i = 0; i < encryptCharLength; i++) {
            encryptChar += "*";
        }
        return mobile + encryptChar;
    }
    public String hideIdCard(String idCard) {
    	return IdcardUtil.hide(idCard, 4, 15);
    }
}
