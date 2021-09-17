package com.jhua.utils.security;

import com.alibaba.druid.sql.visitor.functions.Char;

import java.util.Random;

/**
 * @author xiejiehua
 * @DATE 9/11/2021
 */

public class SaltUtils {

    /*
     * @Author xiejiehua
     * @Description //TODO 生成盐的静态方法
     * @Date 8:58 PM 9/11/2021
     * @Param [n]
     * @return java.lang.String
     **/
    public static String getSalt(int n) {
        char[] chars = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789-*/+_)(*&^%$#@!~|:{}<>?;".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < n; i++) {
            char aChar = chars[new Random().nextInt(chars.length)];
            stringBuilder.append(aChar);
        }

        return stringBuilder.toString();
    }

}
