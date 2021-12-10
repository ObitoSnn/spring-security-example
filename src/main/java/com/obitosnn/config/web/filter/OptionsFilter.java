package com.obitosnn.config.web.filter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 提供跨域预检请求(OPTIONS)支持
 *
 * @author ObitoSnn
 */
public class OptionsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 跨域时会首先发送一个option请求(预检请求)，这里我们给option请求直接返回正常状态
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        // 是否允许发送Cookie，默认Cookie不包括在CORS请求之中。设为true时，表示服务器允许Cookie包含在请求中。
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpStatus.OK.value());
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return;
        }
        filterChain.doFilter(request, response);
    }
}
