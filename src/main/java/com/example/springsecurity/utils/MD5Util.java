package com.example.springsecurity.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: MD5工具
 * @author: Zhaotianyi
 * @time: 2021/11/18 13:39
 */
public class MD5Util {
    private static String encodedPasswod = "EencodedPassword";
    private static String RandomSaltHash = "RandomSaltHash";
    private static char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 生成Md5密文
     *
     * @param inputStr
     * @return
     */
    public static String generateMd5(String inputStr) {
        String str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            str = byte2HexStr(md.digest(inputStr.getBytes()));
            return str;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 利用明文生成带有16位随机盐的Md5密文
     *
     * @param inputStr 明文
     */
    public static Map<String, String> generateMd5With16BitRandomSalt(String inputStr) {
        Map<String, String> map = new HashMap<>();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String salt = SaltUtil.getSalt(16);
            //原文加盐
            String inputWithSalt = inputStr + salt;
            //哈希计算,转换输出 带盐密文
            String hashResult = byte2HexStr(md.digest(inputWithSalt.getBytes()));
            map.put(encodedPasswod, hashResult);

            char[] cs = new char[48];
            for (int i = 0; i < 48; i += 3) {
                cs[i] = hashResult.charAt(i / 3 * 2);
                //输出带盐，存储盐到hash值中;每两个hash字符中间插入一个盐字符
                cs[i + 1] = salt.charAt(i / 3);
                cs[i + 2] = hashResult.charAt(i / 3 * 2 + 1);
            }
            hashResult = new String(cs);
            // 传入带有盐的密文hash
            // 用于登陆验证密码时使用相同的盐
            map.put(RandomSaltHash, hashResult);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 检验带盐的hash是否为其明文
     *
     * @param rawPassword    明文
     * @param encodedPasswod 从数据库中查询到的加密密文
     * @param Salthash       带盐hash
     * @return boolean
     */
    public static boolean matchesHashWithSalt(String rawPassword, String encodedPasswod, String Salthash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            String queriedHash = Salthash;
            String salt = getSaltFromHash(queriedHash);
            String inputWithSalt = rawPassword + salt;
            String Result = byte2HexStr(md.digest(inputWithSalt.getBytes()));
            if (encodedPasswod.equals(Result)) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @return: 十六进制字符串
     * @params: [bytes]
     * @Descrption: 将字节数组转换成十六进制字符串
     */
    private static String byte2HexStr(byte[] bytes) {
        /**
         *@Author: DavidHuang
         *@Time: 19:41 2018/5/10
         *@return: java.lang.String
         *@params:  * @param bytes
         *@Descrption:
         */
        int len = bytes.length;
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < len; i++) {
            byte byte0 = bytes[i];
            result.append(hex[byte0 >>> 4 & 0xf]);
            result.append(hex[byte0 & 0xf]);
        }
        return result.toString();
    }

    /**
     * @return: 提取的salt
     * @params: [hash] 3i byte带盐的hash值,带盐方法与MD5WithSalt中相同
     * @Descrption: 从库中查找到的hash值提取出的salt
     */
    public static String getSaltFromHash(String hash) {
        StringBuilder sb = new StringBuilder();
        char[] h = hash.toCharArray();
        for (int i = 0; i < hash.length(); i += 3) {
            sb.append(h[i + 1]);
        }
        return sb.toString();
    }
}
