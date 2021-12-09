package com.obitosnn.config.security.authentication;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.ResponseOutputUtil;
import com.obitosnn.util.TokenUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录认证成功回调
 *
 * @author ObitoSnn
 */
@Slf4j
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 响应头
     */
    public static final String X_ACCESS_TOKEN = "X-Access-Token";

    private final CacheProvider<String, String> cacheProvider;

    public LoginAuthenticationSuccessHandler(CacheProvider<String, String> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;

        Object details = auth.getDetails();
        if (ObjectUtil.isNotEmpty(details)) {
            String cachedTokenKey = details.toString();
            String cachedToken = cacheProvider.get(cachedTokenKey);
            log.debug("用户{}重复登录", TokenUtil.getInfoByToken(cachedToken));
            return;
        }

        log.debug("登录认证成功, {}", authentication);
        User loginUser = (User) auth.getPrincipal();
        String token = TokenUtil.createToken(loginUser.getUsername(), TokenUtil.getSigner(loginUser.getPassword()));
        if (cacheProvider.support(token)) {
            String key = cacheProvider.doCache(token);
            auth.setDetails(key);
        }

        response.setHeader(X_ACCESS_TOKEN, token);
        Result<?> result = Result.ok(MapUtil.of("token", token));
        ResponseOutputUtil.output(response, result);
    }
}
