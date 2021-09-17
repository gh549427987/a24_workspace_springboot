package com.jhua.dao.base;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao {

    private static final String driver;
    private static final String url;
    private static final String username;
    private static final String password;

    private static final String url_xiaochengxu;
    private static final String username_xiaochengxu;
    private static final String password_xiaochengxu;

    static {
        Properties properties = new Properties();

        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("config/db.properties");

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");

        url_xiaochengxu = properties.getProperty("url_xiaochengxu");
        username_xiaochengxu = properties.getProperty("username_xiaochengxu");
        password_xiaochengxu = properties.getProperty("password_xiaochengxu");
    }

    //初始化驱动类， 连接数据库
    public static Connection getConnection_xiaochengxu() {
        Connection connection = null;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url_xiaochengxu, username_xiaochengxu, password_xiaochengxu);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //初始化驱动类， 连接数据库
    public static Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共方法
    public static ResultSet execute(Connection connection, String sql, Object[] params) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        if (params != null){
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1, params[i]);
            }
        }

        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet;
    }

    //编写增删改公共方法
    public static int execute(Connection connection, String sql, Object[] params, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1, params[i]);
        }

        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

}
