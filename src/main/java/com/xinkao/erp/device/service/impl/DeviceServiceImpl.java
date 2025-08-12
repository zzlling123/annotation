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

/**
 * 设备Service实现类
 */
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
        
        // 设备名称模糊查询
        if (StringUtils.hasText(query.getDeviceName())) {
            wrapper.like(Device::getDeviceName, query.getDeviceName());
        }
        
        // MAC地址模糊查询
        if (StringUtils.hasText(query.getMacAddress())) {
            wrapper.like(Device::getMacAddress, query.getMacAddress());
        }
        
        // 状态查询
        if (query.getStatus() != null) {
            wrapper.eq(Device::getStatus, query.getStatus());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Device::getCreateTime);
        
        IPage<Device> devicePage = deviceMapper.selectPage(page, wrapper);
        
        // 转换为VO
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
        //获取登录用户
        Device device = new Device();
        BeanUtils.copyProperties(param, device);

        // 生成设备密钥
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
        // 设置创建时间
        device.setCreateTime(LocalDateTime.now());
        device.setUpdateTime(LocalDateTime.now());
        return deviceMapper.insert(device) > 0;
    }
    
    @Override
    public boolean addDevice(DeviceParam param) {
        //获取登录用户
        LoginUser loginUser = redisUtil.getInfoByToken();
        Device device = new Device();
        BeanUtils.copyProperties(param, device);
        
        // 生成设备密钥
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
        //判断参数param有没有status的值没有，则设置程默认值1
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
        // 设置创建时间
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
        //时间没有到失效时间
        wrapper.ge(Device::getKeyExpireTime, LocalDateTime.now());
        return deviceMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean isDeviceKeyValid(String macAddress, String deviceKey) {
        Device device = getDeviceByMacAddress(macAddress);
        if (device == null) {
            return false;
        }
        
        // 检查密钥是否匹配
        if (!device.getDeviceKey().equals(deviceKey)) {
            return false;
        }
        
        // 检查设备状态
        if (device.getStatus() != 1) {
            return false;
        }
        
        // 检查密钥是否过期
        if (device.getKeyExpireTime() != null && LocalDateTime.now().isAfter(device.getKeyExpireTime())) {
            return false;
        }
        
        // 更新最后激活时间
        device.setLastActivateTime(LocalDateTime.now());
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
    
    /**
     * 转换为VO
     */
    private DeviceVO convertToVO(Device device) {
        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        
        // 隐藏密钥中间部分
        vo.setDeviceKey(DeviceUtils.maskDeviceKey(device.getDeviceKey()));
        
        // 设置状态描述
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
        
        // 检查密钥是否有效
        vo.setKeyValid(device.getKeyExpireTime() == null || 
                       LocalDateTime.now().isBefore(device.getKeyExpireTime()));
        
        return vo;
    }
} 