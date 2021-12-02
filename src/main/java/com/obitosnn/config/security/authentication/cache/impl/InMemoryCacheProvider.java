package com.obitosnn.config.security.authentication.cache.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 基于内存的{@link com.obitosnn.config.security.authentication.cache.CacheProvider}实现类
 *
 * @author ObitoSnn
 */
@Slf4j
@Component
public class InMemoryCacheProvider implements CacheProvider<String>, InitializingBean {
    /**
     * 缓存容器
     */
    private final TimedCache<String, String> timedCache = CacheUtil.newTimedCache(TokenUtil.DEFAULT_EXPIRE_TIME);
    private final String TOKEN_SEPARATOR = "_::_";

    @Override
    public String doCache(String cacheInfo) {
        String username = TokenUtil.getInfoByToken(cacheInfo);
        String key = username + TOKEN_SEPARATOR + cacheInfo;
        timedCache.put(key, cacheInfo);
        log.debug(String.format("用户'%s'的token已缓存", username));
        return username;
    }

    @Override
    public String get(String key) {
        Set<String> keys = timedCache.keySet();
        String result = null;
        for (String s : keys) {
            if (s.contains(key)) {
                result = s.substring(s.indexOf(TOKEN_SEPARATOR) + TOKEN_SEPARATOR.length());
                break;
            }
        }
        if (ObjectUtil.isNotEmpty(result)) {
            log.debug(String.format("获取用户'%s'的token", key));
        }
        return result;
    }

    @Override
    public void update(String expect, String update) {
        String username = TokenUtil.getInfoByToken(expect);
        Set<String> keys = timedCache.keySet();
        boolean hasToken = false;
        for (String key : keys) {
            if (key.contains(username)) {
                hasToken = true;
                break;
            }
        }
        if (!hasToken) {
            throw new RuntimeException(String.format("未用户'%s'的token", username));
        }
        String key = username + TOKEN_SEPARATOR + expect;
        timedCache.put(key, update);
        log.debug(String.format("用户'%s'的token已更新", username));
    }

    @Override
    public void clear(String key) {
        String realKey = null;
        for (String s : timedCache.keySet()) {
            if (s.contains(key)) {
                realKey = s;
                break;
            }
        }
        timedCache.remove(realKey);
        log.debug(String.format("用户'%s'的token已清除", key));
    }

    @Override
    public boolean support(String cacheInfo) {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //开启定时清除缓存任务
        timedCache.schedulePrune(TokenUtil.DEFAULT_EXPIRE_TIME);
    }
}
