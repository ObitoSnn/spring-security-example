package com.obitosnn.config.security.authentication.cache;

/**
 * 缓存认证信息接口
 *
 * @param <K> key
 * @param <V> 真实的缓存信息
 * @author ObitoSnn
 */
public interface CacheProvider<K, V> {
    /**
     * 缓存认证信息
     *
     * @param cacheInfo 缓存信息
     * @return 返回与缓存信息关联的key
     */
    K doCache(V cacheInfo);

    /**
     * 获取缓存信息
     *
     * @param key 与缓存信息关联的key
     * @return 返回缓存信息
     */
    V get(K key);

    /**
     * 更新缓存信息
     *
     * @param expect 预期值
     * @param update 新的缓存信息
     */
    void update(V expect, V update);

    /**
     * 清除{@code key}关联的缓存信息
     * @param key 与缓存信息关联的key
     */
    void clear(K key);

    /**
     * 是否支持缓存该信息
     *
     * @param cacheInfo 缓存信息
     * @return true支持，false不支持
     */
    default boolean support(V cacheInfo) {
        return false;
    }
}
