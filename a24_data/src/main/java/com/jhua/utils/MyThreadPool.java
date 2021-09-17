package com.jhua.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @author xiejiehua
 * @DATE 8/13/2021
 */

public class MyThreadPool {

//    private static final int thread_num = Runtime.getRuntime().availableProcessors();
    private static final int thread_num = 2;
    private static ExecutorService instance = null;
    private static ExecutorService singleThreadExecutor = null;

    //获取单例
    public static synchronized ExecutorService getInstance() throws SQLException {
        if (instance == null) {
            instance = Executors.newFixedThreadPool(thread_num);
            System.out.println("单例模式-生成：newFixedThreadPool —— " + instance);
        }
        return instance;
    }

    public static synchronized ExecutorService getSingleThreadExecutorInstance() {
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newSingleThreadExecutor();
            System.out.println("单例模式-生成：singleThreadExecutor —— " + instance);
        }
        return singleThreadExecutor;

    }



}
