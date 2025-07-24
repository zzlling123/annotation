package com.xinkao.erp.common.config;

import com.xinkao.erp.device.utils.DeviceUtils;
import com.xinkao.erp.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StartupAuthCheckRunner implements ApplicationRunner {

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public void run(ApplicationArguments args) {
        String mainServerUrl = sysConfigService.getConfigByKey("device.authentication.server");
        //获取设备的mac地址
        String macAddress = DeviceUtils.getMacAddress();
        System.out.println("macAddress:"+macAddress);
        String url = "http://114.67.238.43:10202/device/restartStatus?macAddress="+macAddress;
        if (mainServerUrl == null || mainServerUrl.isEmpty()) {
        }else{
            // 2. 拼接主服务器接口
            url = mainServerUrl + "/device/restartStatus?macAddress="+macAddress;
        }
        System.out.println("url:"+url);
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);
            // 可根据需要打印日志
            System.out.println("远程授权校验请求已发送");
        } catch (Exception e) {
            // 可根据需要打印日志
            System.err.println("远程授权校验请求失败: " + e.getMessage());
        }
    }
}