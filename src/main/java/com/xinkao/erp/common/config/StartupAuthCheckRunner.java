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
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(ApplicationArguments args) {
        String mainServerUrl = sysConfigService.getConfigByKey("device.authentication.server");
        String ip = DeviceUtils.extractIpFromUrl(mainServerUrl);
        String macAddress = DeviceUtils.getMacAddress();
        String ipAddress = DeviceUtils.getPublicIpAddress();
        if (!ip.equals(ipAddress)){
            String url = "http://114.67.238.43:10202/device/restartStatus?macAddress="+macAddress;
            if (mainServerUrl == null || mainServerUrl.isEmpty()) {
            }else{
                url = mainServerUrl + "/device/restartStatus?macAddress="+macAddress;
            }
            try {
                restTemplate.getForObject(url, String.class);
            } catch (Exception e) {
            }
        }
    }
}