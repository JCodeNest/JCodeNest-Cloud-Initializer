package cn.jcodenest.wiki.common.util.encrypt;

import cn.jcodenest.wiki.common.constants.EncryptConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 加密解密工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptUtils {

    /**
     * BCrypt密码编码器
     */
    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder(EncryptConstants.BCRYPT_ROUNDS);

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * MD5加密
     *
     * @param data 原始数据
     * @return MD5加密后的字符串，加密失败返回null
     */
    public static String md5(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }

        try {
            return DigestUtils.md5Hex(data);
        } catch (Exception e) {
            log.error("MD5加密失败: data={}", data, e);
            return null;
        }
    }

    /**
     * MD5加密（带盐值）
     *
     * @param data 原始数据
     * @param salt 盐值
     * @return MD5加密后的字符串，加密失败返回null
     */
    public static String md5WithSalt(String data, String salt) {
        if (StringUtils.isBlank(data)) {
            return null;
        }

        String saltedData = data + (salt == null ? "" : salt);
        return md5(saltedData);
    }

    /**
     * SHA256加密
     *
     * @param data 原始数据
     * @return SHA256加密后的字符串，加密失败返回null
     */
    public static String sha256(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }

        try {
            return DigestUtils.sha256Hex(data);
        } catch (Exception e) {
            log.error("SHA256加密失败: data={}", data, e);
            return null;
        }
    }

    /**
     * SHA256加密（带盐值）
     *
     * @param data 原始数据
     * @param salt 盐值
     * @return SHA256加密后的字符串，加密失败返回null
     */
    public static String sha256WithSalt(String data, String salt) {
        if (StringUtils.isBlank(data)) {
            return null;
        }

        String saltedData = data + (salt == null ? "" : salt);
        return sha256(saltedData);
    }

    /**
     * BCrypt密码加密
     *
     * @param password 原始密码
     * @return BCrypt加密后的密码，加密失败返回null
     */
    public static String bcryptEncode(String password) {
        if (StringUtils.isBlank(password)) {
            return null;
        }

        try {
            return BCRYPT_ENCODER.encode(password);
        } catch (Exception e) {
            log.error("BCrypt密码加密失败", e);
            return null;
        }
    }

    /**
     * BCrypt密码验证
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return true-验证通过，false-验证失败
     */
    public static boolean bcryptMatches(String rawPassword, String encodedPassword) {
        if (StringUtils.isBlank(rawPassword) || StringUtils.isBlank(encodedPassword)) {
            return false;
        }

        try {
            return BCRYPT_ENCODER.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error("BCrypt密码验证失败: rawPassword={}, encodedPassword={}", rawPassword, encodedPassword, e);
            return false;
        }
    }

    /**
     * AES加密
     *
     * @param data 原始数据
     * @param key  密钥
     * @return AES加密后的Base64字符串，加密失败返回null
     */
    public static String aesEncrypt(String data, String key) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
            return null;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), EncryptConstants.AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(EncryptConstants.AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            log.error("AES加密失败: data={}, key={}", data, key, e);
            return null;
        }
    }

    /**
     * AES解密
     *
     * @param encryptedData 加密后的Base64字符串
     * @param key           密钥
     * @return AES解密后的字符串，解密失败返回null
     */
    public static String aesDecrypt(String encryptedData, String key) {
        if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(key)) {
            return null;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), EncryptConstants.AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(EncryptConstants.AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] encrypted = Base64.decodeBase64(encryptedData);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败: encryptedData={}, key={}", encryptedData, key, e);
            return null;
        }
    }

    /**
     * AES加密（使用默认密钥）
     *
     * @param data 原始数据
     * @return AES加密后的Base64字符串，加密失败返回null
     */
    public static String aesEncrypt(String data) {
        return aesEncrypt(data, EncryptConstants.AES_KEY);
    }

    /**
     * AES解密（使用默认密钥）
     *
     * @param encryptedData 加密后的Base64字符串
     * @return AES解密后的字符串，解密失败返回null
     */
    public static String aesDecrypt(String encryptedData) {
        return aesDecrypt(encryptedData, EncryptConstants.AES_KEY);
    }

    /**
     * Base64编码
     *
     * @param data 原始数据
     * @return Base64编码后的字符串
     */
    public static String base64Encode(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }

        try {
            return Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Base64编码失败: data={}", data, e);
            return null;
        }
    }

    /**
     * Base64解码
     *
     * @param encodedData Base64编码的数据
     * @return Base64解码后的字符串
     */
    public static String base64Decode(String encodedData) {
        if (StringUtils.isBlank(encodedData)) {
            return encodedData;
        }

        try {
            byte[] decoded = Base64.decodeBase64(encodedData);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64解码失败: encodedData={}", encodedData, e);
            return null;
        }
    }

    /**
     * 生成随机盐值
     *
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        if (length <= 0) {
            return "";
        }

        byte[] salt = new byte[length];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    /**
     * 生成随机盐值（默认16字节）
     *
     * @return 随机盐值
     */
    public static String generateSalt() {
        return generateSalt(16);
    }

    /**
     * 生成AES密钥
     *
     * @return AES密钥的Base64字符串，生成失败返回null
     */
    public static String generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(EncryptConstants.AES_ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            log.error("生成AES密钥失败", e);
            return null;
        }
    }

    /**
     * 计算字符串的哈希值
     *
     * @param data      原始数据
     * @param algorithm 哈希算法
     * @return 哈希值的十六进制字符串，计算失败返回null
     */
    public static String hash(String data, String algorithm) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(algorithm)) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("计算哈希值失败: data={}, algorithm={}", data, algorithm, e);
            return null;
        }
    }

    /**
     * 验证数据完整性
     *
     * @param data         原始数据
     * @param expectedHash 期望的哈希值
     * @param algorithm    哈希算法
     * @return true-验证通过，false-验证失败
     */
    public static boolean verifyHash(String data, String expectedHash, String algorithm) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(expectedHash) || StringUtils.isBlank(algorithm)) {
            return false;
        }

        String actualHash = hash(data, algorithm);
        return expectedHash.equalsIgnoreCase(actualHash);
    }

    /**
     * 生成安全的随机字符串
     *
     * @param length 长度
     * @return 安全的随机字符串
     */
    public static String generateSecureRandomString(int length) {
        if (length <= 0) {
            return "";
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
