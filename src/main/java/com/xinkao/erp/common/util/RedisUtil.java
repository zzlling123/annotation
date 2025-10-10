package com.xinkao.erp.common.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.xinkao.erp.common.exception.BusinessException;

@Component
public class RedisUtil {

    @Resource
    public RedisTemplate redisTemplate;

    public <T> void set(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    
    public <T> void set(final String key, final T value, final Integer timeout,
        final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    public <T> T get(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    public <T> long setList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }
    public <T> long setList(final String key, final List<T> dataList,final long timeout, final TimeUnit unit) {
    	Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
    	redisTemplate.expire(key, timeout, unit);
    	return count == null ? 0 : count;
    }

    public <T> List<T> getList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public <T> void setMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    public <T> void setMap(final String key, final Map<String, T> dataMap, final long timeout, final TimeUnit unit) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
            redisTemplate.expire(key, timeout, unit);
        }
    }

    public <T> Map<String, T> getMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public <T> void setMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    public <T> T getMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    public <T> List<T> getMultiMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new BusinessException("递增因子必须大于");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new BusinessException("递减因子必须大于");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    public Boolean setIfAbsent(String key, String value, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
    }

    public Boolean delRetry(String key) {
        for (int i = 0; i < 5; i++) {
            boolean b = deleteObject(key);
            if (b) {
                return true;
            }
        }
        return false;
    }

    public <T> T getInfoByToken() {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(HttpContextUtils.getHttpServletRequest().getHeader("Authorization"));
    }

}
