package com.jhua.event;

import com.jhua.Singleton.PlayRecordSingleton;
import com.jhua.constants.Constants;
import com.jhua.dao.base.BaseDao;
import com.jhua.utils.DataDealer;
import joinery.DataFrame;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayRecord {

    // 获取PlayRecord表
    public static DataFrame<Object> dataFrameOfPlayRecord() throws IOException {
            return PlayRecordSingleton.dataFrameOfPlayRecord();
    }
    public static DataFrame<Object> dataFrameOfPlayRecord(String startDate, String endDate) throws IOException {
            return PlayRecordSingleton.dataFrameOfPlayRecord(startDate, endDate);
    }

    // 从本地读取数据，减轻数据库压力
    public static DataFrame<Object> dataFrameOfPlayRecord_fromLocalCsv() throws IOException {
        File file = new File(Constants.OUT_PUT_PATH + "/source/PlayRecord.csv");
        if (file.exists()) {
            System.out.println("读取本地文件" + file.getAbsolutePath());
            DataFrame<Object> dataFrame = DataFrame.readCsv(file.getAbsolutePath());
            return dataFrame;
        }
        return null;
    }

    // 返回属于商家的PlayRecord表
    public static DataFrame<Object> dataFrameOfPlayRecord_Store() throws IOException {
        return Store.returnIsStoreDataFrame(dataFrameOfPlayRecord());
    }
    public static DataFrame<Object> dataFrameOfPlayRecord_Store(String startDate, String endDate) throws IOException {
        return Store.returnIsStoreDataFrame(dataFrameOfPlayRecord(startDate, endDate));
    }

    // 返回游玩超过3分钟的比
    public static DataFrame<Object> dataFrameOfPlayRecord_Out3Min(DataFrame<Object> dataFrame) {
        Map<String, Integer> index = DataDealer.columns_index(dataFrame);
        dataFrame = dataFrame.select(value -> {
            return (double) value.get(index.get("play_minutes")) > 3.0; // 选择游玩大于3分钟的玩家
        });
        return dataFrame;
    }

    public static DataFrame<Object> per_month_recharge_store() throws IOException {
        DataFrame<Object> play_record = dataFrameOfPlayRecord();
        DataFrame<Object> store = Store.dataFrameOfStore();

        Map<String, Integer> store_index = DataDealer.columns_index(store);
        // 以user_id为主键 左连接 表


//        play_record.writeCsv("/Users/jhua/IdeaProjects/kuangshen/Joinery/src/main/output/Text.csv");


        return play_record;
    }

    // 玩家 时长 日期
    public static DataFrame<Object> perAccountPlayRecordDayCounts(String fromDate, String toDate, Boolean isStore) throws IOException, ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO 每个商家，每天玩的总次数
         *                 条件，如当日游戏时长大于3分钟的商家数
         *                  日度就传入如“2021-01-02”，月度就传入如“2021-01”，周度传入如”2021-01-02 00“
         * @Date  2021/3/16
         * @Param [fromDate, toDate]
         * @return joinery.DataFrame<java.lang.Object>
         **/


        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null)
            return null;

        // 设置时间
        Calendar cal_create = Calendar.getInstance();
        cal_create.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_fromDate = Calendar.getInstance();
        cal_fromDate.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_toDate = Calendar.getInstance();
        cal_toDate.setFirstDayOfWeek(Calendar.MONDAY);

        final SimpleDateFormat sim_3 = new SimpleDateFormat(pattern);

        // 处理并筛选出合适的数据
        DataFrame<Object> dataFrame = PlayRecord.dataFrameOfPlayRecord(fromDate, toDate);
//        DataDealer.format_time(dataFrame, "create_time", pattern);

        // 不修改上面那一句规范时间的语句，下面新增一条
