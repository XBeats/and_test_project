package com.aitangba.testproject.datasecurity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fhf11991 on 2018/6/6
 */
public class AESCoder {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * 加密
     *
     * @param secretKeyStr 密钥
     * @param content 明文数据
     * @return 加密后的数据
     * @throws GeneralSecurityException
     */
    public static String encrypt(String secretKeyStr, String content) throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, new SecureRandom(secretKeyStr.getBytes(CHARSET)));
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();

        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        byte[] byteContent = content.getBytes(CHARSET);
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化

        byte[] result = cipher.doFinal(byteContent); // 加密
        return parseByte2HexStr(result); // 转换成16进制，方面传递数据
    }

    /**
     * 解密
     *
     * @param secretKeyStr 密钥
     * @param encryptedContentStr 16进制的数据
     * @return 解密后的数据
     * @throws GeneralSecurityException
     */
    public static String decrypt(String secretKeyStr, String encryptedContentStr) throws GeneralSecurityException {
        byte[] encryptedContent = parseHexStr2Byte(encryptedContentStr); // 由16进制数据转换成2进制

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        // 手动设置Random，解决在Linux系统中发生“BadPaddingException”
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(secretKeyStr.getBytes(CHARSET));
        keyGenerator.init(128, random);

        SecretKey secretKey = keyGenerator.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();

        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化

        byte[] result = cipher.doFinal(encryptedContent); // 解密
        return new String(result, CHARSET);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, GeneralSecurityException {
        String content = "我是中国人shoneworn12233";
        String secretKeyStr = "12345678";
        // 加密
        System.out.println("加密前：" + content);
        String code = encrypt(secretKeyStr, content);
        System.out.println("密文字符串：" + code);
        // 解密
        System.out.println("解密后：" + decrypt(secretKeyStr, code)); //不转码会乱码
    }
}
