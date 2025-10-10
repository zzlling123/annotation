package com.xinkao.erp.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.device.entity.Device;
import com.xinkao.erp.device.mapper.DeviceMapper;
import com.xinkao.erp.device.param.DeviceParam;
import com.xinkao.erp.device.param.KeyValidationParam;
import com.xinkao.erp.device.query.DeviceQuery;
import com.xinkao.erp.device.service.DeviceService;
import com.xinkao.erp.device.utils.DeviceUtils;
import com.xinkao.erp.device.vo.DeviceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {
    
    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private RedisUtil redisUtil;
    
    @Override
    public IPage<DeviceVO> getDeviceList(DeviceQuery query) {
        Page<Device> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getDeviceName())) {
            wrapper.like(Device::getDeviceName, query.getDeviceName());
        }
        
        if (StringUtils.hasText(query.getMacAddress())) {
            wrapper.like(Device::getMacAddress, query.getMacAddress());
        }
        
        if (query.getStatus() != null) {
            wrapper.eq(Device::getStatus, query.getStatus());
        }
        
        wrapper.orderByDesc(Device::getCreateTime);
        
        IPage<Device> devicePage = deviceMapper.selectPage(page, wrapper);
        
        IPage<DeviceVO> voPage = new Page<>(devicePage.getCurrent(), devicePage.getSize(), devicePage.getTotal());
        List<DeviceVO> voList = devicePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public DeviceVO getDeviceById(Long id) {
        Device device = deviceMapper.selectById(id);
        return device != null ? convertToVO(device) : null;
    }

    @Override
    public boolean addDeviceAuthRequest(DeviceParam param){
        Device device = new Device();
        BeanUtils.copyProperties(param, device);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        Device oldDevice = deviceMapper.selectOne(wrapper.eq(Device::getMacAddress, device.getMacAddress()));
        if (oldDevice != null && oldDevice.getStatus() == 2) {
            device.setStatus(0);
            device.setDeviceName(param.getDeviceName());
            device.setDescription(param.getDescription());
            device.setKeyValidHours(param.getKeyValidHours());
            device.setDeviceKey(DeviceUtils.generateDeviceKey());
            device.setKeyGenerateTime(LocalDateTime.now());
            device.setKeyExpireTime(LocalDateTime.now().plusHours(param.getKeyValidHours()));
            device.setUpdateTime(LocalDateTime.now());
            device.setRestartStatus(0);
            return deviceMapper.updateById(device) > 0;
        }else {
            device.setDeviceName(param.getDeviceName());
            device.setDescription(param.getDescription());
            device.setMacAddress(param.getMacAddress());
            device.setKeyValidHours(param.getKeyValidHours());
            device.setDeviceKey(DeviceUtils.generateDeviceKey());
            device.setKeyGenerateTime(LocalDateTime.now());
            device.setKeyExpireTime(LocalDateTime.now().plusHours(param.getKeyValidHours()));

            if (!DeviceUtils.isValidMacAddress(device.getMacAddress())){
                return false;
            }
            device.setStatus(0);
            device.setUpdateTime(LocalDateTime.now());
            device.setCreateTime(LocalDateTime.now());
            device.setUpdateTime(LocalDateTime.now());
            device.setRestartStatus(0);
            return deviceMapper.insert(device) > 0;
        }
    }
    
    @Override
    public boolean addDevice(DeviceParam param) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Device device = new Device();
        BeanUtils.copyProperties(param, device);

        device.setDeviceName(param.getDeviceName());
        device.setDescription(param.getDescription());
        device.setMacAddress(param.getMacAddress());
        device.setKeyValidHours(param.getKeyValidHours());
        device.setDeviceKey(DeviceUtils.generateDeviceKey());
        device.setKeyGenerateTime(LocalDateTime.now());
        device.setKeyExpireTime(LocalDateTime.now().plusHours(param.getKeyValidHours()));

        if (!DeviceUtils.isValidMacAddress(device.getMacAddress())){
            return false;
        }
        if (param.getStatus() == null){
            device.setStatus(0);
        }else if (param.getStatus() == 1){
            device.setStatus(1);
        }else {
            device.setStatus(0);
        }
        device.setUpdateTime(LocalDateTime.now());
        if (loginUser!=null){
            device.setUpdateBy(loginUser.getUser().getRealName());
        }
        device.setCreateTime(LocalDateTime.now());
        device.setUpdateTime(LocalDateTime.now());
        
        return deviceMapper.insert(device) > 0;
    }
    
    @Override
    public boolean updateDevice(DeviceParam param) {
        LoginUser loginUser = redisUtil.getInfoByToken();
        Device device = deviceMapper.selectById(param.getId());
        if (device == null) {
            return false;
        }
        BeanUtils.copyProperties(param, device);
        device.setUpdateTime(LocalDateTime.now());
        device.setKeyExpireTime(LocalDateTime.now().plusHours(param.getKeyValidHours()));
        if (loginUser!=null){
            device.setUpdateBy(loginUser.getUser().getRealName());
        }
        return deviceMapper.updateById(device) > 0;
    }

    public boolean updateDevice1(DeviceParam param) {
        Device device = deviceMapper.selectById(param.getId());
        if (device == null) {
            return false;
        }
        BeanUtils.copyProperties(param, device);
        device.setUpdateTime(LocalDateTime.now());
        return deviceMapper.updateById(device) > 0;
    }

    @Override
    public boolean deleteDevice(Long id) {
        return deviceMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean regenerateDeviceKey(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            return false;
        }
        
        device.setDeviceKey(DeviceUtils.generateDeviceKey());
        device.setKeyGenerateTime(LocalDateTime.now());
        device.setKeyExpireTime(LocalDateTime.now().plusHours(device.getKeyValidHours()));
        device.setUpdateTime(LocalDateTime.now());
        
        return deviceMapper.updateById(device) > 0;
    }
    
    @Override
    public boolean validateDeviceKey(KeyValidationParam param) {
        return isDeviceKeyValid(param.getMacAddress(), param.getDeviceKey());
    }
    
    @Override
    public Device getDeviceByMacAddress(String macAddress) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getMacAddress, macAddress);
        wrapper.eq(Device ::getStatus, 1);
        wrapper.ge(Device::getKeyExpireTime, LocalDateTime.now());
        return deviceMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean isDeviceKeyValid(String macAddress, String deviceKey) {
        Device device = getDeviceByMacAddress(macAddress);
        if (device == null) {
            return false;
        }
        System.out.println("deviceKey:" + deviceKey);
        System.out.println("device____key:" + device.getDeviceKey());
        if (!device.getDeviceKey().equals(deviceKey)) {
            System.out.println("密钥不匹配");
            return false;
        }
        
        if (device.getStatus() != 1) {
            return false;
        }
        
        if (device.getKeyExpireTime() != null && LocalDateTime.now().isAfter(device.getKeyExpireTime())) {
            return false;
        }
        
        device.setLastActivateTime(LocalDateTime.now());
        device.setRestartStatus(0);
        deviceMapper.updateById(device);
        
        return true;
    }
    
    @Override
    public boolean activateDevice(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            return false;
        }
        
        device.setStatus(1);
        device.setUpdateTime(LocalDateTime.now());
        
        return deviceMapper.updateById(device) > 0;
    }
    
    @Override
    public boolean disableDevice(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            return false;
        }
        
        device.setStatus(2);
        device.setUpdateTime(LocalDateTime.now());
        
        return deviceMapper.updateById(device) > 0;
    }
    
    private DeviceVO convertToVO(Device device) {
        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        
        
        switch (device.getStatus()) {
            case 0:
                vo.setStatusDesc("未激活");
                break;
            case 1:
                vo.setStatusDesc("已激活");
                break;
            case 2:
                vo.setStatusDesc("已禁用");
                break;
            default:
                vo.setStatusDesc("未知");
        }
        
        vo.setKeyValid(device.getKeyExpireTime() == null || 
                       LocalDateTime.now().isBefore(device.getKeyExpireTime()));
        
        return vo;
    }
} 