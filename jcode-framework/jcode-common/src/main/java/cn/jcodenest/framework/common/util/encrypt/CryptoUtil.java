package cn.jcodenest.framework.common.util.encrypt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;
import org.bouncycastle.util.encoders.Hex;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加解密工具类
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
public class CryptoUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String MD5_ALGORITHM = "MD5";
    private static final String SHA256_ALGORITHM = "SHA-256";

    // SM2 椭圆曲线参数
    private static final ECDomainParameters SM2_DOMAIN_PARAMS;

    static {
        // 初始化 SM2 椭圆曲线参数
        SM2P256V1Curve curve = new SM2P256V1Curve();
        ECPoint g = curve.createPoint(
                new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16),
                new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16)
        );
        BigInteger n = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B61C6823C0C2E5E2E4C6B4C6B", 16);
        BigInteger h = BigInteger.ONE;
        SM2_DOMAIN_PARAMS = new ECDomainParameters(curve, g, n, h);
    }

    /**
     * Jasypt 加密器实例
     */
    private static StringEncryptor stringEncryptor;

    /**
     * 初始化 Jasypt 加密器
     *
     * @param password 加密密码
     * @return StringEncryptor 实例
     */
    public static StringEncryptor initJasyptEncryptor(String password) {
        if (stringEncryptor == null) {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

            EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
            config.setPassword(password);
            config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
            config.setStringOutputType("base64");

            encryptor.setConfig(config);
            stringEncryptor = encryptor;
        }
        return stringEncryptor;
    }

    /**
     * Jasypt 加密
     *
     * @param plainText 明文
     * @param password  加密密码
     * @return 加密后的密文
     */
    public static String jasyptEncrypt(String plainText, String password) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        try {
            StringEncryptor encryptor = initJasyptEncryptor(password);
            return encryptor.encrypt(plainText);
        } catch (Exception e) {
            log.error("Jasypt 加密失败", e);
            throw new RuntimeException("Jasypt 加密失败", e);
        }
    }

    /**
     * Jasypt 解密
     *
     * @param encryptedText 密文
     * @param password      解密密码
     * @return 解密后的明文
     */
    public static String jasyptDecrypt(String encryptedText, String password) {
        if (!StringUtils.hasText(encryptedText)) {
            return encryptedText;
        }
        try {
            StringEncryptor encryptor = initJasyptEncryptor(password);
            return encryptor.decrypt(encryptedText);
        } catch (Exception e) {
            log.error("Jasypt 解密失败", e);
            throw new RuntimeException("Jasypt 解密失败", e);
        }
    }

    /**
     * MD5 加密
     *
     * @param plainText 明文
     * @return MD5 加密后的十六进制字符串
     */
    public static String md5Encrypt(String plainText) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            log.error("MD5 加密失败", e);
            throw new RuntimeException("MD5 加密失败", e);
        }
    }

    /**
     * SHA256 加密
     *
     * @param plainText 明文
     * @return SHA256 加密后的十六进制字符串
     */
    public static String sha256Encrypt(String plainText) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256_ALGORITHM);
            byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            log.error("SHA256 加密失败", e);
            throw new RuntimeException("SHA256 加密失败", e);
        }
    }

    /**
     * 生成 AES 密钥
     *
     * @return Base64 编码的 AES 密钥
     */
    public static String generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("生成 AES 密钥失败", e);
            throw new RuntimeException("生成 AES 密钥失败", e);
        }
    }

    /**
     * AES 加密
     *
     * @param plainText 明文
     * @param key       Base64 编码的 AES 密钥
     * @return Base64 编码的密文
     */
    public static String aesEncrypt(String plainText, String key) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES 加密失败", e);
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    /**
     * AES 解密
     *
     * @param encryptedText Base64 编码的密文
     * @param key           Base64 编码的 AES 密钥
     * @return 解密后的明文
     */
    public static String aesDecrypt(String encryptedText, String key) {
        if (!StringUtils.hasText(encryptedText)) {
            return encryptedText;
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] encrypted = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES 解密失败", e);
            throw new RuntimeException("AES 解密失败", e);
        }
    }

    /**
     * 生成 SM2 密钥对
     *
     * @return SM2KeyPair 包含公钥和私钥的十六进制字符串
     */
    public static SM2KeyPair generateSM2KeyPair() {
        try {
            ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
            ECKeyGenerationParameters keyGenParams = new ECKeyGenerationParameters(SM2_DOMAIN_PARAMS, new SecureRandom());
            keyPairGenerator.init(keyGenParams);
            AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

            ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
            ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();

            String privateKeyHex = privateKey.getD().toString(16);
            String publicKeyHex = Hex.toHexString(publicKey.getQ().getEncoded(false));

            return new SM2KeyPair(publicKeyHex, privateKeyHex);
        } catch (Exception e) {
            log.error("生成 SM2 密钥对失败", e);
            throw new RuntimeException("生成 SM2 密钥对失败", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * SM2 密钥对类
     */
    public static class SM2KeyPair {
        private final String publicKey;
        private final String privateKey;

        public SM2KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        @Override
        public String toString() {
            return "SM2KeyPair{" +
                    "publicKey='" + publicKey + '\'' +
                    ", privateKey='" + privateKey + '\'' +
                    '}';
        }
    }
}
