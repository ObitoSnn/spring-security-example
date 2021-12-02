package com.obitosnn.config.security.authentication;

import cn.hutool.json.JSONUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 401Unauthorized
 *
 * @author ObitoSnn
 */
@Slf4j
public class UnAuthorityEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("UnAuthorityEntryPoint, {}", authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String content = JSONUtil.parseObj(Result.error("操作失败", HttpStatus.UNAUTHORIZED.value(),"无权访问,请先登录")).toString();
        response.getWriter().write(content);
    }
}
