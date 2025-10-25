package com.xinkao.erp.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinkao.erp.device.entity.Device;
import com.xinkao.erp.device.param.DeviceParam;
import com.xinkao.erp.device.param.KeyValidationParam;
import com.xinkao.erp.device.query.DeviceQuery;
import com.xinkao.erp.device.vo.DeviceVO;

public interface DeviceService {
    
    IPage<DeviceVO> getDeviceList(DeviceQuery query);
    
    DeviceVO getDeviceById(Long id);
    
    boolean addDevice(DeviceParam param);
    
    boolean updateDevice(DeviceParam param);

    boolean updateDevice1(DeviceParam param);
    
    boolean deleteDevice(Long id);
    
    boolean regenerateDeviceKey(Long id);
    
    boolean validateDeviceKey(KeyValidationParam param);
    
    Device getDeviceByMacAddress(String macAddress);
    
    boolean isDeviceKeyValid(String macAddress, String deviceKey);

    public boolean addDeviceAuthRequest(DeviceParam param);

    boolean activateDevice(Long id);
    
    boolean disableDevice(Long id);
} 