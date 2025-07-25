package com.xinkao.erp.device.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 从类似“@http://114.67.238.43:10202/annotation”这样的字符串中提取IP地址
     * @param url 输入字符串
     * @return 提取到的IP地址，未匹配到返回null
     */
    public static String extractIpFromUrl(String url) {
        if (url == null) return null;
        // 去掉@符号
        String cleanUrl = url.startsWith("@") ? url.substring(1) : url;
        // 正则匹配IP
        String regex = "([0-9]{1,3}(\\.[0-9]{1,3}){3})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cleanUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 获取当前电脑的外网IP地址
     * @return 外网IP，获取失败返回null
     */
    public static String getPublicIpAddress() {
        String[] services = {
                "http://ifconfig.me/ip",
                "https://api.ipify.org",
                "http://ip-api.com/line/?fields=query"
        };
        for (String service : services) {
            try {
                URL url = new URL(service);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String ip = in.readLine();
                    if (ip != null && ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        return ip.trim();
                    }
                }
            } catch (Exception e) {
                // 可以记录日志，继续尝试下一个服务
            }
        }
        return null;
    }

    /**
     * 获取当前电脑的IP地址
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof java.net.Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // 可根据需要打印日志
        }
        return "127.0.0.1";
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