//        Map<String, Integer> index = DataDealer.columns_index(dataFrame);
//        dataFrame.select(value -> {
//            Date create_time = (Date) value.get(index.get("create_time"));
//            System.out.println(create_time);
//            return true;
//        });
        // 上面这一段不用了，以后尽量少用DataDealer.format_time这个方法

        dataFrame = dataFrame.retain("user_id", "play_minutes", "create_time").groupBy("user_id", "create_time").sum(); //每天玩的总时长数
        Map<String, Integer> stringIntegerMap = DataDealer.columns_index(dataFrame);
        dataFrame = dataFrame.select(value -> {

            // 时间比较
            Date create_time = (Date) value.get(stringIntegerMap.get("create_time"));

            try {
                cal_fromDate.setTime(sim_3.parse(fromDate));
                cal_toDate.setTime(sim_3.parse(toDate));
                cal_create.setTime(create_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return cal_create.after(cal_fromDate) && cal_create.before(cal_toDate) && (Double)value.get(stringIntegerMap.get("play_minutes"))>3.0;
            
        });  //筛选时长大于3分钟
//        dataFrame.writeXls(Constants.OUT_PUT_PATH+"筛选之后的数据.xlsx");

        // 返回指定时间区间的数据
//        DataDealer.selectDataFrameOfDetectedTime(dataFrame, fromDate, toDate);

        // 通过isStore判断是否需要进行商家的统计
        if (isStore)
            dataFrame = Store.returnIsStoreDataFrame(dataFrame);

        // 分发数据
        // 先创建一个带有时间的空dataFrame
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, dataFrame.col("user_id").size());

        result = result.transpose();

        // 遍历并把所有的东西都写进去
        List<Object> result_userid_list = result.col(0);
        // 确定是同一行
//        DataDealer.format_time(dataFrame, "create_time", pattern);

        int row = 0;
        for (int i = 0; i < dataFrame.col(0).size(); i++) {

            // 确定是同一行
            row = i;

            // USER_ID
            // 先判断dataFrame此处的user_id是否在result的index列表内
            String user_id = (String) dataFrame.get(i, 0);
            if (!(result_userid_list.contains(user_id))) {

                // 如果不存在这个user_id的话，就给他加上
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j) == null) {
                        result.set(j, 0, user_id);
                        row = j;
                        break;
                    }

                }
            } else {
                // 如果存在这个user_id,确定为同一行
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j).equals(user_id)) {
                        row = j;
                        break;
                    }
                }
            }

            Date user_time = (Date) dataFrame.get(i, 1);//时间获取
            //CREATE_TIME
            //遍历result表的时间，看是否有符合的，添加进去

            for (int j = 1; j < result.columns().size(); j++) {
                String result_time = (String) result.columns().toArray()[j]; //获取列名，硬转String

                // 先比较时间谁大谁小
                Calendar cal_result = Calendar.getInstance();
                cal_result.setFirstDayOfWeek(Calendar.MONDAY);
                Calendar cal_user = Calendar.getInstance();
                cal_user.setFirstDayOfWeek(Calendar.MONDAY);
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                Date result_time_d = sdf.parse(result_time);
                Date user_time_d = user_time;

                cal_result.setTime(result_time_d);
                cal_user.setTime(user_time_d);


                // 分开 日-月比较和周比较
                if (pattern.length() == 10) {

                    // 如果子表的时间，比结果表的时间还早，那就没必要继续遍历了
                    if (cal_user.before(cal_result)) {
                        break;
                    } else if (cal_user.get(Calendar.DAY_OF_MONTH) == cal_result.get(Calendar.DAY_OF_MONTH)) {
                        // 日期相同
                        if (result.get(row, j) != null) {
                            Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                            result.set(row, j, o);
                            break;
                        } else {
                            result.set(row, j, dataFrame.get(i, 2));
                            break;
                        }
                    }
                } else if (pattern.length() == 7) {
                    // 如果子表的时间，比结果表的时间还早，那就没必要继续遍历了
                    if (cal_user.before(cal_result)) {
                        break;
                    } else if (cal_user.get(Calendar.MONTH) == cal_result.get(Calendar.MONTH)) {
                        // 日期相同
                        if (result.get(row, j) != null) {
                            Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                            result.set(row, j, o);
                            break;
                        } else {
                            result.set(row, j, dataFrame.get(i, 2));
                            break;
                        }
                    }
                } else {
                    // 比较周
                    int wiy_user = cal_user.get(Calendar.WEEK_OF_YEAR);
                    int wiy_result = cal_result.get(Calendar.WEEK_OF_YEAR);
                    if (wiy_user == wiy_result){
                        // result非null，说明已经有数据已经在里面，需要加起来
                        if (result.get(row, j) != null){
                            Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                            result.set(row, j, o);
                            break;
                        } else {
                            result.set(row, j, dataFrame.get(i, 2));
                            break;
                        }
                    }
                }
            }


        }

//        result.set(0, 0, "账号/游戏时长/日期");
        if (pattern.length() == 7 && isStore) {
            result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "平台月活跃商家：单月游戏时长大于3分钟的账号(月度）.xlsx");
        }
        else if (pattern.length() == 7) {
            result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "平台月活跃账号：单月游戏时长大于3分钟的账号(月度）.xlsx");
        }
        else if (pattern.length() == 10 && isStore) {
            result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "平台日活跃商家：单日游戏时长大于3分钟的账号(日度）.xlsx");
        }
        else if (pattern.length() == 10) {
            result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "平台日活跃账号：单日游戏时长大于3分钟的账号(日度）.xlsx");
        }
        else if (pattern.length() == 13 && isStore) {
            result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "平台周活跃商家：单周游戏时长大于3分钟的账号(周度）.xlsx");
        }
        else if (pattern.length() == 13) {
            result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "平台周活跃账号：单周游戏时长大于3分钟的账号(周度）.xlsx");
        }


        return result;
    }

    // 商家时长 周
    public static DataFrame<Object> perStorePlayRecordWeekendPlayTimeSum(String fromDate, String toDate) throws IOException, ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO 周末游戏时长 商家
         * @Date  2021/3/17
         * @Param [fromDate, toDate]
         * @return joinery.DataFrame<java.lang.Object>
         **/
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null || pattern.length() != 13)
            return null;

        // 处理并筛选出合适的数据
