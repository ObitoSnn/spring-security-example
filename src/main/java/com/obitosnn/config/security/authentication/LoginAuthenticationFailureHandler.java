package com.obitosnn.config.security.authentication;

import com.obitosnn.util.ResponseOutputUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录认证失败回调
 *
 * @author ObitoSnn
 */
@Slf4j
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.debug("登录认证失败, {}", exception.getMessage());

        ResponseOutputUtil.output(response, Result.error("操作失败", HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
    }
}
