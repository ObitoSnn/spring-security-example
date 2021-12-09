package com.obitosnn.config.security.authentication.cache.impl;

import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis做缓存
 *
 * @author ObitoSnn
 */
@Slf4j
public class RedisCacheProviderImpl implements CacheProvider<String, String>, InitializingBean {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String TOKEN_SEPARATOR = "_::_";

    public RedisCacheProviderImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 连接不上redis抛异常
        redisTemplate.getConnectionFactory().getConnection().ping();
    }

    /**
     * 生成key
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回key
     */
    private String generateKey(String prefix, String suffix) {
        return prefix + TOKEN_SEPARATOR + suffix;
    }

    @Override
    public String doCache(String cacheInfo) {
        final String key = generateKey(cacheInfo);
        redisTemplate.opsForValue().set(key, cacheInfo, TokenUtil.DEFAULT_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        String username = TokenUtil.getInfoByToken(cacheInfo);
        log.debug(String.format("用户'%s'的token已缓存", username));
        return key;
    }

    @Override
    public String generateKey(String cacheInfo) {
        return generateKey(TokenUtil.getInfoByToken(cacheInfo), cacheInfo);
    }

    @Override
    public String get(String key) {
        Set<String> keys = redisTemplate.keys(key);
        String result = null;
        if (ObjectUtil.isNotEmpty(keys)) {
            if (keys.size() > 1) {
                throw new RuntimeException(String.format("用户'%s'缓存的token不唯一", key));
            }
            String cachedToken = keys.toArray(new String[0])[0];
            result = cachedToken.substring(cachedToken.indexOf(TOKEN_SEPARATOR) + TOKEN_SEPARATOR.length());
        }
        if (ObjectUtil.isNotEmpty(result)) {
            log.debug(String.format("获取用户'%s'的token: %s", TokenUtil.getInfoByToken(result), result));
        } else {
            throw new RuntimeException(String.format("用户'%s'缓存的token不存在", key));
        }
        return result;
    }

    @Override
    public void update(String expect, String update) {
        String username = TokenUtil.getInfoByToken(expect);
        final String key = generateKey(username, expect);
        // 校验token是否存在
        get(key);
        redisTemplate.opsForValue().set(key, update, TokenUtil.DEFAULT_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        log.debug(String.format("用户'%s'的token已更新", username));
    }

    @Override
    public void clear(String key) {
        Set<String> keys = redisTemplate.keys(key);
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
