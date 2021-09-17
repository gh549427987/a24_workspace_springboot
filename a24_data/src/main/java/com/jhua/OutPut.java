package com.jhua;

import com.jhua.constants.Constants;
import com.jhua.dao.base.BaseDao;
import com.jhua.utils.DataDealer;
import com.jhua.event.PlayRecord;
import com.jhua.event.RechargeHistory;
import com.jhua.event.User;
import com.jhua.utils.ZipUtil;
import joinery.DataFrame;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import static com.jhua.event.User.gatherPlatform;

public class OutPut {

    private static String onLineDateMon = "2020-07";
    private static String onLineDateWeek = "2020-07-06 00";

    private static String fromDate = null;
    private static String toDate = null;

    private static String fromDate_day = null;
    private static String fromDate_mon = null;
    private static String fromDate_week = null;
    private static String toDate_mon = null;
    private static String toDate_day = null;
    private static String toDate_week = null;

    private static boolean flag = false; // 标识时间位数是否足够
    private static boolean output_platform_flag = false; // 标识时间位数是否足够
    private static boolean output_store_flag = false; // 标识时间位数是否足够
    private static boolean output_game_flag = false; // 标识时间位数是否足够

    public OutPut(String fromDate, String toDate) {

        if (fromDate.length() == 13 && toDate.length() == 13){
            flag = true;
            OutPut.fromDate = fromDate;
            OutPut.toDate = toDate;
            fromDate_day = fromDate.substring(0, 10);
            fromDate_mon = fromDate.substring(0, 7);
            fromDate_week = fromDate;
            toDate_mon = toDate.substring(0, 7);
            toDate_day = toDate.substring(0, 10);
            toDate_week = toDate;
        } else {
            flag = false;
            System.out.println("请输入13位的时间(如'2021-01-01 00')：");
        }

    }

    // 平台维度
    public void output_platform(int n) throws IOException, ParseException, SQLException {

        if (n == 1) {
            // 平台维度 - 注册数
            System.out.println("正在执行 >>>>>> 平台维度 - 注册数");
            System.out.println("月度 >>>");
            gatherPlatform(fromDate_mon, toDate_mon);
        } else if (n == 2) {
            // 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数
            System.out.println("正在执行 >>>>>> 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数");
            System.out.println("日度-商家 >>>");
            PlayRecord.perAccountPlayRecordDayCounts(fromDate_day, toDate_day, true);
        } else if (n == 3) {
            System.out.println("正在执行 >>>>>> 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数");
            System.out.println("周度-商家 >>>");
            PlayRecord.perAccountPlayRecordDayCounts(fromDate_week, toDate_week, true);
        } else if (n == 4) {
            System.out.println("正在执行 >>>>>> 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数");
            System.out.println("月度-商家 >>>");
            PlayRecord.perAccountPlayRecordDayCounts(fromDate_mon, toDate_mon, true);
        } else if (n == 5) {
            System.out.println("正在执行 >>>>>> 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数");
            System.out.println("日度-账号 >>>");
            PlayRecord.perAccountPlayRecordDayCounts(fromDate_day, toDate_day, false);
        } else if (n == 6) {
            System.out.println("正在执行 >>>>>> 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数");
            System.out.println("周度-账号 >>>");
            PlayRecord.perAccountPlayRecordDayCounts(fromDate_week, toDate_week, false);
        } else if (n == 7) {
            System.out.println("正在执行 >>>>>> 平台维度 - 日度 月度 周度 平台日活跃商家数-定义：单日游戏时长大于3分钟的账号数");
            System.out.println("月度-账号 >>>");
            PlayRecord.perAccountPlayRecordDayCounts(fromDate_mon, toDate_mon, false);
        } else if (n == 8) {
            System.out.println("正在执行 >>>>>> 平台维度 - 月度 周度 平台留存 ——》 【定义】某个日期新创建的用户，在其他日期下有游戏记录的（无条件筛选，只要产生过游戏记录就统计进来）");
            System.out.println("月度 >>>");
            User.perPlatformLiuCun(onLineDateMon, toDate_mon);
        } else if (n == 9) {
            System.out.println("正在执行 >>>>>> 平台维度 - 月度 周度 平台留存 ——》 【定义】某个日期新创建的用户，在其他日期下有游戏记录的（无条件筛选，只要产生过游戏记录就统计进来）");
            System.out.println("周度 >>>");
            User.perPlatformLiuCun(onLineDateWeek, toDate_week);
        } else if (n == 10) {
            System.out.println("正在执行 >>>>>> 平台维度 - 平台日付费-定义：付费商家付费额");
            RechargeHistory.perDayMaxPayAmountAndStore(fromDate_day, toDate_day);
        } else if (n == 11) {
            System.out.println("正在执行 >>>>>> 平台维度 - 该月每天付费影币额");
            PlayRecord.everyDayYingBiUse(fromDate_day, toDate_day);
        } else {
            output_platform_flag = true;
        }
    }

