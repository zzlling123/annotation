package com.xinkao.erp.login.vo;

import lombok.Data;

@Data
/**生成验证码返回实体-vo
 * @author hys_thanks
 */
public class GenCaptchaResultVo {

	/**验证码**/
	private String code;
	
	/**图片验证码base64编码**/
	private String codePic;
}
