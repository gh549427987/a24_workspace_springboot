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

public class UserSingleton {

    private static UserSingleton instance = null;
    private static boolean hasOutPutXlsx = false;

    private static DataFrame<Object> raw_dataFrameOfUser = null;
    private static DataFrame<Object> dataFrameOfUser = null;

    private UserSingleton() throws SQLException {
        //单例模式
        Connection connection = BaseDao.getConnection();
        try {
            ResultSet execute = null;
            execute = BaseDao.execute(connection, Constants.user_SelectAll, null);
            raw_dataFrameOfUser = DataFrame.readSql(execute);
            raw_dataFrameOfUser.convert(
                    Long.class, // id
                    String.class, // user_id
                    String.class, // user_icon_url
                    String.class, // nick_name
                    String.class, // mobile_no
                    String.class, // email
                    String.class, // status
                    String.class, // type
                    Long.class, // chain_merchant_id
                    String.class, // online_limit
                    String.class, // user_level
                    String.class, // discount_card_matrix_orderid
                    String.class, // username
                    String.class, // password
                    Integer.class, // salt
                    String.class, // login_type
                    Integer.class, // sdk_aid
                    Long.class, // sdk_userinfo
                    String.class, // last_login_time
                    Date.class, // remark
                    String.class, // create_by
                    String.class, // update_by
                    Date.class, // create_time
                    Date.class
                                );
            DataDealer.writeXls(raw_dataFrameOfUser, Constants.OUT_PUT_SOURCE_PATH+"\\User.xlsx");

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    //获取单例
    public static synchronized UserSingleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserSingleton();
            System.out.println("单例模式-生成：UserSingleton —— " + instance);
        }
        return instance;
    }

    //获取user的dataFrame【已经去除非正常的账号】
    public static DataFrame<Object> dataFrameOfUser() throws IOException {
        try {
            getInstance();
            dataFrameOfUser = UserOperationUtil.reduceUnnormalAccounts(raw_dataFrameOfUser);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataFrameOfUser;
    }

    //获取原始表
    public static DataFrame<Object> raw_dataFrameOfUser() {
        try {
            getInstance();
//            // 如果本次实例没有导出，那就导出
//            if (!hasOutPutXlsx) {
//                DataDealer.writeXls(raw_dataFrameOfUser, Constants.RESOURCE_PATH+"/raw/user.xlsx");
//                hasOutPutXlsx = true;
//            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return raw_dataFrameOfUser;
    }




}

