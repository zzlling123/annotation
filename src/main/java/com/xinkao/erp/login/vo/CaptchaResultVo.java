package com.xinkao.erp.login.vo;

import lombok.Data;

@Data
/**
 * 验证码返回实体-vo
 * @author hys_thanks
 */
public class CaptchaResultVo {

	/**验证码校验开关:off-关 on-开**/
	private String captchaOnOff;
	
	/**验证码uuid:登录时与验证码一起返回**/
	private String captchaId;
	
	/**如果是图片验证码,base64编码,手机验证码为空串**/
	private String img;
}
