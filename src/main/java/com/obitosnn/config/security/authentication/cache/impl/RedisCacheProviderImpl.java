package com.obitosnn.config.security.authentication.cache.impl;

import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 使用redis做缓存
 *
 * @author ObitoSnn
 */
@Component
public class RedisCacheProviderImpl implements CacheProvider<String> {
    private final String TOKEN_SEPARATOR = "_::_";
    private final String WILDCARD = "*";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void doCache(String cacheInfo) {
        String username = TokenUtil.getInfoByToken(cacheInfo);
        String key = username + TOKEN_SEPARATOR + cacheInfo;
        redisTemplate.opsForValue().set(key, cacheInfo);
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
        return result;
    }

    @Override
    public void update(String expect, String update) {
        String username = TokenUtil.getInfoByToken(expect);
        String key = username + TOKEN_SEPARATOR + expect;
        String cachedToken = (String) redisTemplate.opsForValue().get(key);
        if (ObjectUtil.isEmpty(cachedToken)) {
            throw new RuntimeException("预期值不存在");
        }
        redisTemplate.opsForValue().set(key, update);
    }

    @Override
    public void clear(String key) {
        Set<String> keys = redisTemplate.keys(key + WILDCARD);
        if (ObjectUtil.isEmpty(key)) {
            return;
        }
        for (String s : keys) {
            if (s.contains(key)) {
                redisTemplate.delete(s);
                return;
            }
        }
    }

    @Override
    public boolean support(String cacheInfo) {
        return ObjectUtil.isNotEmpty(redisTemplate);
    }
}
