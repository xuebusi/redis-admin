package com.xuebusi.cms.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密解密算法
 *
 * @author songqingyun@xuebusi.com on 2018/6/28.
 */
public class CryptUtils {

    private static Logger logger = LoggerFactory.getLogger(CryptUtils.class);
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"; // 默认的加密算法
    private static final String PASSWORD = "AHuer4fzxdkr923487xsnHFlqnrb108v";

    /**
     * mask前先解码
     *
     * @param content
     * @param decode
     * @return
     */
    public static String mask(String content, boolean decode) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }
        if (decode) {
            content = decrypt(content);
        }
        return mask(content);
    }

    /**
     * 打码工具
     *
     * @param content
     * @return
     */
    public static String mask(String content) {
        if (StringUtils.isEmpty(content)) {
            return StringUtils.EMPTY;
        }
        int length = content.length();
        if (length == 1) {
            return content;
        }
        if (length == 2) {
            return content.substring(0, 1) + "*";
        }
        if (length < 5) {
            return content.substring(0, 1) + "**";
        }
        if (content.contains("@")) {
            return mask(content.substring(0, content.indexOf("@"))) + content.substring(content.indexOf("@"));
        }
        if (length >= 5) {
            return content.substring(0, 2) + "******" + content.substring(length - 2);

        }
        return StringUtils.EMPTY;
    }

    /**
     * 使用默认密码
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        return encrypt(content, PASSWORD);
    }

    /**
     * AES 加密操作
     *
     * @param content  待加密内容
     * @param password 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM); // 创建密码器

            byte[] byteContent = content.getBytes("utf-8");

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password)); // 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent); // 加密

            return Base64.getEncoder().encodeToString(result); // 通过Base64转码返回
        } catch (Exception ex) {
            logger.error("error", ex);
        }

        return null;
    }

    /**
     * 使用默认密码
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return decrypt(content, PASSWORD);
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param password
     * @return
     */
    public static String decrypt(String content, String password) {

        try {
            // 实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            if (StringUtils.isNotBlank(content)) {
                // 执行操作
                byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));
                return new String(result, "utf-8");
            }
        } catch (Exception ex) {
            logger.error("error", ex.getMessage());
        }

        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String password) {
        // 返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;

        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            // AES 要求密钥长度为 128
            kg.init(128, random);

            // 生成一个密钥
            SecretKey secretKey = kg.generateKey();

            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM); // 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            logger.error("error", ex);
        }

        return null;
    }

    public static void main(String[] args) {
        String s = "";
        for (String ss : s.split(",")) {
            System.out.println(decrypt(ss));
        }
    }
}