//        DataFrame<Object> dataFrame = dataFrameOfPlayRecord();
        DataFrame<Object> dataFrame = PlayRecord.dataFrameOfPlayRecord(fromDate, toDate);
        DataDealer.format_time(dataFrame, "create_time", pattern);
        dataFrame = dataFrame.retain("user_id", "play_minutes", "create_time").groupBy("user_id", "create_time").sum();
        Map<String, Integer> stringIntegerMap = DataDealer.columns_index(dataFrame);
        dataFrame = dataFrame.select(value -> (Double)value.get(stringIntegerMap.get("play_minutes"))>3.0);  //筛选时长大于3分钟
//        dataFrame.writeCsv(Constants.OUT_PUT_PATH+"筛选之后的数据.csv");

        // 通过isStore判断是否需要进行商家的统计
        boolean isStore = true; //恒定true,以后有需求再改
        if (isStore)
            dataFrame = Store.returnIsStoreDataFrame(dataFrame);

        // 分发数据
        // 先创建一个带有时间的空dataFrame
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, dataFrame.col("user_id").size());
        result = result.transpose();

        // 遍历并把所有的东西都写进去
        List<Object> result_userid_list = result.col(0);
        // 确定是同一行
        int row = 0;
        for (int i = 0; i < dataFrame.col(0).size(); i++) {

            // 确定是同一行
            row = i;

            // USER_ID
            // 先判断dataFrame此处的user_id是否在result的index列表内
            String user_id = (String) dataFrame.get(i, 0);
            if (!(result_userid_list.contains(user_id))) {

                // 如果不存在这个user_id的话，就给他加上
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j) == null) {
                        result.set(j, 0, user_id);
                        row = j;
                        break;
                    }

                }
            } else {
                // 如果存在这个user_id,确定为同一行
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j).equals(user_id)) {
                        row = j;
                        break;
                    }
                }
            }

            String user_time = (String) dataFrame.get(i, 1);//时间获取
            //CREATE_TIME
            //遍历result表的时间，看是否有符合的，添加进去
            for (int j = 1; j < result.columns().size(); j++) {
                String result_time = (String) result.columns().toArray()[j]; //获取列名，硬转String

                // 先比较时间谁大谁小
                Calendar cal_result = Calendar.getInstance();
                cal_result.setFirstDayOfWeek(Calendar.MONDAY);
                Calendar cal_user = Calendar.getInstance();
                cal_user.setFirstDayOfWeek(Calendar.MONDAY);
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                Date result_time_d = sdf.parse(result_time);
                Date user_time_d = sdf.parse(user_time);

                cal_result.setTime(result_time_d);
                cal_user.setTime(user_time_d);


                // 比较周
                int wiy_user = cal_user.get(Calendar.DAY_OF_WEEK);
                int wiy_result = cal_result.get(Calendar.DAY_OF_WEEK);
                if ((wiy_user == 1 || wiy_user ==7) && //周日或者周六　周六是７　周日是１
                        cal_user.get(Calendar.WEEK_OF_YEAR) == cal_result.get(Calendar.WEEK_OF_YEAR)){ // 同一周，
                    // result非null，说明已经有数据已经在里面，需要加起来
                    if (result.get(row, j) != null){
                        Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                        result.set(row, j, o);
                        break;
                    } else {
                        result.set(row, j, dataFrame.get(i, 2));
                        break;
                    }
                }

            }

        }

        //扩展：添加商家的店铺名
        result.add("店铺名称");
        DataFrame<Object> dataFrame_store = Store.dataFrameOfStore();
        Map<String, Integer> index_store = DataDealer.columns_index(dataFrame_store);
        for (int i = 0; i < result.col(0).size(); i++) {
            String userId = (String) result.get(i, 0);
            if (userId == null) {
                break;
            }
            DataFrame<Object> userIdStoreName = dataFrame_store
                    .select(value -> {
                String user_id_store = (String) value.get(index_store.get("user_id"));
                        return userId.equals(user_id_store);
            })
                    .retain("user_id", "store_name");

            if (!userIdStoreName.isEmpty()) {
                if (userIdStoreName.size()>1) {
                    result.set(i, "店铺名称", userIdStoreName.get(0, 1));
                } else {
                    userIdStoreName.reindex("user_id");
                    result.set(i, "店铺名称", userIdStoreName.get(userId, "store_name"));
                }
            }
        }
        result.rename("Content", "用户ID/游戏时长（分钟）/日期（游玩大于3分钟）");
        DataDealer.writeXls(result, Constants.OUT_PUT_PATH+"商家周末游戏时长(周度）.xlsx");

        return result;
    }

    // 每款游戏的点击次数
    public static DataFrame<Object> perGamePointCounts(String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null)
            return null;

        // 读取表
        DataFrame<Object> dataFrame = dataFrameOfPlayRecord();
        DataDealer.format_time(dataFrame, "create_time", pattern);
        dataFrame = dataFrame.retain("game_name", "play_record_id", "create_time")
                .groupBy("game_name", "create_time")
                .count(); //每天玩的总点击次数

        // 分发数据 先创建一个带有时间的空dataFrame
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, dataFrame.col("game_name").size());
        result = result.transpose();

        // 遍历并把所有的东西都写进去
        List<Object> result_userid_list = result.col(0);
        // 确定是同一行
        int row = 0;
        for (int i = 0; i < dataFrame.col(0).size(); i++) {

            // 确定是同一行
            row = i;

            // USER_ID
            // 先判断dataFrame此处的user_id是否在result的index列表内
            String user_id = (String) dataFrame.get(i, 0);
            if (!(result_userid_list.contains(user_id))) {

                // 如果不存在这个user_id的话，就给他加上
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j) == null) {
                        result.set(j, 0, user_id);
                        row = j;
                        break;
                    }

                }
            } else {
                // 如果存在这个user_id,确定为同一行
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j).equals(user_id)) {
                        row = j;
                        break;
                    }
                }
            }

            String user_time = (String) dataFrame.get(i, 1);//时间获取
            //CREATE_TIME
            //遍历result表的时间，看是否有符合的，添加进去
            for (int j = 1; j < result.columns().size(); j++) {
                String result_time = (String) result.columns().toArray()[j]; //获取列名，硬转String

                // 先比较时间谁大谁小
                Calendar cal_result = Calendar.getInstance();
                cal_result.setFirstDayOfWeek(Calendar.MONDAY);
                Calendar cal_user = Calendar.getInstance();
                cal_user.setFirstDayOfWeek(Calendar.MONDAY);
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                Date result_time_d = sdf.parse(result_time);
                Date user_time_d = sdf.parse(user_time);

                cal_result.setTime(result_time_d);
                cal_user.setTime(user_time_d);

                // 分开 日-月比较和周比较
                if (pattern.length() != 13) {

                    // 如果子表的时间，比结果表的时间还早，那就没必要继续遍历了
                    if (cal_user.before(cal_result)) {
                        break;
                    } else if (cal_user.equals(cal_result)){
                        // 判断两个时间是否相等
                        result.set(row, j, dataFrame.get(i, 2));
                        break;
                    }
                } else {
                    // 比较周
                    int wiy_user = cal_user.get(Calendar.WEEK_OF_YEAR);
                    int wiy_result = cal_result.get(Calendar.WEEK_OF_YEAR);
                    if (wiy_user == wiy_result){
                        // result非null，说明已经有数据已经在里面，需要加起来
                        if (result.get(row, j) != null){
                            Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                            result.set(row, j, o);
                            break;
                        } else {
                            result.set(row, j, dataFrame.get(i, 2));
                            break;
                        }
                    }
                }
            }


        }


        if (pattern.length() == 7) {
            result.rename("Content", "游戏名/次数/日期");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "每款游戏的使用次数(日度）.xlsx");
        }
        else if (pattern.length() == 10) {
            result.rename("Content", "游戏名/次数/日期");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "每款游戏的使用次数(月度）.xlsx");
        }
        else if (pattern.length() == 13) {
            result.rename("Content", "游戏名/次数/日期");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "每款游戏的使用次数(周度）.xlsx");
        }

        return result;
    }

    // 每款游戏的游戏时长
    public static DataFrame<Object> perGamePlayTimeSum(String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null)
            return null;

        // 读取表
        DataFrame<Object> dataFrame = dataFrameOfPlayRecord();
        DataDealer.format_time(dataFrame, "create_time", pattern);
        dataFrame = dataFrame.retain("game_name", "play_minutes", "create_time")
                .groupBy("game_name", "create_time")
                .sum(); //每天玩的总点击次数

        // 分发数据 先创建一个带有时间的空dataFrame
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, dataFrame.col("game_name").size());
        result = result.transpose();

        // 遍历并把所有的东西都写进去
        List<Object> result_userid_list = result.col(0);
        // 确定是同一行
        int row = 0;
        for (int i = 0; i < dataFrame.col(0).size(); i++) {

            // 确定是同一行
            row = i;

            // USER_ID
            // 先判断dataFrame此处的user_id是否在result的index列表内
            String user_id = (String) dataFrame.get(i, 0);
            if (!(result_userid_list.contains(user_id))) {

                // 如果不存在这个user_id的话，就给他加上
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j) == null) {
                        result.set(j, 0, user_id);
                        row = j;
                        break;
                    }

                }
            } else {
                // 如果存在这个user_id,确定为同一行
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j).equals(user_id)) {
                        row = j;
                        break;
                    }
                }
            }

            String user_time = (String) dataFrame.get(i, 1);//时间获取
            //CREATE_TIME
            //遍历result表的时间，看是否有符合的，添加进去
            for (int j = 1; j < result.columns().size(); j++) {
                String result_time = (String) result.columns().toArray()[j]; //获取列名，硬转String

                // 先比较时间谁大谁小
                Calendar cal_result = Calendar.getInstance();
                cal_result.setFirstDayOfWeek(Calendar.MONDAY);
                Calendar cal_user = Calendar.getInstance();
                cal_user.setFirstDayOfWeek(Calendar.MONDAY);
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                Date result_time_d = sdf.parse(result_time);
                Date user_time_d = sdf.parse(user_time);

                cal_result.setTime(result_time_d);
                cal_user.setTime(user_time_d);

                // 分开 日-月比较和周比较
                if (pattern.length() != 13) {

                    // 如果子表的时间，比结果表的时间还早，那就没必要继续遍历了
                    if (cal_user.before(cal_result)) {
                        break;
                    } else if (cal_user.equals(cal_result)){
                        // 判断两个时间是否相等
                        result.set(row, j, dataFrame.get(i, 2));
                        break;
                    }
                } else {
                    // 比较周
                    int wiy_user = cal_user.get(Calendar.WEEK_OF_YEAR);
                    int wiy_result = cal_result.get(Calendar.WEEK_OF_YEAR);
                    if (wiy_user == wiy_result){
                        // result非null，说明已经有数据已经在里面，需要加起来
                        if (result.get(row, j) != null){
                            Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                            result.set(row, j, o);
                            break;
                        } else {
                            result.set(row, j, dataFrame.get(i, 2));
                            break;
                        }
                    }
                }
            }


        }



        if (pattern.length() == 7) {
            result.rename("Content", "游戏名/游戏时长（分钟）/日期");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "每款游戏使用时长(日度）.xlsx");
        }
        else if (pattern.length() == 10) {
            result.rename("Content", "游戏名/游戏时长（分钟）/日期");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "每款游戏使用时长(月度）.xlsx");
        }
        else if (pattern.length() == 13) {
            result.rename("Content", "游戏名/游戏时长（分钟）/日期");
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "每款游戏使用时长(周度）.xlsx");
        }


        return result;
    }

    // 游戏维度 - 游戏周留存
    public static DataFrame<Object> perGameLiuCun(String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null || pattern.length() != 13)
            return null;

        // 读取表
        DataFrame<Object> dataFrame = dataFrameOfPlayRecord();
        dataFrame = Store.returnIsStoreDataFrame(dataFrame);// 筛选出商家表
        if (dataFrame.isEmpty()) {
            System.out.println("无商家");
        }
        DataDealer.format_time(dataFrame, "create_time", pattern);
        dataFrame = dataFrame.retain("game_name", "user_id", "create_time")
                .groupBy("game_name", "create_time")
                .count(); //每周玩的用户数

        // 获得result 表就是每款游戏，每周玩的用户数
        DataFrame<Object> result = DataDealer.getCountsResultDataFrame(dataFrame, fromDate, toDate, "game_name", 0);

        // 逻辑：
            // 从result表入手，直接从第二周开始计算
        DataFrame<Object> raw = dataFrameOfPlayRecord();
        DataFrame<Object> sub_dataFrame = raw.retain("play_record_id", "game_name").groupBy("game_name").count();

        // 读取表
        DataFrame<Object> dataFrame_pr = dataFrameOfPlayRecord();
        DataDealer.format_time(dataFrame_pr, "create_time", pattern);
        Map<String, Integer> sim_0 = DataDealer.columns_index(dataFrame_pr);
        dataFrame_pr = dataFrame_pr.select(value -> {
            return (double) value.get(sim_0.get("play_minutes")) > 3.0;
        });// 筛选超过3分钟的比
        dataFrame_pr = dataFrame_pr.retain("game_name", "user_id", "create_time", "play_record_id")
                .groupBy("user_id", "game_name", "create_time")
                .count()
        ;

        dataFrame_pr = Store.returnIsStoreDataFrame(dataFrame_pr);// 筛选出商家表
        DataFrame<Object> dataFrame_game = dataFrame_pr.retain("game_name", "play_record_id").groupBy("game_name").count();


