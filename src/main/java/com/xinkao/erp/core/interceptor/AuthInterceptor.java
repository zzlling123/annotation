package com.xinkao.erp.core.interceptor;

import com.xinkao.erp.device.service.DeviceService;
import com.xinkao.erp.device.utils.DeviceUtils;
import com.xinkao.erp.system.service.SysConfigService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import com.xinkao.erp.common.model.BaseResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Resource
    private DeviceService deviceService;

    @Resource
    public RedisTemplate redisTemplate;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private SysConfigService sysConfigService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("Authorization");
//        if (token == null || token.isEmpty()) {
//            //response.setStatus(401);
//            //response.getWriter().write("未提供认证信息");
//            //return false;
//        }else {
//            ValueOperations<String, Object> operation = redisTemplate.opsForValue();
//            Object userObj = operation.get(token);
//            if (userObj == null) {
//                response.setStatus(401);
//                response.getWriter().write("无效的认证信息");
//                return false;
//            }
//            LoginUser loginUser = (LoginUser) userObj;
//            List<Role> roleList = loginUser.getRoleList();
//            //roleList有id为1的用户直接放行
//            if (roleList!=null&&roleList.stream().anyMatch(role -> role.getId() == 1)) {
//                // 超级管理员直接放行
//                return true;
//            }
//        }
        String mainServerUrl = sysConfigService.getConfigByKey("device.authentication.server");
        if (mainServerUrl == null || mainServerUrl.isEmpty()) {
            System.out.println("未配置认证服务器地址");
            response.setStatus(500);
            response.getWriter().write("未配置认证服务器地址");
            return false;
        }
        // 获取当前电脑的MAC地址
        String macAddress = getMacAddress();
        System.out.println("当前MAC地址: " + macAddress);
        //主服务器ip地址
        String ip = DeviceUtils.extractIpFromUrl(mainServerUrl);
        //获取设备的mac地址
        //获取当前设备的ip地址
        String ipAddress = DeviceUtils.getPublicIpAddress();
        System.out.println("请求拦截器内容开始");
        System.out.println("当前设备是否与主服务器一致：" + ip.equals(ipAddress));
        if (ip.equals(ipAddress)){
            System.out.println("当前设备为主服务器访问");
            return true;
        }
        // 查询当前系统中是否存在该MAC地址的设备
        //改成服务器远程访问
        BaseResponse responseBody = restTemplate.getForObject(mainServerUrl + "/device/checkAuth?macAddress=" + macAddress, BaseResponse.class);
        boolean hasDevice = responseBody != null && responseBody.getData() instanceof Boolean && (Boolean) responseBody.getData();

        if (!hasDevice) {
            System.out.println("未找到该MAC地址的设备");
            System.out.println("设备未注册，请联系管理员添加设备");
            // 没有找到该MAC地址的设备，返回错误
            response.setStatus(403);
            //修改返回前端乱码
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write("设备未注册，请联系管理员添加设备");
            return false;
        }
        //System.out.println("MAC地址 " + macAddress + " 已注册，允许访问");
        System.out.println("请求拦截器内容开始");
        return true;
    }
    
    /**
     * 获取当前电脑的MAC地址
     */
    private String getMacAddress() {
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
}
