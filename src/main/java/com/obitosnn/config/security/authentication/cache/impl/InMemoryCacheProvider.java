package com.obitosnn.config.security.authentication.cache.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * 基于内存的{@link com.obitosnn.config.security.authentication.cache.CacheProvider}实现类
 *
 * @author ObitoSnn
 */
@Slf4j
public class InMemoryCacheProvider implements CacheProvider<String, String>, InitializingBean {
    /**
     * 缓存容器
     */
    private final TimedCache<String, String> timedCache = CacheUtil.newTimedCache(TokenUtil.DEFAULT_EXPIRE_TIME);
    private static final String TOKEN_SEPARATOR = "_::_";

    @Override
    public String doCache(String cacheInfo) {
        final String key = generateKey(cacheInfo);
        String username = TokenUtil.getInfoByToken(cacheInfo);
        timedCache.put(key, cacheInfo);
        log.debug(String.format("用户'%s'的token已缓存", username));
        return key;
    }

    @Override
    public String generateKey(String cacheInfo) {
        return TokenUtil.getInfoByToken(cacheInfo) + TOKEN_SEPARATOR + cacheInfo;
    }

    @Override
    public String get(String key) {
        if (ObjectUtil.isEmpty(key)) {
            throw new RuntimeException("key不能为空");
        }
        String result = timedCache.get(key);
        String token = key.substring(key.indexOf(TOKEN_SEPARATOR) + TOKEN_SEPARATOR.length());
        String username = TokenUtil.getInfoByToken(token);
        if (ObjectUtil.isNotEmpty(result)) {
            log.debug(String.format("获取用户'%s'的token", username));
            result = token;
        } else {
            throw new RuntimeException(String.format("用户'%s'的缓存的token已被删除", username));
        }
        return result;
    }

    @Override
    public void update(String expect, String update) {
        final String key = generateKey(expect);
        String username = TokenUtil.getInfoByToken(expect);
        // 校验token是否存在
        get(key);
        timedCache.put(key, update);
        log.debug(String.format("用户'%s'的token已更新", username));
    }

    @Override
    public void clear(String key) {
        timedCache.remove(key);
        log.debug(String.format("用户'%s'的token已清除", key));
    }

    @Override
    public boolean support(String cacheInfo) {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //开启定时清除缓存任务
        timedCache.schedulePrune(60L * 1000L + TokenUtil.DEFAULT_EXPIRE_TIME);
    }
}