    // 商家维度
    public void output_store(int n) throws IOException, ParseException {
        if (n == 1) {
            // 商家维度 - 商家指定时间内的消费情况 不按周
            System.out.println("正在执行 >>>>>> 商家维度 - 商家指定时间内的消费情况 不按周");
            RechargeHistory.storePointTimeConsume(fromDate_day, toDate_day);
            System.out.println("正在执行 >>>>>> 所购卡的信息-店铺信息");
            discount_store();
            device_total();
        } else {
            output_store_flag = true;
        }
    }

    // 游戏维度
    public void output_game(int n) throws IOException, ParseException {
        if (n == 1) {
            //             游戏维度 - 每款游戏的周留存
            System.out.println("正在执行 >>>>>> 游戏维度 - 每款游戏的周留存");
            PlayRecord.perGameLiuCun(fromDate_week, toDate_week);
        } else if (n == 2) {
            // 平台维度 - 平台日付费峰值-定义：最高付费商家付费额
            System.out.println("正在执行 >>>>>> 平台维度 - 平台日付费-定义：付费商家付费额");
            RechargeHistory.perDayMaxPayAmountAndStore(fromDate_day, toDate_day);
        } else if (n == 3) {
            // 游戏维度 - 商家周末游戏时长 按周
            System.out.println("正在执行 >>>>>> 游戏维度 - 商家周末游戏时长 按周");
            PlayRecord.perStorePlayRecordWeekendPlayTimeSum(fromDate_week, toDate_week);
        } else if (n == 4) {
            // 游戏维度 - 每款游戏的次数
            System.out.println("正在执行 >>>>>> 游戏维度 - 每款游戏的次数");
            PlayRecord.perGamePointCounts(fromDate_day, toDate_day);
        } else if (n == 5) {
            // 游戏维度 - 每款游戏的使用时长
            System.out.println("正在执行 >>>>>> 游戏维度 - 每款游戏的使用时长");
            PlayRecord.perGamePlayTimeSum(fromDate_day, toDate_day);
        } else if (n == 6) {
            // 游戏维度 - 游戏周活跃商家以及占比
            System.out.println("正在执行 >>>>>> 游戏维度 - 游戏周活跃商家以及占比");
            PlayRecord.perGameHuoYueAndZhanBi(fromDate_week, toDate_week);
        } else if (n == 7) {
            // 游戏维度 - 游戏记录
            System.out.println("正在执行 >>>>>> 游戏维度 - 游戏记录");
            PlayRecord.playRecord(fromDate, toDate);
        } else if (n == 8) {
            // 游戏维度 - 每款游戏的使用时长
            System.out.println("正在执行 >>>>>> 游戏维度 - 每款游戏的使用时长");
            PlayRecord.perGamePlayTimeSum(fromDate, toDate);
        } else {
            output_game_flag = true;
        }
    }

    //计算设备数的表
    public void device_total() throws IOException, ParseException {
        Connection connection = BaseDao.getConnection();
        try {
            ResultSet execute = null;
            execute = BaseDao.execute(connection, Constants.device_total, null);
            DataFrame result = DataFrame.readSql(execute);
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "用户和设备对应关系-所有设备的数量.xlsx");
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    //所购卡的信息-店铺信息 临时搬过来，之后搬回去
    public void discount_store() throws IOException, ParseException {
        Connection connection = BaseDao.getConnection();
        try {
            ResultSet execute = null;
            execute = BaseDao.execute(connection, Constants.discount_store_info, null);
            DataFrame result = DataFrame.readSql(execute);
            DataDealer.writeXls(result, Constants.OUT_PUT_PATH + "所购卡的信息-店铺信息.xlsx");
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

        //输出全部
    public void outPutAll() throws ParseException, IOException, SQLException {

        //输出大型源数据
        PlayRecord.dataFrameOfPlayRecord(fromDate, toDate);
        RechargeHistory.dataFrameOfRechargeHistory(fromDate, toDate);

        //输出主要数据
        if (flag){
            for (int i = 1; i < 100; i++) {
                if (!output_platform_flag) {
                    output_platform(i);
                }
                if (!output_store_flag) {
                    output_store(i);
                }
                if (!output_game_flag) {

                    if (i == 1) {//跳过输出周留存，太TM长了
                        continue;
                    }
                    output_game(i);
                }
            }
        } else {
            System.out.println("时间输入有误！");
        }

    }

    public void outPutAll(String file) throws ParseException, IOException, SQLException {
        outPutAll();
        ZipUtil.compress("C:\\Users\\wb.xiejiehua\\IdeaProjects\\a24_workspace_springboot\\a24_data\\src\\main\\output", "C:\\Users\\wb.xiejiehua\\IdeaProjects\\a24_workspace_springboot\\a24_data\\src\\main\\" + file + ".zip");
    }

    public static void main(String[] args) throws IOException, ParseException, SQLException {

//        OutPut outPut = new OutPut("2021-01-01 00", "2021-02-01 00");
//        outPut.outPutAll("一月");
//        OutPut outPut1 = new OutPut("2021-02-01 00", "2021-03-01 00");
//        outPut1.outPutAll("二月");
        OutPut outPut2 = new OutPut("2020-07-22 00", "2021-09-06 00");
        outPut2.output_platform(8);

    }

    @Test
    public void test() throws IOException, ParseException, SQLException {
        OutPut outPut = new OutPut("2020-07-06 00", "2021-07-01 00");
        outPut.output_platform(4);
    }
}
