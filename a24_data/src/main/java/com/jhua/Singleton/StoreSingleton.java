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

public class StoreSingleton {

    private static StoreSingleton instance;
    private static boolean hasOutPutXlsx = false;

    private static DataFrame<Object> dataFrameOfStore = null;
    private static DataFrame<Object> raw_dataFrameOfStore = null;

    private StoreSingleton() throws SQLException {
        //单例模式
        Connection connection = BaseDao.getConnection();
        try {
            ResultSet execute = null;
            execute = BaseDao.execute(connection, Constants.store_SelectAll, null);
            raw_dataFrameOfStore = DataFrame.readSql(execute);
            raw_dataFrameOfStore.convert(
                    Integer.class,// id                   bigint
                    String.class,// store_id             varchar
                    String.class,// user_id              varchar
                    String.class,// username             varchar
                    String.class,// store_name           varchar
                    String.class,// store_address        varchar
                    String.class,// mobile_phone         varchar
                    String.class,// fix_phone            varchar
                    String.class,// identity_no          varchar
                    String.class,// real_name            varchar
                    Integer.class,// pass_real_name_check tinyint
                    Integer.class,// status               tinyint
                    String.class,// create_by            varchar
                    String.class,// update_by            varchar
                    Date.class,// create_time          timestamp
                    Date.class// update_time          timestamp
            );
            DataDealer.writeXls(raw_dataFrameOfStore, Constants.OUT_PUT_SOURCE_PATH+"\\Store.xlsx");

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    //获取单例
    public static synchronized StoreSingleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new StoreSingleton();
            System.out.println("单例模式-生成：StoreSingleton —— " + instance);
        }
        return instance;
    }

    //获取user的dataFrame【已经去除非正常的账号】
    public static DataFrame<Object> dataFrameOfStore() throws IOException {
        try {
            getInstance();
            dataFrameOfStore = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfStore);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return dataFrameOfStore;
    }

    //获取原始表
    public static DataFrame<Object> raw_dataFrameOfStore() {
        try {
            getInstance();
            // 如果本次实例没有导出，那就导出
            if (!hasOutPutXlsx) {
                DataDealer.writeXls(raw_dataFrameOfStore, Constants.RESOURCE_PATH+"/raw/user.xlsx");
                hasOutPutXlsx = true;
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return raw_dataFrameOfStore;
    }

}
