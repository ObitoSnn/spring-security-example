package com.obitosnn.config.security.authentication;

import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登出回调
 *
 * @author ObitoSnn
 */
@Slf4j
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    private final CacheProvider<String> cacheProvider;

    public LogoutHandler(CacheProvider<String> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (ObjectUtil.isEmpty(authentication)) {
            // 重复登出 authentication 为null
            return;
        }

        log.debug("正在执行登出操作, {}", authentication);

        cacheProvider.clear(((User) authentication.getPrincipal()).getUsername());
    }
}
