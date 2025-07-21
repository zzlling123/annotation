package com.xinkao.erp.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinkao.erp.device.entity.Device;
import com.xinkao.erp.device.param.DeviceParam;
import com.xinkao.erp.device.param.KeyValidationParam;
import com.xinkao.erp.device.query.DeviceQuery;
import com.xinkao.erp.device.vo.DeviceVO;

/**
 * 设备Service接口
 */
public interface DeviceService {
    
    /**
     * 分页查询设备列表
     */
    IPage<DeviceVO> getDeviceList(DeviceQuery query);
    
    /**
     * 根据ID获取设备详情
     */
    DeviceVO getDeviceById(Long id);
    
    /**
     * 新增设备
     */
    boolean addDevice(DeviceParam param);
    
    /**
     * 更新设备
     */
    boolean updateDevice(DeviceParam param);
    
    /**
     * 删除设备
     */
    boolean deleteDevice(Long id);
    
    /**
     * 生成新的设备密钥
     */
    boolean regenerateDeviceKey(Long id);
    
    /**
     * 验证设备密钥
     */
    boolean validateDeviceKey(KeyValidationParam param);
    
    /**
     * 根据MAC地址获取设备
     */
    Device getDeviceByMacAddress(String macAddress);
    
    /**
     * 检查设备密钥是否有效
     */
    boolean isDeviceKeyValid(String macAddress, String deviceKey);

    /**
     * 添加设备授权请求
     */
    public boolean addDeviceAuthRequest(DeviceParam param);

    /**
     * 激活设备
     */
    boolean activateDevice(Long id);
    
    /**
     * 禁用设备
     */
    boolean disableDevice(Long id);
} 