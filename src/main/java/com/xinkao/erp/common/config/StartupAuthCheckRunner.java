package com.xinkao.erp.common.config;

import com.xinkao.erp.device.utils.DeviceUtils;
import com.xinkao.erp.system.service.SysConfigService;
import io.lettuce.core.ScriptOutputType;
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
        //截取mainServerUrl（http://114.67.238.43:10202/annotation）里面的ip
        String ip = DeviceUtils.extractIpFromUrl(mainServerUrl);
        //获取设备的mac地址
        String macAddress = DeviceUtils.getMacAddress();
        //获取当前设备的ip地址
        String ipAddress = DeviceUtils.getPublicIpAddress();
        System.out.println("重启后请求内容开始");
        System.out.println("当前设备是否与主服务器一致：" + ip.equals(ipAddress));
        if (!ip.equals(ipAddress)){
            String url = "http://114.67.238.43:10202/device/restartStatus?macAddress="+macAddress;

            if (mainServerUrl == null || mainServerUrl.isEmpty()) {
            }else{
                // 2. 拼接主服务器接口
                url = mainServerUrl + "/device/restartStatus?macAddress="+macAddress;
            }
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
        System.out.println("重启后请求内容结束");
    }
}