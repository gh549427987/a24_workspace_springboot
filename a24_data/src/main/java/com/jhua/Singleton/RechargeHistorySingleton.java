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

public class RechargeHistorySingleton {

    private static RechargeHistorySingleton instance = null;
    private static boolean hasOutPutXlsx = false;

    private static DataFrame<Object> raw_dataFrameOfRechargeHistory = null;
    private static DataFrame<Object> dataFrameOfRechargeHistory = null;

    private static String start;
    private static String end;

    private RechargeHistorySingleton() throws SQLException {
        //单例模式
        Connection connection = BaseDao.getConnection();
        try {

            ResultSet execute = null;
            String sql = SingletonUtil.sqlDate(Constants.rechargeHistory_SelectAll, start, end);
            System.out.println("单例模式-RechargeHistorySingleton：" + sql);
            execute = BaseDao.execute(connection, sql, null);
            raw_dataFrameOfRechargeHistory = DataFrame.readSql(execute);
            raw_dataFrameOfRechargeHistory.convert(
                    Integer.class,// id          bigint
                    String.class,// user_id     varchar
                    String.class,// order_id    varchar
                    Date.class,// pay_time    timestamp
                    String.class,// paymoney    varchar
                    Integer.class,// pay_coin    int
                    Integer.class,// remain_coin int
                    Date.class,// create_time timestamp
                    Date.class// update_time timestamp
            );
            DataDealer.writeXls(raw_dataFrameOfRechargeHistory, Constants.OUT_PUT_SOURCE_PATH+"\\RechargeHistory.xlsx");

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    //获取单例
    public static synchronized RechargeHistorySingleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new RechargeHistorySingleton();
            System.out.println("单例模式-生成：RechargeHistorySingleton —— " + instance);
        }
        return instance;
    }

    //获取user的dataFrame【已经去除非正常的账号】
    public static DataFrame<Object> dataFrameOfRechargeHistory() throws IOException {

        try {
            getInstance();
            dataFrameOfRechargeHistory = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfRechargeHistory);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataFrameOfRechargeHistory;
    }

    public static DataFrame<Object> dataFrameOfRechargeHistory(String startDate, String endDate) throws IOException {

        start = startDate;
        end = endDate;

        try {
            getInstance();
            dataFrameOfRechargeHistory = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfRechargeHistory);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataFrameOfRechargeHistory;
    }

    //获取原始表
    public static DataFrame<Object> raw_dataFrameOfRechargeHistory() {
        try {
            getInstance();
            // 如果本次实例没有导出，那就导出
            if (!hasOutPutXlsx) {
                DataDealer.writeXls(raw_dataFrameOfRechargeHistory, Constants.RESOURCE_PATH+"/raw/user.xlsx");
                hasOutPutXlsx = true;
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return raw_dataFrameOfRechargeHistory;
    }

}
