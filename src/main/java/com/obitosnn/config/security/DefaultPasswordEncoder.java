package com.obitosnn.config.security;

import com.obitosnn.util.BcryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author ObitoSnn
 */
@Slf4j
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return BcryptUtil.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.debug("正在验证密码, rawPassword:{}, encodedPassword:{}",
                rawPassword.toString(), encodedPassword) ;

        return BcryptUtil.isValidate(rawPassword.toString(), encodedPassword.toString());
    }
}
