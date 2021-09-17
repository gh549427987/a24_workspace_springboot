package com.jhua.Singleton;

import com.jhua.constants.Constants;
import com.jhua.dao.base.BaseDao;
import com.jhua.utils.DataDealer;
import com.jhua.utils.UserOperationUtil;
import joinery.DataFrame;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDiscountCardApplSingleton {

    private static UserDiscountCardApplSingleton instance = null;
    private static boolean hasOutPutXlsx = false;

    private static DataFrame<Object> raw_dataFrameOfUDCA = null;
    private static DataFrame<Object> dataFrameOfUDCA = null;

    private UserDiscountCardApplSingleton() throws SQLException {

        //单例模式
        Connection connection = BaseDao.getConnection();
        try {
            ResultSet execute = null;
            execute = BaseDao.execute(connection, Constants.userDiscountCardAppl_SelectAll, null);
            raw_dataFrameOfUDCA = DataFrame.readSql(execute);
            raw_dataFrameOfUDCA.convert(
                    Long.class, // id                         bigint
                    String.class, // matrix_orderid             varchar
                    String.class, // user_id                    varchar
                    String.class, // card_id                    varchar
                    Date.class, // purchase_date              timestamp
                    Date.class, // expire_date                timestamp
                    Integer.class, // discount_hours             int
                    Integer.class, // remain_discount_minutes    int
                    String.class, // level_up_from              varchar
                    String.class, // level_up_to                varchar
                    String.class, // bind_device_udids          varchar
                    Integer.class, // infinite_card_device_count int
                    Integer.class, // auto_repurchase            tinyint
                    Integer.class, // version                    int
                    Integer.class, // status                     tinyint
                    String.class, // remark                     varchar
                    String.class, // create_by                  varchar
                    String.class, // update_by                  varchar
                    Date.class, // create_time                timestamp
                    Date.class // update_time                timestamp
            );
            DataDealer.writeXls(raw_dataFrameOfUDCA, Constants.OUT_PUT_SOURCE_PATH+"\\UserDiscountCardAppl.xlsx");

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

    }

    private static synchronized UserDiscountCardApplSingleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserDiscountCardApplSingleton();
            System.out.println("单例模式-生成：UserDiscountCardApplSingleton —— " + instance);
        }
        return instance;
    }

    //返回处理过的数据
    public static DataFrame<Object> dataFrameOfUserDiscountCardAppl() throws IOException {
        try {
            getInstance();
            dataFrameOfUDCA = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfUDCA);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataFrameOfUDCA;
    }

    //获取原始表
    public static DataFrame<Object> raw_dataFrameOfUDCA() {
        try {
            getInstance();
            // 如果本次实例没有导出，那就导出
            if (!hasOutPutXlsx) {
                DataDealer.writeXls(raw_dataFrameOfUDCA, Constants.RESOURCE_PATH+"/raw/user.xlsx");
                hasOutPutXlsx = true;
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return raw_dataFrameOfUDCA;
    }
}
