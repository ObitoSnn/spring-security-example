package com.obitosnn.config.security.authentication.cache.impl;

import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis做缓存
 *
 * @author ObitoSnn
 */
@Slf4j
@Component
public class RedisCacheProviderImpl implements CacheProvider<String> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String TOKEN_SEPARATOR = "_::_";
    private final String WILDCARD = "*";

    public RedisCacheProviderImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String doCache(String cacheInfo) {
        String username = TokenUtil.getInfoByToken(cacheInfo);
        String key = username + TOKEN_SEPARATOR + cacheInfo;
        redisTemplate.opsForValue().set(key, cacheInfo, TokenUtil.DEFAULT_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        log.debug(String.format("用户'%s'的token已缓存", username));
        return username;
    }

    @Override
    public String get(String key) {
        Set<String> keys = redisTemplate.keys(key + WILDCARD);
        String result = null;
        if (ObjectUtil.isNotEmpty(keys)) {
            if (keys.size() > 1) {
                throw new RuntimeException("token不唯一");
            }
            String cachedToken = keys.toArray(new String[0])[0];
            result = cachedToken.substring(cachedToken.indexOf(TOKEN_SEPARATOR) + TOKEN_SEPARATOR.length());
        }
        if (ObjectUtil.isNotEmpty(result)) {
            log.debug(String.format("获取用户'%s'的token: %s", key, result));
        }
        return result;
    }

    @Override
    public void update(String expect, String update) {
        String username = TokenUtil.getInfoByToken(expect);
        String key = username + TOKEN_SEPARATOR + expect;
        String cachedToken = (String) redisTemplate.opsForValue().get(key);
        if (ObjectUtil.isEmpty(cachedToken)) {
            String msg = String.format("预期值不存在,用户'%s'的token未缓存", username);
            throw new RuntimeException(msg);
        }
        redisTemplate.opsForValue().set(key, update, TokenUtil.DEFAULT_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        log.debug(String.format("用户'%s'的token已更新", username));
    }

    @Override
    public void clear(String key) {
        Set<String> keys = redisTemplate.keys(key + WILDCARD);
        if (ObjectUtil.isEmpty(keys)) {
            return;
        }
        for (String s : keys) {
            if (s.contains(key)) {
                redisTemplate.delete(s);
                log.debug(String.format("用户'%s'的token已删除", key));
                return;
            }
        }
    }

    @Override
    public boolean support(String cacheInfo) {
        return ObjectUtil.isNotEmpty(redisTemplate);
    }
}
