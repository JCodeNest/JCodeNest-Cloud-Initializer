package cn.jcodenest.framework.signature.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP API 签名 Redis DAO
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AllArgsConstructor
public class ApiSignatureRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 验签随机数
     * <p>
     * KEY   格式：signature_nonce:%s // 参数为 随机数
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String SIGNATURE_NONCE = "api_signature_nonce:%s:%s";

    /**
     * 签名密钥
     * <p>
     * HASH 结构
     * KEY   格式：%s     // 参数为 appid
     * VALUE 格式：String
     * 过期时间：永不过期（预加载到 Redis）
     */
    private static final String SIGNATURE_APPID = "api_signature_app";

    // ========== 验签随机数 ==========

    /**
     * 获取验签随机数
     *
     * @param appId appId
     * @param nonce 随机数
     * @return 验签随机数
     */
    public String getNonce(String appId, String nonce) {
        return stringRedisTemplate.opsForValue().get(formatNonceKey(appId, nonce));
    }

    /**
     * 设置验签随机数
     *
     * @param appId appId
     * @param nonce 随机数
     * @param time  过期时间
     * @param timeUnit 过期时间单位
     * @return 是否成功
     */
    public Boolean setNonce(String appId, String nonce, int time, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(formatNonceKey(appId, nonce), "", time, timeUnit);
    }

    /**
     * 格式化验签随机数 KEY
     *
     * @param appId appId
     * @param nonce 随机数
     * @return KEY
     */
    private static String formatNonceKey(String appId, String nonce) {
        return String.format(SIGNATURE_NONCE, appId, nonce);
    }

    // ========== 签名密钥 ==========

    /**
     * 获取签名密钥
     *
     * @param appId appId
     * @return 签名密钥
     */
    public String getAppSecret(String appId) {
        return (String) stringRedisTemplate.opsForHash().get(SIGNATURE_APPID, appId);
    }
}
