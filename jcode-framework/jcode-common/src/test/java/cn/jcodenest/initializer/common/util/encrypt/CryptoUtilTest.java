package cn.jcodenest.initializer.common.util.encrypt;

import cn.jcodenest.framework.common.util.encrypt.CryptoUtil;

import java.util.Scanner;

/**
 * 加解密测试类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class CryptoUtilTest {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * 用于存储生成的AES密钥
     */
    private static String aesKey = null;

    /**
     * 用于存储生成的SM2密钥对
     */
    private static CryptoUtil.SM2KeyPair sm2KeyPair = null;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("    JCodeNest Cloud 加解密工具测试程序");
        System.out.println("=================================================");

        while (true) {
            showMenu();
            int choice = getChoice();

            try {
                switch (choice) {
                    case 1:
                        testJasyptEncryption();
                        break;
                    case 2:
                        testJasyptDecryption();
                        break;
                    case 3:
                        testMD5Encryption();
                        break;
                    case 4:
                        testSHA256Encryption();
                        break;
                    case 5:
                        generateAESKey();
                        break;
                    case 6:
                        testAESEncryption();
                        break;
                    case 7:
                        testAESDecryption();
                        break;
                    case 8:
                        generateSM2KeyPair();
                        break;
                    case 9:
                        showCurrentKeys();
                        break;
                    case 10:
                        performanceTest();
                        break;
                    case 11:
                        showUsageGuide();
                        break;
                    case 0:
                        System.out.println("感谢使用 JCodeNest Cloud 加解密工具！再见！");
                        return;
                    default:
                        System.out.println("无效的选择，请重新输入！");
                }
            } catch (Exception e) {
                System.err.println("操作失败: " + e.getMessage());
            }

            System.out.println("\n按回车键继续...");
            scanner.nextLine();
        }
    }

    private static void showMenu() {
        System.out.println("\n=== 功能菜单 ===");
        System.out.println("1.  Jasypt 加密模式 (支持多次加密)");
        System.out.println("2.  Jasypt 解密模式 (支持多次解密)");
        System.out.println("3.  MD5 加密 (单次操作)");
        System.out.println("4.  SHA256 加密 (单次操作)");
        System.out.println("5.  生成 AES 密钥");
        System.out.println("6.  AES 加密模式 (支持多次加密)");
        System.out.println("7.  AES 解密模式 (支持多次解密)");
        System.out.println("8.  生成 SM2 密钥对");
        System.out.println("9.  查看当前密钥");
        System.out.println("10. 性能测试");
        System.out.println("11. 使用指南");
        System.out.println("0.  退出程序");
        System.out.print("请选择功能 (0-11): ");
    }

    private static int getChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void testJasyptEncryption() {
        System.out.println("\n=== Jasypt 加密模式 ===");
        System.out.print("请输入加密密码: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("密码不能为空！");
            return;
        }

        System.out.println("✓ 密码设置成功！现在可以进行多次加密操作");
        System.out.println("提示：输入 'exit' 或 'quit' 返回主菜单");

        while (true) {
            System.out.print("\n请输入要加密的明文 (exit/quit 退出): ");
            String plainText = scanner.nextLine();

            if ("exit".equalsIgnoreCase(plainText) || "quit".equalsIgnoreCase(plainText)) {
                System.out.println("退出 Jasypt 加密模式");
                break;
            }

            if (plainText.isEmpty()) {
                System.out.println("明文不能为空！请重新输入");
                continue;
            }

            try {
                String encrypted = CryptoUtil.jasyptEncrypt(plainText, password);
                System.out.println("原文: " + plainText);
                System.out.println("密文: " + encrypted);
                System.out.println("✓ 加密完成！");
            } catch (Exception e) {
                System.err.println("加密失败: " + e.getMessage());
            }
        }
    }

    private static void testJasyptDecryption() {
        System.out.println("\n=== Jasypt 解密模式 ===");
        System.out.print("请输入解密密码: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("密码不能为空！");
            return;
        }

        System.out.println("✓ 密码设置成功！现在可以进行多次解密操作");
        System.out.println("提示：输入 'exit' 或 'quit' 返回主菜单");

        while (true) {
            System.out.print("\n请输入要解密的密文 (exit/quit 退出): ");
            String encryptedText = scanner.nextLine();

            if ("exit".equalsIgnoreCase(encryptedText) || "quit".equalsIgnoreCase(encryptedText)) {
                System.out.println("退出 Jasypt 解密模式");
                break;
            }

            if (encryptedText.isEmpty()) {
                System.out.println("密文不能为空！请重新输入");
                continue;
            }

            try {
                String decrypted = CryptoUtil.jasyptDecrypt(encryptedText, password);
                System.out.println("密文: " + encryptedText);
                System.out.println("明文: " + decrypted);
                System.out.println("✓ 解密完成！");
            } catch (Exception e) {
                System.err.println("解密失败: " + e.getMessage());
                System.out.println("请检查密文格式和密码是否正确");
            }
        }
    }

    private static void testMD5Encryption() {
        System.out.println("\n=== MD5 加密 ===");
        System.out.print("请输入要加密的明文: ");
        String plainText = scanner.nextLine();

        if (plainText.isEmpty()) {
            System.out.println("明文不能为空！");
            return;
        }

        String md5Hash = CryptoUtil.md5Encrypt(plainText);
        System.out.println("原文: " + plainText);
        System.out.println("MD5: " + md5Hash);
        System.out.println("✓ MD5 加密完成！（注意：MD5 是不可逆的哈希算法）");
    }

    private static void testSHA256Encryption() {
        System.out.println("\n=== SHA256 加密 ===");
        System.out.print("请输入要加密的明文: ");
        String plainText = scanner.nextLine();

        if (plainText.isEmpty()) {
            System.out.println("明文不能为空！");
            return;
        }

        String sha256Hash = CryptoUtil.sha256Encrypt(plainText);
        System.out.println("原文: " + plainText);
        System.out.println("SHA256: " + sha256Hash);
        System.out.println("✓ SHA256 加密完成！（注意：SHA256 是不可逆的哈希算法）");
    }

    private static void generateAESKey() {
        System.out.println("\n=== 生成 AES 密钥 ===");
        aesKey = CryptoUtil.generateAESKey();
        System.out.println("AES 密钥: " + aesKey);
        System.out.println("✓ AES 密钥生成完成！（已保存到内存中，可用于后续加解密）");
    }

    private static void testAESEncryption() {
        System.out.println("\n=== AES 加密模式 ===");

        String currentAesKey = aesKey;
        if (currentAesKey == null) {
            System.out.print("未找到 AES 密钥，是否生成新密钥？(y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(choice) || "yes".equals(choice)) {
                currentAesKey = CryptoUtil.generateAESKey();
                aesKey = currentAesKey; // 保存到全局变量
                System.out.println("AES 密钥: " + currentAesKey);
            } else {
                System.out.print("请输入 AES 密钥: ");
                currentAesKey = scanner.nextLine();
                if (currentAesKey.isEmpty()) {
                    System.out.println("AES 密钥不能为空！");
                    return;
                }
                aesKey = currentAesKey; // 保存到全局变量
            }
        }

        System.out.println("✓ AES 密钥设置成功！现在可以进行多次加密操作");
        System.out.println("当前使用的密钥: " + currentAesKey);
        System.out.println("提示：输入 'exit' 或 'quit' 返回主菜单");

        while (true) {
            System.out.print("\n请输入要加密的明文 (exit/quit 退出): ");
            String plainText = scanner.nextLine();

            if ("exit".equalsIgnoreCase(plainText) || "quit".equalsIgnoreCase(plainText)) {
                System.out.println("退出 AES 加密模式");
                break;
            }

            if (plainText.isEmpty()) {
                System.out.println("明文不能为空！请重新输入");
                continue;
            }

            try {
                String encrypted = CryptoUtil.aesEncrypt(plainText, currentAesKey);
                System.out.println("原文: " + plainText);
                System.out.println("密文: " + encrypted);
                System.out.println("✓ 加密完成！");
            } catch (Exception e) {
                System.err.println("加密失败: " + e.getMessage());
            }
        }
    }

    private static void testAESDecryption() {
        System.out.println("\n=== AES 解密模式 ===");

        String currentAesKey = aesKey;
        if (currentAesKey == null) {
            System.out.print("请输入 AES 密钥: ");
            currentAesKey = scanner.nextLine();
            if (currentAesKey.isEmpty()) {
                System.out.println("AES 密钥不能为空！");
                return;
            }
            aesKey = currentAesKey; // 保存到全局变量
        }

        System.out.println("✓ AES 密钥设置成功！现在可以进行多次解密操作");
        System.out.println("当前使用的密钥: " + currentAesKey);
        System.out.println("提示：输入 'exit' 或 'quit' 返回主菜单");

        while (true) {
            System.out.print("\n请输入要解密的密文 (exit/quit 退出): ");
            String encryptedText = scanner.nextLine();

            if ("exit".equalsIgnoreCase(encryptedText) || "quit".equalsIgnoreCase(encryptedText)) {
                System.out.println("退出 AES 解密模式");
                break;
            }

            if (encryptedText.isEmpty()) {
                System.out.println("密文不能为空！请重新输入");
                continue;
            }

            try {
                String decrypted = CryptoUtil.aesDecrypt(encryptedText, currentAesKey);
                System.out.println("密文: " + encryptedText);
                System.out.println("明文: " + decrypted);
                System.out.println("✓ 解密完成！");
            } catch (Exception e) {
                System.err.println("解密失败: " + e.getMessage());
                System.out.println("请检查密文格式和密钥是否正确");
            }
        }
    }

    private static void generateSM2KeyPair() {
        System.out.println("\n=== 生成 SM2 密钥对 ===");
        sm2KeyPair = CryptoUtil.generateSM2KeyPair();
        System.out.println("SM2 公钥: " + sm2KeyPair.getPublicKey());
        System.out.println("SM2 私钥: " + sm2KeyPair.getPrivateKey());
        System.out.println("✓ SM2 密钥对生成完成！（已保存到内存中）");
    }

    private static void showCurrentKeys() {
        System.out.println("\n=== 当前密钥信息 ===");

        if (aesKey != null) {
            System.out.println("AES 密钥: " + aesKey);
        } else {
            System.out.println("AES 密钥: 未生成");
        }

        if (sm2KeyPair != null) {
            System.out.println("SM2 公钥: " + sm2KeyPair.getPublicKey());
            System.out.println("SM2 私钥: " + sm2KeyPair.getPrivateKey());
        } else {
            System.out.println("SM2 密钥对: 未生成");
        }
    }

    private static void performanceTest() {
        System.out.println("\n=== 性能测试 ===");
        System.out.print("请输入测试次数 (建议1000): ");
        String input = scanner.nextLine();

        int iterations;
        try {
            iterations = Integer.parseInt(input);
            if (iterations <= 0) {
                System.out.println("测试次数必须大于0！");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("无效的数字格式！");
            return;
        }

        String testData = "Performance test data for encryption";
        System.out.println("开始性能测试，测试数据: " + testData);
        System.out.println("测试次数: " + iterations);

        // MD5 性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            CryptoUtil.md5Encrypt(testData + i);
        }
        long md5Time = System.currentTimeMillis() - startTime;
        System.out.println("MD5 " + iterations + "次加密耗时: " + md5Time + "ms");

        // SHA256 性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            CryptoUtil.sha256Encrypt(testData + i);
        }
        long sha256Time = System.currentTimeMillis() - startTime;
        System.out.println("SHA256 " + iterations + "次加密耗时: " + sha256Time + "ms");

        // AES 性能测试
        String testAesKey = CryptoUtil.generateAESKey();
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            String encrypted = CryptoUtil.aesEncrypt(testData + i, testAesKey);
            CryptoUtil.aesDecrypt(encrypted, testAesKey);
        }
        long aesTime = System.currentTimeMillis() - startTime;
        System.out.println("AES " + iterations + "次加解密耗时: " + aesTime + "ms");

        System.out.println("✓ 性能测试完成！");
    }

    private static void showUsageGuide() {
        System.out.println("\n=== 使用指南 ===");
        System.out.println();
        System.out.println("📋 各种加密算法的适用场景：");
        System.out.println();
        System.out.println("1. 🔐 Jasypt 加密");
        System.out.println("   - 适用场景：Spring Boot 配置文件加密");
        System.out.println("   - 特点：可逆加密，与 Spring Boot 无缝集成");
        System.out.println("   - 使用示例：数据库密码、API密钥等敏感配置");
        System.out.println("   - 配置方式：application.yml 中使用 ENC(密文)");
        System.out.println();

        System.out.println("2. 🔒 MD5 加密");
        System.out.println("   - 适用场景：用户密码存储、数据完整性校验");
        System.out.println("   - 特点：不可逆哈希算法，速度快");
        System.out.println("   - 注意：安全性较低，建议加盐使用");
        System.out.println("   - 输出长度：32位十六进制字符串");
        System.out.println();

        System.out.println("3. 🛡️ SHA256 加密");
        System.out.println("   - 适用场景：密码存储、数字签名、区块链");
        System.out.println("   - 特点：不可逆哈希算法，安全性高");
        System.out.println("   - 推荐：比 MD5 更安全的哈希选择");
        System.out.println("   - 输出长度：64位十六进制字符串");
        System.out.println();

        System.out.println("4. 🔑 AES 加密");
        System.out.println("   - 适用场景：数据传输、文件加密、API通信");
        System.out.println("   - 特点：对称加密，可逆，性能好");
        System.out.println("   - 密钥长度：256位（本工具使用）");
        System.out.println("   - 注意：密钥需要安全保存和传输");
        System.out.println();

        System.out.println("5. 🇨🇳 SM2 国密算法");
        System.out.println("   - 适用场景：国密要求的项目、政府系统");
        System.out.println("   - 特点：非对称加密，符合国家密码标准");
        System.out.println("   - 用途：数字签名、密钥交换、身份认证");
        System.out.println("   - 标准：GM/T 0003-2012");
        System.out.println();

        System.out.println("💡 最佳实践建议：");
        System.out.println("   • 密码存储：使用 SHA256 + 盐值");
        System.out.println("   • 配置加密：使用 Jasypt");
        System.out.println("   • 数据传输：使用 AES");
        System.out.println("   • 国密要求：使用 SM2");
        System.out.println("   • 性能要求高：优先选择 AES 和 MD5");
        System.out.println("   • 安全要求高：优先选择 SHA256 和 SM2");
        System.out.println();

        System.out.println("⚠️ 安全提醒：");
        System.out.println("   • 密钥和密码请妥善保管，不要硬编码在代码中");
        System.out.println("   • 生产环境建议使用环境变量或密钥管理系统");
        System.out.println("   • 定期更换密钥和密码");
        System.out.println("   • 对敏感数据进行多重加密保护");
    }
}
