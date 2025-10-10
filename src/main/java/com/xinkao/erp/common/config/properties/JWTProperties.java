package com.xinkao.erp.common.config.properties;

import lombok.Data;

@Data
public class JWTProperties {

    private int expireTime = 30;
    private String rsaPrivateKey;
    private String rsaPublicKey;
    private String issuer = "XINKAO";
}
