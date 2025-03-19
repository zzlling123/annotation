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

/**
 * <p>
 * 系统配置-全部系统公用 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2022-05-30 15:51:19
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Resource
    private RedisUtil redisUtil;
    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }
    
    /**
     * 分页查询参数配置
     * @param pageable
     * @param sysConfigQuery
     * @return
     */
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

    /**
     * 查询参数配置信息列表
     * @param sysConfigQuery
     * @return
     */
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

    /**
     * 根据ID获取参数配置
     * @param configId
     * @return
     */
    @Override
    public SysConfig getConfigById(@NonNull String configId) {
        return getById(configId);
    }

    /**
     * 获图片取验证码开关
     * @return
     */
    @Override
    public boolean getCaptchaPicOnOff() {
        String captchaPicOnOff = getConfigByKey(SysConfigEnum.CAPTCHA_PIC_ON_OFF.getKey());
        return Convert.toBool(captchaPicOnOff, true);
    }
	/**
	 * 验证码大小写不敏感
	 */
	@Override
	public boolean getCaptchaLowercaseOnOff() {
		String captchaLowercaseOnOff = getConfigByKey(SysConfigEnum.CAPTCHA_LOWERCASE_ON_OFF.getKey());
        return Convert.toBool(captchaLowercaseOnOff, true);
	}
    /**
     * 根据键值查询参数信息
     * @param key
     */
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

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configList = getConfigList(new SysConfigQuery());
        for (SysConfig config : configList) {
            redisUtil.set(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisUtil.keys(XinKaoConstant.SYS_CONFIG_KEY + "*");
        redisUtil.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
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
        // 检查是否键值重复
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
        // 检查是否键值重复
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

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return XinKaoConstant.SYS_CONFIG_KEY + configKey;
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param configId 参数ID
     * @param configKey 参数键值
     * @return 结果
     */
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
