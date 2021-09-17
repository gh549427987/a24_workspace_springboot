package com.jhua.utils;

import com.jhua.fake.FakeConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public class RandomUtils {

    public static Integer RandomInt(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public static Object RandomArray(Object[] option) {

        //产生0-(arr.length-1)的整数值,也是数组的索引
        int index=(int)(Math.random()*option.length);
        Object chosen = option[index];
        return chosen;

    }

    public static long RandomSeconds(int min, int max) {
        Date date = new Date();
        Integer seconds = RandomInt(min, max);
//        long random_second = seconds;
        long time = date.getTime() / 1000 + seconds;

        return time;
    }




}
