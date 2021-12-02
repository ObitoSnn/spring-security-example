package com.obitosnn.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.RegisteredPayload;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author ObitoSnn
 */
public class TokenUtil {

    private TokenUtil() {
    }

    /**
     * token有效时间
     */
    public static final long DEFAULT_EXPIRE_TIME = 30L * 60L * 1000L;

    /**
     * token刷新间隔
     */
    private static final long REFRESH_INTERVAL = 60L * 1000L;

    /**
     * 刷新token
     * 当前时间与签发{@link cn.hutool.jwt.RegisteredPayload#ISSUED_AT}时间间隔超过{@link #REFRESH_INTERVAL}则生成新的token
     *
     * @param token 待刷新的token
     * @return 返回token
     */
    public static String refreshTokenIfNecessary(String token, JWTSigner signer) {
        JWT jwt = JWT.of(token).setSigner(signer);

        Date issuedAt = jwt.getPayload().getClaimsJson().getDate(RegisteredPayload.ISSUED_AT);

        LocalDateTime refreshLocalDateTime = LocalDateTime.ofInstant(new Date(issuedAt.getTime() + REFRESH_INTERVAL).toInstant(),
                ZoneId.systemDefault());

        //判断是否需要生成新的token
        boolean shouldRefresh = refreshLocalDateTime.isBefore(LocalDateTime.now());

        if (shouldRefresh) {
            token = createToken(jwt.getPayload().getClaim("username").toString(), signer);
        }

        return token;
    }

    /**
     * 验证JWT是否有效
     *
     * @param token  token
     * @param signer 签名器
     * @return true有效，false无效
     */
    public static boolean isValidate(String token, JWTSigner signer) {
        return JWT.of(token).setSigner(signer).validate(0);
    }

    /**
     * 生成token
     *
     * @param username payload信息
     * @param signer   签名器
     * @return 生成的token
     */
    public static String createToken(String username, JWTSigner signer) {
        return JWT.create()
                .setPayload("username", username)
                .setExpiresAt(new Date(System.currentTimeMillis() + DEFAULT_EXPIRE_TIME))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSigner(signer)
                .sign();
    }

    /**
     * 获取token中载荷信息的信息，默认获取username
     *
     * @param token token
     * @return 返回载荷中的信息
     */
    public static String getInfoByToken(String token) {
        return getInfoByToken(token, "username");
    }

    /**
     * 根据token获取对应属性的信息
     *
     * @param token     token
     * @param fieldName 属性名
     * @return token中的信息
     */
    public static String getInfoByToken(String token, String fieldName) {
        return JWT.of(token).getPayload().getClaim(fieldName).toString();
    }

    /**
     * 获取JWTSigner
     *
     * @param key 签名密钥
     * @return JWTSigner
     */
    public static JWTSigner getSigner(String key) {
        return JWTSignerUtil.hs256(key.getBytes(StandardCharsets.UTF_8));
    }
}
