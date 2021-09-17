package com.jhua.utils;

import com.jhua.constants.Constants;

public class SingletonUtil {

    public static String sqlDate(String sql, String startDate, String endDate, String dateKey) {

        boolean flag = false;//标记位

        // 都为空，返回原本sql；有一个不为空，添加“where”
        if (startDate == null && endDate == null) {
            return sql;
        } else {
            sql += " where ";
        }

        // 添加startDate
        if (startDate != null) {
            sql += dateKey + ">'" + startDate + "'";
            flag = true;
        }

        // 添加endDate
        if (flag && endDate != null) {
            sql += " and ";
            sql += dateKey + "<'" + endDate + "'";
        } else if (endDate != null) {
            sql += dateKey + "<'" + endDate + "'";
        }

        return sql;
    }

    public static String sqlDate(String sql, String startDate, String endDate) {
        return sqlDate(sql, startDate, endDate, "create_time");
    }

    public static void main(String[] args) {
        String sql0 = sqlDate(Constants.playRecord_SelectAll, "2021-04-21 00:00:00", "2021-05-22 00:00:00");
        String sql1 = sqlDate(Constants.playRecord_SelectAll, "2021-04-21 00:00:00", null);
        String sql2 = sqlDate(Constants.playRecord_SelectAll, null, "2021-05-22 00:00:00");
        System.out.println(sql0);
        System.out.println(sql1);
        System.out.println(sql2);

    }

}
