package com.obitosnn.config.security.authentication;

import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt认证成功回调
 *
 * @author ObitoSnn
 */
@Slf4j
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final CacheProvider<String, String> cacheProvider;

    public JwtAuthenticationSuccessHandler(CacheProvider<String, String> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("Jwt认证成功, {}", authentication);

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        UsernamePasswordAuthenticationToken auth
                = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        User loginUser = (User) auth.getPrincipal();
        String token = jwtAuthenticationToken.getPrincipal().toString();
        //更新token
        String refreshToken = TokenUtil.refreshTokenIfNecessary(token,
                TokenUtil.getSigner(loginUser.getPassword()));

        cacheProvider.update(token, refreshToken);
    }
}
