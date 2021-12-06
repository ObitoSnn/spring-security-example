package com.obitosnn.config.security.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 登录认证过滤器
 * 如果登录认证过程交给Controller处理，则不需要这个类
 *
 * @author ObitoSnn
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    public LoginFilter(AuthenticationManager authenticationManager,
                       AuthenticationSuccessHandler authenticationSuccessHandler,
                       AuthenticationFailureHandler authenticationFailureHandler) {
        //拦截 /login POST
        super();
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("登录认证Filter");

        try {
            ServletInputStream inputStream = request.getInputStream();
            String content = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONUtil.parseObj(content);
            String username = jsonObject.get("username").toString();

            if (ObjectUtil.isNotEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (ObjectUtil.isNotEmpty(loginUser) && loginUser.getUsername().equals(username)) {
                    return SecurityContextHolder.getContext().getAuthentication();
                }
            }

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username, jsonObject.get("password"));
            return getAuthenticationManager().authenticate(token);
        } catch (Exception e) {
            throw new AuthenticationException("认证失败") {};
        }
    }
}
