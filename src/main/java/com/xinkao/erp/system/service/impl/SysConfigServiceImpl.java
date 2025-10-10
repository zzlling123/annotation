package com.xinkao.erp.system.service.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.system.SysConfigEnum;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.mapper.BeanMapper;
import com.xinkao.erp.system.entity.SysConfig;
import com.xinkao.erp.system.mapper.SysConfigMapper;
import com.xinkao.erp.system.model.param.SysConfigParam;
import com.xinkao.erp.system.model.query.SysConfigQuery;
import com.xinkao.erp.system.service.SysConfigService;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;


@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Resource
    private RedisUtil redisUtil;
    
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }
    
    
    @Override
    public Page<SysConfig> pageBy(Pageable pageable, SysConfigQuery sysConfigQuery) {
        Page page = pageable.toPage();
        String configName = sysConfigQuery.getConfigName();
        String configKey = sysConfigQuery.getConfigKey();
        Integer system = sysConfigQuery.getSystem();
        Page<SysConfig> sysConfigPage =
            lambdaQuery().like(StrUtil.isNotBlank(configName), SysConfig::getConfigName, configName)
                .like(StrUtil.isNotEmpty(configKey), SysConfig::getConfigKey, configKey)
                .eq(null != system, SysConfig::getIsSystem, system)
                .orderByDesc(SysConfig::getCreateTime).page(page);
        return sysConfigPage;
    }

    
    @Override
    public List<SysConfig> getConfigList(SysConfigQuery sysConfigQuery) {
        String configName = sysConfigQuery.getConfigName();
        String configKey = sysConfigQuery.getConfigKey();
        Integer system = sysConfigQuery.getSystem();
        List<SysConfig> sysConfigList = lambdaQuery().like(StrUtil.isNotBlank(configName), SysConfig::getConfigName, configName)
            .like(StrUtil.isNotEmpty(configKey), SysConfig::getConfigKey, configKey)
            .eq(null != system, SysConfig::getIsSystem, system)
            .orderByDesc(SysConfig::getCreateTime).list();
        return sysConfigList;
    }

    
    @Override
    public SysConfig getConfigById(@NonNull String configId) {
        return getById(configId);
    }

    
    @Override
    public boolean getCaptchaPicOnOff() {
        String captchaPicOnOff = getConfigByKey(SysConfigEnum.CAPTCHA_PIC_ON_OFF.getKey());
        return Convert.toBool(captchaPicOnOff, true);
    }
	
	@Override
	public boolean getCaptchaLowercaseOnOff() {
		String captchaLowercaseOnOff = getConfigByKey(SysConfigEnum.CAPTCHA_LOWERCASE_ON_OFF.getKey());
        return Convert.toBool(captchaLowercaseOnOff, true);
	}
    
    @Override
    public String getConfigByKey(String key) {
        String configValue = redisUtil.get(getCacheKey(key));
        if (StrUtil.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig sysConfig = getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
        if (null != sysConfig) {
            redisUtil.set(getCacheKey(key), sysConfig.getConfigValue());
            return sysConfig.getConfigValue();
        }
        return StrUtil.EMPTY;
    }

    
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configList = getConfigList(new SysConfigQuery());
        for (SysConfig config : configList) {
            redisUtil.set(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisUtil.keys(XinKaoConstant.SYS_CONFIG_KEY + "*");
        redisUtil.deleteObject(keys);
    }

    
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    @Override
    public boolean delete(String ids) {
        List<String> idList = StrUtil.split(ids, ',');
        for (String configId : idList) {
            SysConfig config = getConfigById(configId);
            if (CommonEnum.GLOBAL_YN.YES.getCode() == config.getIsSystem()) {
                throw new BusinessException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            redisUtil.deleteObject(getCacheKey(config.getConfigKey()));
        }
        removeByIds(idList);
        return true;
    }

    @Override
    public boolean insert(SysConfigParam sysConfigParam) {

        if (!checkConfigKeyUnique(null, sysConfigParam.getConfigKey())) {
            throw new BusinessException("新增参数'" + sysConfigParam.getConfigKey() + "'失败，参数键名已存在");
        }
        SysConfig sysConfig = BeanMapper.map(sysConfigParam, SysConfig.class);
        boolean saveReuslt = save(sysConfig);
        if (saveReuslt) {
            redisUtil.set(getCacheKey(sysConfig.getConfigKey()), sysConfig.getConfigValue());
        }
        return saveReuslt;
    }

    @Override
    public boolean update(SysConfigParam sysConfigParam) {
        Assert.hasText(sysConfigParam.getId(), "参数配置标识不能为空");

        if (!checkConfigKeyUnique(sysConfigParam.getId(), sysConfigParam.getConfigKey())) {
            throw new BusinessException("修改参数'" + sysConfigParam.getConfigKey() + "'失败，参数键名已存在");
        }
        SysConfig sysConfig = BeanMapper.map(sysConfigParam, SysConfig.class);
        boolean updateResult = updateById(sysConfig);
        if (updateResult) {
            redisUtil.set(getCacheKey(sysConfig.getConfigKey()), sysConfig.getConfigValue());
        }
        return updateResult;
    }

    
    private String getCacheKey(String configKey) {
        return XinKaoConstant.SYS_CONFIG_KEY + configKey;
    }

    
    @Override
    public boolean checkConfigKeyUnique(String configId, String configKey) {
        configId = StrUtil.isEmpty(configId) ? "-1" : configId;
        SysConfig sysConfig = lambdaQuery().eq(SysConfig::getConfigKey, configKey)
            .ne(DataEntity::getId, configId)
            .one();
        if (null ==  sysConfig) {
            return true;
        }
        return false;
    }
}
