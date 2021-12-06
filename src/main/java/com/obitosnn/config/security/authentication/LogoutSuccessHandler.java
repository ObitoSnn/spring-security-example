package com.obitosnn.config.security.authentication;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出成功回调
 *
 * @author ObitoSnn
 */
@Slf4j
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("登出成功, {}", ObjectUtil.isEmpty(authentication) ? "用户重复登出" : authentication);

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        String content = JSONUtil.parseObj(Result.ok("登出成功")).toString();

        response.getWriter().write(content);
    }
}
