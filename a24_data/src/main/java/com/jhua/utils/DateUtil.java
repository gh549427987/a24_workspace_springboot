package com.jhua.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date date(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse("2020-07-06 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
