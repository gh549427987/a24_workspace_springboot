package com.jhua.event;

import com.jhua.Singleton.UserSingleton;
import com.jhua.constants.Constants;
import com.jhua.utils.DataDealer;
import joinery.DataFrame;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.jhua.utils.DataDealer.liuCunWriteInMethodForPlatform;

public class User {

    public static String s1 = "注册账号数（新增）";
    public static String s2 = "注册账号数（累计）";
    public static String s3 = "覆盖商家数-定义：填写商家信息账号数（新增）";
    public static String s4 = "覆盖商家数-定义：填写商家信息账号数（累计）";

    public static DataFrame<Object> dataFrameOfUser() throws IOException {
        return UserSingleton.dataFrameOfUser();
    }

    public static DataFrame<Object> raw_dataFrameOfUser() throws IOException {
        return UserSingleton.raw_dataFrameOfUser();
    }

    //每月注册
    public static DataFrame<Object> new_user_registers_month(String pattern) throws IOException, ParseException {

        // 读取表
        DataFrame<Object> user = dataFrameOfUser();

        user = user.retain("user_id", "create_time");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = simpleDateFormat.parse( "2020-07-06 00:00:00" );
        DataFrame<Object> select = user.select(value -> {
            Date date = (Date) value.get(1);

            return date.after(date2);
        });
        DataDealer.writeXls(select, Constants.DESKTOP + "select.xlsx");


        // 先重新格式化所有时间为月度
        DataDealer.format_time(user, "create_time", pattern);

        user = user.groupBy("create_time").count();

        // 重命名列名
        user = user.rename("user_id", "注册账号数（新增）");
//        user.writeXls(Constants.OUT_PUT_PATH+"\\注册账号数（新增）.xlsx");

        return user;
    }

    //所有注册
    public static DataFrame<Object> all_user_registers_month(String pattern) throws IOException, ParseException {

        // 用已有函数获取每月的注册量，稍后进行累加
        DataFrame<Object> user = new_user_registers_month(pattern);

        // 不断累加所有月份注册的比
        int count = 0;
        int row = 0;
        ListIterator<List<Object>> iterrows = user.iterrows();
        while (iterrows.hasNext()) {
            List<Object> next = iterrows.next();
            count += (int) next.get(1);
            user.set(row, 1, count);
            row++;
        }

        // 重命名列名
        user = user.rename("注册账号数（新增）", "注册账号数（累计）");


        return user;
    }

    // 平台维度 - 用户留存
    public static DataFrame<Object> perPlatformLiuCun(String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);

        // 读取表
        DataFrame<Object> dataFrame = dataFrameOfUser();
//        dataFrame = Store.returnIsStoreDataFrame(dataFrame);// 筛选出商家表
        if (dataFrame.isEmpty()) {
            System.out.println("用户");
            return null;
        }
        DataDealer.format_time(dataFrame, "create_time", pattern);
        dataFrame = dataFrame.retain("user_id", "create_time");

        DataFrame<Object> dataFrame_xinZengShuLiang = dataFrame  //新增用户数量
                .retain("user_id", "create_time")
                .groupBy("create_time")
        ;


        DataFrame<Object> result = DataDealer.createLiuCunTable(fromDate, toDate, "Platform");

        DataFrame<Object> liu_cun_result = liuCunWriteInMethodForPlatform(dataFrame_xinZengShuLiang, result, fromDate, toDate);

        if (pattern.length() == 7) {
            liu_cun_result.rename("Content", "日期（注册人数）/占比/日期（游玩时长大于3分钟的人数)");
            DataDealer.writeXls(liu_cun_result, Constants.OUT_PUT_PATH + "平台(月)留存（判断标准是当日游戏时长大于3分钟）.xlsx");
        }
        else if (pattern.length() == 10) {
            liu_cun_result.rename("Content", "日期（注册人数）/占比/日期（游玩时长大于3分钟的人数)");
            DataDealer.writeXls(liu_cun_result,Constants.OUT_PUT_PATH + "平台(日)留存（判断标准是当日游戏时长大于3分钟）.xlsx");
        }
        else if (pattern.length() == 13) {
            liu_cun_result.rename("Content", "日期（注册人数）/占比/日期（游玩时长大于3分钟的人数)");
            DataDealer.writeXls(liu_cun_result,Constants.OUT_PUT_PATH + "平台(周)留存（判断标准是当日游戏时长大于3分钟）.xlsx");
        }

        return liu_cun_result;
    }

    // 汇入主表
    private static void toPlatformTable(DataFrame platform, DataFrame<Object> subDataFrame, String indexName) {
        /*
         * @Author liu-miss
         * @Description //TODO 把每个子表通过统一的方式写入主表中
         * @Date  2021/3/16
         * @Param [dataFrame, indexName]
         * @return void
         **/

        //遍历，获取指定indexName的具体行索引
        int index_of_s = 0;
        for (int i = 0; i < platform.col(0).size(); i++) {
            if (indexName.equals((String) platform.get(i, 0))){
                index_of_s = i;
            }
        }

        //遍历，逐个插入
        for (int i = 0; i < subDataFrame.index().size(); i++) {
            for (int j = 0; j < platform.columns().size(); j++) {

                String sub_index = (String)(subDataFrame.index().toArray()[i]);
                String maj_index = (String)(platform.columns().toArray()[j]);

                if (sub_index.equals(maj_index)) {
                    Object value = subDataFrame.get(sub_index, indexName);
                    platform.set(index_of_s, maj_index, value);
                }
            }
        }

    }

    //用户和商家账号注册数统计完成
    public static void gatherPlatform(String fromDate, String toDate) throws ParseException, IOException {
        /*
         * @Author liu-miss
         * @Description //TODO 生成平台表，需要输入起始日期和结束日期
         * @Date  2021/3/16
         * @Param [fromDate, toDate]
         * @return void
         **/

        //校验时间,并给出正确的pattern;
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null)
            return;// 不正确就直接扔回去

        // 创建新的时间表，并对表进行一定的修改
        DataFrame<Object> platform = DataDealer.format_time_dataframe(fromDate, toDate, 4);
        assert platform != null;

        platform.set(0, 0, s1);
        platform.set(0, 1, s2);
        platform.set(0, 2, s3);
        platform.set(0, 3, s4);


        platform = platform.transpose();// 转置

        // 生成所有的子表
        DataFrame<Object> new_user_registers_month = DataDealer.reduceCreateTime(User.new_user_registers_month(pattern));
        DataFrame<Object> all_user_registers_month = DataDealer.reduceCreateTime(User.all_user_registers_month(pattern));
        DataFrame<Object>  new_store_registers_month = DataDealer.reduceCreateTime(Store.new_store_registers_month(pattern));
        DataFrame<Object> all_store_registers_month = DataDealer.reduceCreateTime(Store.all_store_registers_month(pattern));

        //汇入主表
        toPlatformTable(platform, new_user_registers_month, s1);
        toPlatformTable(platform, all_user_registers_month, s2);
        toPlatformTable(platform, new_store_registers_month, s3);
        toPlatformTable(platform, all_store_registers_month, s4);

        platform.rename("Content", "数据类/账号数/日期");
        DataDealer.writeXls(platform, Constants.OUT_PUT_PATH+"用户与商家账号注册.xlsx");
    }


    public static void main(String[] args) throws IOException, ParseException {
        new_user_registers_month("yyyy-MM");
    }
}

