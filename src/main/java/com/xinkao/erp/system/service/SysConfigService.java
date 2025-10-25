package com.xinkao.erp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.entity.SysConfig;
import com.xinkao.erp.system.model.param.SysConfigParam;
import com.xinkao.erp.system.model.query.SysConfigQuery;


public interface SysConfigService extends BaseService<SysConfig> {

    
    Page<SysConfig> pageBy(Pageable pageable, SysConfigQuery sysConfigQuery);
    
    boolean checkConfigKeyUnique(String configId, String configKey);

    
    List<SysConfig> getConfigList(SysConfigQuery sysConfigQuery);

    
    SysConfig getConfigById(String configId);

    
    String getConfigByKey(String configKey);

    
    boolean getCaptchaPicOnOff();
    
    boolean getCaptchaLowercaseOnOff();
    
   void loadingConfigCache();

    
    void clearConfigCache();

    
    void resetConfigCache();

    
    boolean delete(String ids);

    
    boolean insert(SysConfigParam sysConfigParam);

    
    boolean update(SysConfigParam sysConfigParam);
}
