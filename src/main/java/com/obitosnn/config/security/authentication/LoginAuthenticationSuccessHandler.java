package com.obitosnn.config.security.authentication;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 响应头
     */
    public static final String X_ACCESS_TOKEN = "X-Access-Token";

    @Autowired
    private CacheProvider<String> cacheProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = ((User) authentication.getPrincipal()).getUsername();
        String cachedToken = cacheProvider.get(username);

        String token;
        if (ObjectUtil.isNotEmpty(cachedToken)) {
            log.debug(String.format("用户重复登录,username: '%s'", username));
            // 不是首次登录不需要缓存token
            token = cachedToken;
        } else {
            log.debug("登录认证成功, {}", authentication);
            User loginUser = (User) authentication.getPrincipal();
            token = TokenUtil.createToken(loginUser.getUsername(), TokenUtil.getSigner(loginUser.getPassword()));
            if (cacheProvider.support(token)) {
                cacheProvider.doCache(token);
            }
        }
        response.setHeader(X_ACCESS_TOKEN, token);
        Result<?> result = Result.ok(MapUtil.of("token", token));

        String content = JSONUtil.parse(result).toString();
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(content);
    }
}
