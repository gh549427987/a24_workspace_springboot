package com.jhua.utils;

import com.alibaba.fastjson.JSONObject;
import com.jhua.config.GameConstant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public class HmacSHAUtils {

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String hmacSHA256(String message, String key) {

        // 加密
        Mac hmacSHA256 = null;
        try {
            hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec hmacSHA2561 = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            hmacSHA256.init(hmacSHA2561);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("加密出错");
        }

        byte[] bytes = hmacSHA256.doFinal(message.getBytes());
        String hash = byteArrayToHexString(bytes);
        return hash;
    }

}
