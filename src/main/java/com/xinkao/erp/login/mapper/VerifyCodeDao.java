package com.xinkao.erp.login.mapper;

/**
 * @author ZSX
 * @Description
 * @createTime 2020/12/22 14:05
 */

public class VerifyCodeDao {
    /**
     * 验证码
     */
    private String code;
    /**
     * 图片
     */
    private byte[] imgBytes;
    /**
     * 过期时间
     */
    private long expireTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }

    public void setImgBytes(byte[] imgBytes) {
        this.imgBytes = imgBytes;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
