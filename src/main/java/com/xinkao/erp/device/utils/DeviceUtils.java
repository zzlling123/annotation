package com.xinkao.erp.device.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * 设备工具类
 */
public class DeviceUtils {
    
    /**
     * 获取当前电脑的MAC地址
     */
    public static String getMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            
            if (networkInterface != null) {
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                }
            }
            
            // 如果上面的方法失败，尝试遍历所有网络接口
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (!ni.isLoopback() && ni.isUp() && !ni.isVirtual()) {
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null && mac.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        return sb.toString();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取MAC地址失败: " + e.getMessage());
        }
        
        return "UNKNOWN-MAC";
    }
    
    /**
     * 生成随机密钥
     * @param length 密钥长度
     * @return 生成的密钥
     */
    public static String generateRandomKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成32位随机密钥
     * @return 32位密钥
     */
    public static String generateDeviceKey() {
        return generateRandomKey(32);
    }
    
    /**
     * 隐藏密钥中间部分
     * @param key 原始密钥
     * @return 隐藏后的密钥
     */
    public static String maskDeviceKey(String key) {
        if (key == null || key.length() < 8) {
            return key;
        }
        
        int start = 4;
        int end = key.length() - 4;
        return key.substring(0, start) + "****" + key.substring(end);
    }
    
    /**
     * 验证MAC地址格式
     * @param macAddress MAC地址
     * @return 是否有效
     */
    public static boolean isValidMacAddress(String macAddress) {
        if (macAddress == null || macAddress.trim().isEmpty()) {
            return false;
        }
        
        // 简单的MAC地址格式验证（XX-XX-XX-XX-XX-XX）
        String pattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        return macAddress.matches(pattern);
    }
} 