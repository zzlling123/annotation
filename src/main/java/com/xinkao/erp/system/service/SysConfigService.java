package com.xinkao.erp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.entity.SysConfig;
import com.xinkao.erp.system.model.param.SysConfigParam;
import com.xinkao.erp.system.model.query.SysConfigQuery;

/**
 * <p>
 * 系统配置-全部系统公用 服务类
 * </p>
 *
 * @author hanhys
 * @since 2022-05-30 15:51:19
 */
public interface SysConfigService extends BaseService<SysConfig> {

    /**
     * 分页查询参数配置
     * @param pageable
     * @param sysConfigQuery
     * @return
     */
    Page<SysConfig> pageBy(Pageable pageable, SysConfigQuery sysConfigQuery);
    /**
     * 校验参数键名是否唯一
     *
     * @param configId 参数ID
     * @param configKey 参数键值
     * @return 结果
     */
    boolean checkConfigKeyUnique(String configId, String configKey);

    /**
     * 获取参数配置列表
     * @return
     */
    List<SysConfig> getConfigList(SysConfigQuery sysConfigQuery);

    /**
     * 根据ID后去参数配置
     * @param configId
     * @return
     */
    SysConfig getConfigById(String configId);

    /**
     * 根据参数键值获取参数配置
     * @param configKey
     * @return
     */
    String getConfigByKey(String configKey);

    /**
     * 获取图片验证码功能是否开启
     * @return
     */
    boolean getCaptchaPicOnOff();
    /**
     * 验证码大小写不敏感开关
     * @return
     */
    boolean getCaptchaLowercaseOnOff();
    /**
     * 加载参数缓存数据
     */
   void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

    /**
     * 根据id删除参数配置
     * @param ids
     * @return
     */
    boolean delete(String ids);

    /**
     * 新增参数配置
     * @param sysConfigParam
     * @return
     */
    boolean insert(SysConfigParam sysConfigParam);

    /**
     * 修改参数配置
     * @param sysConfigParam
     * @return
     */
    boolean update(SysConfigParam sysConfigParam);
}