//        // test
//        DataFrame<Object> liu_cun1 = DataDealer.createLiuCunTable(fromDate, toDate, "庄园惊魂");
//        DataFrame<Object> liu_cun_result1 = DataDealer.liuCunWriteInMethodForGame(dataFrame_pr, liu_cun1, fromDate, toDate, "庄园惊魂"); // 使用次方法获得游戏留存
//        liu_cun_result1.writeXls("dddddddddd.xlsx");
//        // test



        for (Object game_name : dataFrame_game.col("game_name")) {
            System.out.println("正在输出 " + (String) game_name + " 的留存数据 ~");
            DataFrame<Object> liu_cun = DataDealer.createLiuCunTable(fromDate, toDate, (String) game_name);
            DataFrame<Object> liu_cun_result = DataDealer.liuCunWriteInMethodForGame(dataFrame_pr, liu_cun, fromDate, toDate, (String) game_name); // 使用次方法获得游戏留存

            // 控制乱码
            if (((String)game_name).contains("爆裂球拍")) {
                liu_cun_result.rename("Content", "游戏名/游戏时长（分钟）/日期");
                DataDealer.writeXls(liu_cun_result, Constants.OUT_PUT_PATH + "game\\" + "爆裂球拍 - 周留存.xlsx");
            } else {
                liu_cun_result.rename("Content", "游戏名/游戏时长（分钟）/日期");
                DataDealer.writeXls(liu_cun_result, Constants.OUT_PUT_PATH + "game\\" + game_name+" - 周留存.xlsx");
            }
        }

        return null;
    }

    // 游戏维度 - 游戏周活跃商家以及占比
    public static DataFrame<Object> perGameHuoYueAndZhanBi(String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern.length() != 13){
            return null;
        }

        // 筛选大于3分钟的商家原始记录【user_is未去重】
        DataFrame<Object> playRecord = dataFrameOfPlayRecord();
        Map<String, Integer> sim_0 = DataDealer.columns_index(playRecord);
        DataFrame<Object> dataFrame_huoYue = Store.returnIsStoreDataFrame(playRecord); // 去除非商家
        dataFrame_huoYue = dataFrame_huoYue.select(value -> {
            return (double) value.get(sim_0.get("play_minutes")) > 3.0; // 选择游玩大于3分钟的玩家
        });
        DataDealer.format_time(dataFrame_huoYue, "create_time", pattern);
        dataFrame_huoYue = dataFrame_huoYue.retain("game_name", "create_time", "user_id")
                .groupBy("create_time", "game_name")
        ;

        // 获得游戏的list
        DataFrame<Object> dataFrame_gameList = playRecord.retain("game_name", "play_record_id");
        Map<String, Integer> sim_gameList = DataDealer.columns_index(dataFrame_huoYue);
        dataFrame_gameList = dataFrame_gameList.select(value -> {
                    String game_name = (String) value.get(sim_gameList.get("game_name"));
                    return !game_name.contains("购买");  // 去除购买的优惠卡记录
                })
                .groupBy("game_name")
                .count();

        // 分发数据，先创建一个带有时间的空dataFrame
        List<Object> gameList = dataFrame_gameList.col("game_name");
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, dataFrame_gameList.col("game_name").size()*2);
        result = result.transpose();

        // 装填基础列表
        int row_count = 0;
        for (Object game : gameList) {
            result.set(row_count, 0, game);
            row_count++;
            result.set(row_count, 0, "占总商家%");
            row_count++;
        }

        // 计算活跃
        DataFrame<Object> huoYue_result = DataDealer.perGameHuoYueAndZhanBiWriteInMethod(dataFrame_huoYue, result, fromDate, toDate);

        // 输出
        if (pattern.length() == 13) {
            huoYue_result.rename("Content", "游戏名/占比/日期");
            DataDealer.writeXls(huoYue_result, Constants.OUT_PUT_PATH + "游戏维度-活跃游戏（周度）占比.xlsx");
        }


        return null;
    }

    // 游戏维度 - 游戏记录
    public static DataFrame<Object> playRecord(String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        DataFrame<Object> raw_user = User.raw_dataFrameOfUser(); // 这里选择所有的user，不管三七二十一一定要给他匹配上
        DataFrame<Object> store = Store.dataFrameOfStore();
        DataFrame<Object> playRecord = dataFrameOfPlayRecord();


        Map<String, Integer> stringIntegerMap_pr = DataDealer.columns_index(playRecord);
        Map<String, Integer> stringIntegerMap_raw_user = DataDealer.columns_index(raw_user);
        Map<String, Integer> stringIntegerMap_st = DataDealer.columns_index(store);
//        Store.returnIsStoreDataFrame(playRecord);

        // 时间处理参数
        SimpleDateFormat simpleDateFormat_00 = new SimpleDateFormat(pattern);
        SimpleDateFormat simpleDateFormat_01 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat simpleDateFormat_02 = new SimpleDateFormat("HH:mm:ss");

        Calendar cal_start = Calendar.getInstance();
        Calendar cal_end = Calendar.getInstance();
        Calendar cal_create = Calendar.getInstance();
        Calendar cal_from = Calendar.getInstance();
        Calendar cal_to = Calendar.getInstance();

        cal_start.setFirstDayOfWeek(Calendar.MONDAY);
        cal_end.setFirstDayOfWeek(Calendar.MONDAY);
        cal_create.setFirstDayOfWeek(Calendar.MONDAY);
        cal_from.setFirstDayOfWeek(Calendar.MONDAY);
        cal_to.setFirstDayOfWeek(Calendar.MONDAY);

        cal_from.setTime(simpleDateFormat_00.parse(fromDate));
        cal_to.setTime(simpleDateFormat_00.parse(toDate));

        DataFrame<Object> base = new DataFrame<>("user_id", "账号名", "店铺名",	"省份", "城市", "区",	"详细地址", "游戏名", "开始日期", "开始时间",	"结束日期", "结束时间","付费额","免扣额", "游戏时长", "type");
        Map<String, Integer> stringIntegerMap_base = DataDealer.columns_index(base);

        // 遍历整个playRecord表
        int row = 0;
        for (List<Object> pr : playRecord) {

            String user_id = (String) pr.get(stringIntegerMap_pr.get("user_id"));
            // 时间比较
            Date date_start = (Date) pr.get(stringIntegerMap_pr.get("play_start_time"));
            Date date_end = (Date) pr.get(stringIntegerMap_pr.get("play_finish_time"));
            Date date_create = (Date) pr.get(stringIntegerMap_pr.get("create_time"));

            cal_start.setTime(date_start);
            cal_end.setTime(date_end);
            cal_create.setTime(date_create);

            boolean c1 = cal_create.after(cal_from);
            boolean c2 = cal_create.before(cal_to);

            // 如果记录时间在指定范围内
            if (c1 && c2) {
                String game_name = (String) pr.get(stringIntegerMap_pr.get("game_name"));
                String type = (String) pr.get(stringIntegerMap_pr.get("type"));

                String start_date = simpleDateFormat_01.format(date_start);
                String end_date = simpleDateFormat_01.format(date_end);;

                String start_time = simpleDateFormat_02.format(date_start);
                String end_time = simpleDateFormat_02.format(date_end);

                Object play_minute = (Object) pr.get(stringIntegerMap_pr.get("play_minutes"));
                Object pay_amount = (Object) pr.get(stringIntegerMap_pr.get("pay_amount"));
                Object pay_discount = (Object) pr.get(stringIntegerMap_pr.get("pay_discount"));

                Object[] s = {user_id, null, null, null, null, null, null, game_name, start_date, start_time, end_date, end_time,pay_amount,pay_discount, play_minute, type};

                base.append(row, s);

                // 添加店铺信息
                boolean isStore = false;
                for (List<Object> objectList : store) {

                    String user_id_inStore = (String) objectList.get(stringIntegerMap_st.get("user_id"));

                    // 如果用户名相等，说明userid在这个表里
                    if (user_id.equals(user_id_inStore)) {

                        // 找出所有数据
                        String username = (String) objectList.get(stringIntegerMap_st.get("username"));
                        String store_name = (String) objectList.get(stringIntegerMap_st.get("store_name"));
                        String store_address = (String) objectList.get(stringIntegerMap_st.get("store_address"));

                        String[] split = store_address.split("/");
                        String a1 = split[0];
                        String a2 = split[1];
                        String a3 = split[2];
                        String a4 = split[3];

                        base.set(row, stringIntegerMap_base.get("账号名"), username);
                        base.set(row, stringIntegerMap_base.get("店铺名"), store_name);
                        base.set(row, stringIntegerMap_base.get("省份"), a1);
                        base.set(row, stringIntegerMap_base.get("城市"), a2);
                        base.set(row, stringIntegerMap_base.get("区"), a3);
                        base.set(row, stringIntegerMap_base.get("详细地址"), a4);
                        isStore = true;
                        break;
                    }
//                    else {// 如果不在商家表里面就只能另外插入了
//                        DataFrame<Object> user_id1 = raw_user.select(value -> {
//                            String raw_user_id = (String) value.get(stringIntegerMap_raw_user.get("user_id"));
//                            return user_id.equals(raw_user_id);
//                        }).head(1);
//
//                        List<Object> row1 = user_id1.row(0);
//                        String mobile_no = (String) row1.get(stringIntegerMap_raw_user.get("mobile_no"));
//                        String email = (String) row1.get(stringIntegerMap_raw_user.get("email"));
//                        String combine = "联系方式：( " + mobile_no + " " +  email + " )";
//                        base.set(row, stringIntegerMap_base.get("账号名"), combine);
//                    }
                }

                // 如果店铺信息那里没找到，去user表里面找
                if (!isStore) {
                    for (List<Object> objectList : raw_user) {

                        String user_id_raw_user = (String) objectList.get(stringIntegerMap_raw_user.get("user_id"));
                        // 如果用户名相等，说明userid在这个表里
                        if (user_id.equals(user_id_raw_user)) {

                            // 找出所有数据

                            String mobile_no = (String) objectList.get(stringIntegerMap_raw_user.get("mobile_no"));
                            String email = (String) objectList.get(stringIntegerMap_raw_user.get("email"));


                            String combine = "联系方式：( " + mobile_no + " " +  email + " )";
                            base.set(row, stringIntegerMap_base.get("账号名"), combine);
                            break;
                        }
                    }

                }
                row++;
            }
        }

        // checy 钦赐 去掉个人，测试，员工
        String[] unnormal_user_account_list = {"web_qa",
                                                "ad_nsqa",
                                                "mtlvr",
                                                "checychen0509@163.com",
                                                "1829102306918691989860abc1257692123@163.com",
                                                "c549427987@163.com",
                                                "15000045855",
                                                "aaronxuedu@163.com",
                                                "leefound@163.com",
                                                "oliviazkq@163.com",
                                                "zzzwt0001@163.com",
                                                "netviosvr@163.com",
                                                "18256403240",
                                                "13416361007",
                                                "18686843274",
                                                "zaowuworld",
                                                "18291023069",
                                                "18565658129",
                                                "13480945722"};

        List<String> strings = Arrays.asList(Constants.unnormal_user_account_list);
        ArrayList<String> unnormal_user_account_arraylist = new ArrayList<>(strings);

        System.out.println(stringIntegerMap_base);

        base = base.select(value -> {
            String account = (String) value.get(stringIntegerMap_base.get("账号名"));
            String type = (String) value.get(stringIntegerMap_base.get("type"));

            boolean b1 = !unnormal_user_account_arraylist.contains(account);//个人账号
            boolean b2 = !(account.startsWith("web_qa") || account.startsWith("ad_nsqa") || account.startsWith("mtlvr")); //测试账号
            boolean b3 = !type.equals("ssvcPlay");//非自助机

            return b1 && b2 && b3;
        });

        DataDealer.writeXls(base, Constants.OUT_PUT_PATH + "游戏记录.xlsx");
        return base;
    }

    // 返回每个月的消耗影币额
    public static DataFrame<Object> everyDayYingBiUse(String fromdate, String toDate) throws SQLException, IOException {
        String sql = "SELECT\n" +
                "    sum(pr.pay_amount) AS `影币`,\n" +
                "    DAY(pr.create_time) as `日`\n" +
                "FROM\n" +
                "    play_record pr\n" +
                "where create_time>'"+ fromdate+" 00:00:00'\n" +
                "  and create_time<'"+ toDate+"  00:00:00'\n" +
                "GROUP BY DAY(pr.create_time);";

        DataFrame<Object> dataFrame = DataFrame.readSql(BaseDao.getConnection(), sql);

        DataDealer.writeXls(dataFrame, Constants.OUT_PUT_PATH + "每个月的消耗影币额.xlsx");

        return dataFrame;
    }

    public static void main(String[] args) throws IOException, ParseException {
//        perAccountPlayRecordDayCounts("2020-11-01 00", "2021-03-09 00", true); //商家
//        perAccountPlayRecordDayCounts("2020-11-01", "2021-03-09", false); //非商家
//        perStorePlayRecordWeekendPlayTimeSum("2020-11-01 00", "2021-03-09 00");
//        perGamePointCounts("2020-11-01", "2021-03-09");
//        perGamePlayTimeSum("2020-11-01", "2021-03-09");
//        perGameLiuCun("2020-11-01 00", "2021-03-09 00");
//        perAccountPlayRecordDayCounts("2020-11", "2021-03", true);
        perGameLiuCun("2020-11-01 00", "2021-03-01 00");


    }
}
