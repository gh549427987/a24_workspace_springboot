package com.jhua.Singleton;

import com.jhua.constants.Constants;
import com.jhua.dao.base.BaseDao;
import com.jhua.utils.DataDealer;
import com.jhua.utils.SingletonUtil;
import com.jhua.utils.UserOperationUtil;
import joinery.DataFrame;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayRecordSingleton {

    private static PlayRecordSingleton instance = null;
    private static boolean hasOutPutXlsx = false;

    private static DataFrame<Object> raw_dataFrameOfPlayRecord = null;
    private static DataFrame<Object> dataFrameOfPlayRecord = null;

    private static String start;
    private static String end;

    private PlayRecordSingleton() throws SQLException {

        //单例模式
        Connection connection = BaseDao.getConnection();
        try {
            ResultSet execute = null;
            String sql = SingletonUtil.sqlDate(Constants.playRecord_SelectAll, start, end);
            System.out.println("单例模式-PlayRecordSingleton：" + sql);
            execute = BaseDao.execute(connection, sql, null);
            raw_dataFrameOfPlayRecord = DataFrame.readSql(execute);
            raw_dataFrameOfPlayRecord.convert(
                    Integer.class, // id                  bigint
                    String.class, // play_record_id      varchar
                    String.class, // user_id             varchar
                    String.class, // sessionid           varchar
                    String.class, // udid                varchar
                    String.class, // deviceid            varchar
                    String.class, // game_id             varchar
                    String.class, // game_name           varchar
                    String.class, // type                varchar
                    String.class, // matrix_orderid      varchar
                    String.class, // ssvc_out_trade_no   varchar
                    Integer.class, // gamer_pay_amount    int
                    Integer.class, // ssvc_buy_minutes    int
                    Integer.class, // ssvc_remain_minutes int
                    Integer.class, // version             int
                    Date.class, // play_start_time     timestamp
                    Date.class, // play_finish_time    timestamp
                    Date.class, // last_heartbeat_time timestamp
                    Integer.class, // play_minutes        int
                    Integer.class, // pay_amount          int
                    Integer.class, // pay_discount        int
                    Integer.class, // status              tinyint
                    String.class, // err_status          varchar
                    String.class, // trade_status        varchar
                    String.class, // attach_json         varchar
                    String.class, // update_by           varchar
                    Date.class, // create_time         timestamp
                    Date.class  // update_time         timestamp
            );
            DataDealer.writeXls(raw_dataFrameOfPlayRecord, Constants.OUT_PUT_SOURCE_PATH+"\\PlayRecord.xlsx");
            raw_dataFrameOfPlayRecord.writeCsv(Constants.OUT_PUT_SOURCE_PATH+"\\PlayRecord.csv");
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    //获取单例
    public static synchronized PlayRecordSingleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new PlayRecordSingleton();
            System.out.println("单例模式-生成：PlayRecordSingleton —— " + instance);
        }
        return instance;
    }

    //获取user的dataFrame【已经去除非正常的账号】
    public static DataFrame<Object> dataFrameOfPlayRecord() throws IOException {
        try {
            getInstance();
            dataFrameOfPlayRecord = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfPlayRecord);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataFrameOfPlayRecord;
    }

    public static DataFrame<Object> dataFrameOfPlayRecord(String startDate, String endDate) throws IOException {

        start = startDate;
        end = endDate;

        try {
            getInstance();
            dataFrameOfPlayRecord = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfPlayRecord);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataFrameOfPlayRecord;
    }

    //获取原始表
    public static DataFrame<Object> raw_dataFrameOfPlayRecord() {
        try {
            getInstance();
            // 如果本次实例没有导出，那就导出
            if (!hasOutPutXlsx) {
                DataDealer.writeXls(raw_dataFrameOfPlayRecord, Constants.RESOURCE_PATH+"/raw/user.xlsx");
                hasOutPutXlsx = true;
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return raw_dataFrameOfPlayRecord;
    }

}
