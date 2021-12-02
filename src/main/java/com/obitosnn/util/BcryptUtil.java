package com.obitosnn.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @author ObitoSnn
 */
public class BcryptUtil {

    private BcryptUtil() {
    }

    /**
     * 校验密码
     *
     * @param plaintext  明文
     * @param ciphertext 密文
     * @return true有效，false无效
     */
    public static boolean isValidate(String plaintext, String ciphertext) {
        return BCrypt.checkpw(plaintext, ciphertext);
    }

    /**
     * 加密
     *
     * @param plaintext 明文密码
     * @return 返回密文
     */
    public static String encrypt(String plaintext) {
        return BCrypt.hashpw(plaintext);
    }
}
