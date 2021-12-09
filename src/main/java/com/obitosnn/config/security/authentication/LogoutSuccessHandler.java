package com.obitosnn.config.security.authentication;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.obitosnn.util.ResponseOutputUtil;
import com.obitosnn.vo.Result;
import lombok.extern.slf4j.Slf4j;
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
        boolean empty = ObjectUtil.isEmpty(authentication);
        String msg = StrUtil.format("登出成功, {}", (empty ? "用户重复登出" : authentication).toString());
        if (empty) {
            log.debug(msg);
            return;
        }
        log.debug(msg);
        ResponseOutputUtil.output(response, Result.ok("登出成功"));
    }
}
