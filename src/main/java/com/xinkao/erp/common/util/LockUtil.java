package com.xinkao.erp.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class LockUtil {

    private static final Long DEFAULT_LOCK_TIME = 3600L;

    private static final String LOCK_VALUE = "1";

    @Resource
    private RedisUtil redisUtil;

    public Boolean lock(LockParam param) {
        try {
            if (param == null || StringUtils.isBlank(param.getLockKey())) {
                log.warn("param error");
                return Boolean.FALSE;
            }
            long lockTime;
            if (param.getLockTime() == null) {
                lockTime = DEFAULT_LOCK_TIME;
            } else {
                lockTime = param.getLockTime();
            }
            return redisUtil.setIfAbsent(param.getLockKey(), LOCK_VALUE, lockTime);
        } catch (Exception e) {
            log.warn("lock occur exception,key;{},msg:[{}]", param.getLockKey(), e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    public Boolean unlock(String key) {
        try {

            if (StringUtils.isBlank(key)) {
                log.warn("param error");
                return Boolean.FALSE;
            }
            return redisUtil.delRetry(key);
        } catch (Exception e) {
            log.warn("unlock occur exception,key;{},msg:[{}]", key, e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    public Boolean tryLock(TryLockParam param) {
        return null;
    }

    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Data
    public static class LockParam {
        private String lockKey;
        private Long lockTime;
    }

    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Data
    public static class TryLockParam {
        private String lockKey;
        private Long lockTime;
        private Long tryTime;
    }

}
