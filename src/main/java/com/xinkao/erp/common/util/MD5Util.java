package com.xinkao.erp.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Util {
    public static String calculateMD5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return bytesToHex(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("MD5计算失败", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
