package com.xinkao.erp.device.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.device.param.DeviceParam;
import com.xinkao.erp.device.param.KeyValidationParam;
import com.xinkao.erp.device.query.DeviceQuery;
import com.xinkao.erp.device.service.DeviceService;
import com.xinkao.erp.device.utils.DeviceUtils;
import com.xinkao.erp.device.vo.DeviceVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理Controller
 */
@RestController
@RequestMapping("/device")
public class DeviceController extends BaseController {
    
    @Resource
    private DeviceService deviceService;
    
    /**
     * 分页查询设备列表
     */
    @GetMapping("/list")
    @ApiOperation("分页查询设备列表")
    @PrimaryDataSource
    public BaseResponse<IPage<DeviceVO>> getDeviceList(DeviceQuery query) {
        IPage<DeviceVO> page = deviceService.getDeviceList(query);
        return BaseResponse.ok(page);
    }
    
    /**
     * 根据ID获取设备详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取设备详情")
    @PrimaryDataSource
    public BaseResponse<DeviceVO> getDeviceById(@PathVariable Long id) {
        DeviceVO device = deviceService.getDeviceById(id);
        return device != null ? BaseResponse.ok(device) : BaseResponse.fail("设备不存在");
    }
    
    /**
     * 新增设备
     */
    @PostMapping("/add")
    @ApiOperation("新增设备")
   // @PrimaryDataSource
    public BaseResponse<?> addDevice(@Valid @RequestBody DeviceParam param) {
        boolean success = deviceService.addDevice(param);
        return success ? BaseResponse.ok("设备添加成功") : BaseResponse.fail("设备添加失败");
    }
    
    /**
     * 更新设备
     */
    @PutMapping("/update")
    @ApiOperation("更新设备")
    @PrimaryDataSource
    public BaseResponse<?> updateDevice(@Valid @RequestBody DeviceParam param) {
        if (param.getId() == null) {
            return BaseResponse.fail("设备ID不能为空");
        }
        boolean success = deviceService.updateDevice(param);
        return success ? BaseResponse.ok("设备更新成功") : BaseResponse.fail("设备更新失败");
    }
    
    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    @Log(content = "删除设备", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> deleteDevice(@PathVariable Long id) {
        boolean success = deviceService.deleteDevice(id);
        return success ? BaseResponse.ok("设备删除成功") : BaseResponse.fail("设备删除失败");
    }
    
    /**
     * 生成新的设备密钥
     */
    @PostMapping("/{id}/regenerate-key")
    @ApiOperation("生成新的设备密钥")
    @PrimaryDataSource
    public BaseResponse<?> regenerateDeviceKey(@PathVariable Long id) {
        boolean success = deviceService.regenerateDeviceKey(id);
        return success ? BaseResponse.ok("密钥重新生成成功") : BaseResponse.fail("密钥重新生成失败");
    }
    
    /**
     * 验证设备密钥
     */
    @PostMapping("/validate-key")
    @ApiOperation("验证设备密钥")
    public BaseResponse<?> validateDeviceKey(@Valid @RequestBody KeyValidationParam param) {
        boolean valid = deviceService.validateDeviceKey(param);
        return valid ? BaseResponse.ok("密钥验证成功") : BaseResponse.fail("密钥验证失败");
    }
    
    /**
     * 激活设备
     */
    @PostMapping("/{id}/activate")
    @ApiOperation("激活设备")
    @PrimaryDataSource
    @Log(content = "激活设备", operationType = OperationType.UPDATE)
    public BaseResponse<?> activateDevice(@PathVariable Long id) {
        boolean success = deviceService.activateDevice(id);
        return success ? BaseResponse.ok("设备激活成功") : BaseResponse.fail("设备激活失败");
    }
    
    /**
     * 禁用设备
     */
    @PostMapping("/{id}/disable")
    @Log(content = "禁用设备", operationType = OperationType.UPDATE)
    @ApiOperation("禁用设备")
    public BaseResponse<?> disableDevice(@PathVariable Long id) {
        boolean success = deviceService.disableDevice(id);
        return success ? BaseResponse.ok("设备禁用成功") : BaseResponse.fail("设备禁用失败");
    }
    
    /**
     * 获取当前设备的MAC地址
     */
    @GetMapping("/current-mac")
    @ApiOperation("获取当前设备的MAC地址")
    public BaseResponse<Map<String, Object>> getCurrentMacAddress() {
        String macAddress = DeviceUtils.getMacAddress();
        Map<String, Object> result = new HashMap<>();
        result.put("macAddress", macAddress);
        result.put("isValid", DeviceUtils.isValidMacAddress(macAddress));
        return BaseResponse.ok(result);
    }
    
    /**
     * 系统启动密钥验证
     */
    @PostMapping("/system-startup/validate")
    @ApiOperation("系统启动密钥验证")
    public BaseResponse<?> validateSystemStartup(@Valid @RequestBody KeyValidationParam param) {
        boolean valid = deviceService.isDeviceKeyValid(param.getMacAddress(), param.getDeviceKey());
        if (valid) {
            return BaseResponse.ok("系统启动验证成功");
        } else {
            return BaseResponse.fail("系统启动验证失败，请检查MAC地址和密钥");
        }
    }
} 