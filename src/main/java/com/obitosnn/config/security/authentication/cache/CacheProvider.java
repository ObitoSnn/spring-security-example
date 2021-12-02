package com.obitosnn.config.security.authentication.cache;

/**
 * 缓存认证信息接口
 *
 * @param <T> 真实的缓存信息
 * @author ObitoSnn
 */
public interface CacheProvider<T> {
    /**
     * 缓存认证信息
     *
     * @param cacheInfo 缓存信息
     */
    void doCache(T cacheInfo);

    /**
     * 获取缓存信息
     *
     * @param key 与缓存信息关联的key
     * @return 返回缓存信息
     */
    T get(T key);

    /**
     * 更新缓存信息
     *
     * @param expect 预期值
     * @param update 新的缓存信息
     */
    void update(T expect, T update);

    /**
     * 清除缓存信息
     * @param key 与缓存信息关联的key
     */
    void clear(T key);

    /**
     * 是否支持缓存该信息
     *
     * @param cacheInfo 缓存信息
     * @return true支持，false不支持
     */
    default boolean support(T cacheInfo) {
        return false;
    }
}
