package com.obitosnn.config.security;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.LoginAuthenticationSuccessHandler;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.ResponseOutputUtil;
import com.obitosnn.util.TokenUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link SecurityContextRepository}的实现类
 * 基于内存存储{@code SecurityContext}
 *
 * @author ObitoSnn
 */
@Slf4j
public class InMemorySecurityContextRepository implements SecurityContextRepository {
    private static final TimedCache<String, SecurityContext> TIMED_CACHE = CacheUtil.newTimedCache(TokenUtil.DEFAULT_EXPIRE_TIME);
    private final static String TEMP_KEY = "TEMP_KEY";
    public static final String CONTINUE_CACHE_SECURITY_CONTEXT_KEY = "CONTINUE_CACHE_SECURITY_CONTEXT_KEY";
    public static final String UPDATE_SECURITY_CONTEXT_KEY = "UPDATE_SECURITY_CONTEXT_KEY";
    public static final String REMOVE_SECURITY_CONTEXT_KEY = "REMOVE_SECURITY_CONTEXT_KEY";
    private final CacheProvider<String, String> cacheProvider;

    static {
        TIMED_CACHE.put(TEMP_KEY, SecurityContextHolder.createEmptyContext());
        TIMED_CACHE.schedulePrune(60L * 1000L + TokenUtil.DEFAULT_EXPIRE_TIME);
    }

    public InMemorySecurityContextRepository(CacheProvider<String, String> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        String token = request.getHeader(LoginAuthenticationSuccessHandler.X_ACCESS_TOKEN);
        SecurityContext securityContext = null;

        final AntPathRequestMatcher loginRequestMatcher = new AntPathRequestMatcher("/login", HttpMethod.POST.name());
        if (loginRequestMatcher.matches(request)) {
            request.setAttribute(CONTINUE_CACHE_SECURITY_CONTEXT_KEY, true);
            return SecurityContextHolder.createEmptyContext();
        }

        final String key = cacheProvider.generateKey(token);
        String cachedToken = null;
        try {
            cachedToken = cacheProvider.get(key);
        } catch (Exception e) {
            // do nothing
        }
        final AntPathRequestMatcher logoutRequestMatcher = new AntPathRequestMatcher("/logout");
        if (logoutRequestMatcher.matches(request)) {
            boolean validate = ObjectUtil.isNotEmpty(cachedToken) && token.equals(cachedToken);
            if (validate) {
                request.setAttribute(REMOVE_SECURITY_CONTEXT_KEY, key);
            } else {
                // 用户已登录，但携带的token不正确，携带的token可以获取username但并不是缓存的那个token
                HttpServletResponse response = requestResponseHolder.getResponse();
                ResponseOutputUtil.output(response, Result.error("登出失败，请携带正确的token进行登出操作"));
            }
            return validate ? TIMED_CACHE.get(key) : TIMED_CACHE.get(TEMP_KEY);
        }

        // 认为已经携带token
        securityContext = TIMED_CACHE.get(key);
        if (ObjectUtil.isNotEmpty(securityContext)) {
            request.setAttribute(UPDATE_SECURITY_CONTEXT_KEY, true);
        }

        return ObjectUtil.isEmpty(securityContext) ? TIMED_CACHE.get(TEMP_KEY) : securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = context.getAuthentication();
            if (ObjectUtil.isEmpty(auth)) {
                Object removeKey = request.getAttribute(REMOVE_SECURITY_CONTEXT_KEY);
                if (ObjectUtil.isNotEmpty(removeKey)) {
                    removeKey(removeKey.toString());
                }
                return;
            }

            final String key = auth.getDetails().toString();
            String username = TokenUtil.getInfoByToken(cacheProvider.get(key));

            Object continueCacheKey = request.getAttribute(CONTINUE_CACHE_SECURITY_CONTEXT_KEY);
            if (ObjectUtil.isNotEmpty(continueCacheKey)) {
                boolean continueCache = (boolean) continueCacheKey;
                if (continueCache) {
                    TIMED_CACHE.put(key, context);
                    log.debug(String.format("用户'%s'的SecurityContext已缓存, SecurityContext: %s", username, context));
                }
            }

            Object updateSecurityContextKey = request.getAttribute(UPDATE_SECURITY_CONTEXT_KEY);
            if (ObjectUtil.isNotEmpty(updateSecurityContextKey)) {
                boolean updateContext = (boolean) updateSecurityContextKey;
                if (updateContext) {
                    TIMED_CACHE.put(key, context);
                    log.debug(String.format("用户'%s'的SecurityContext已更新, SecurityContext: %s", username, context));
                }
            }
        } finally {
            request.removeAttribute(CONTINUE_CACHE_SECURITY_CONTEXT_KEY);
            request.removeAttribute(REMOVE_SECURITY_CONTEXT_KEY);
            request.removeAttribute(UPDATE_SECURITY_CONTEXT_KEY);
        }
    }

    private void removeKey(String cachedKey) {
        for (String key : TIMED_CACHE.keySet()) {
            if (key.equals(cachedKey)) {
                TIMED_CACHE.remove(key);
                String username = TokenUtil.getInfoByToken(cacheProvider.get(cachedKey));
                log.debug(String.format("用户'%s'的SecurityContext已删除", username));
                return;
            }
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String token = request.getHeader(LoginAuthenticationSuccessHandler.X_ACCESS_TOKEN);
        return ObjectUtil.isNotEmpty(cacheProvider.get(cacheProvider.generateKey(token)));
    }
}
