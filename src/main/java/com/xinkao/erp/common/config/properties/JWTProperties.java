package com.xinkao.erp.common.config.properties;

import lombok.Data;

/**
 * JWT配置
 **/
@Data
public class JWTProperties {

    /**
     * jwt有效时长, 默认30分钟，单位毫秒
     */
    private int expireTime = 30;

    /**
     * jwt私钥
     */
    private String rsaPrivateKey;

    /**
     * jwt公钥
     */
    private String rsaPublicKey;

    /**
     * 默认签发人
     */
    private String issuer = "XINKAO";
}
