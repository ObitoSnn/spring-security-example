package com.obitosnn.config.security.authentication;

import cn.hutool.json.JSONUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 403Forbidden
 *
 * @author ObitoSnn
 */
@Slf4j
public class HttpStatus403AccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.debug("HttpStatus403AccessDeniedHandler, {}", accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        String content = JSONUtil.parseObj(Result.error("操作失败", HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage())).toString();
        response.getWriter().write(content);
    }
}
