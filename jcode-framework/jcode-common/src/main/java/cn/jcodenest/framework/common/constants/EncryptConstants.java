package cn.jcodenest.framework.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 加密相关常量
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptConstants {

    /**
     * AES密钥
     */
    public static final String AES_KEY = "jcodenest-aes-key-2025-32bit";

    /**
     * AES算法
     */
    public static final String AES_ALGORITHM = "AES";

    /**
     * AES转换模式
     */
    public static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * MD5算法
     */
    public static final String MD5_ALGORITHM = "MD5";

    /**
     * SHA256算法
     */
    public static final String SHA256_ALGORITHM = "SHA-256";

    /**
     * RSA算法
     */
    public static final String RSA_ALGORITHM = "RSA";

    /**
     * RSA密钥长度
     */
    public static final int RSA_KEY_SIZE = 2048;

    /**
     * 密码加密轮数
     */
    public static final int BCRYPT_ROUNDS = 12;
}
