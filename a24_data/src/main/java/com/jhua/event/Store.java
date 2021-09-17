package com.jhua.event;

import com.jhua.Singleton.StoreSingleton;
import com.jhua.constants.Constants;
import com.jhua.utils.DataDealer;
import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;
import joinery.DataFrame;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Store {

    public static DataFrame<Object> dataFrameOfStore() throws IOException {
        /*
         * @Author liu-miss
         * @Description //TODO 返回商家的dataFrame，使用User 类的去除非正常商家方法
         * @Date  2021/3/16
         * @Param []
         * @return joinery.DataFrame<java.lang.Object>
         **/
        return StoreSingleton.dataFrameOfStore();
    }

    // 返回只有商家的在内的dataFrame
    public static DataFrame<Object> returnIsStoreDataFrame(DataFrame<Object> dataFrame) throws IOException {
        // 通过isStore判断是否需要进行商家的统计
        // 如果dataFrame里面的user_id，在store表里能找到，那就选上
        DataFrame<Object> store = Store.dataFrameOfStore();
        Map<String, Integer> sim_dataFrame = DataDealer.columns_index(dataFrame);

        ArrayList<Object> store_user_id = new ArrayList<>(store.col("user_id")); // 获取商家的user_id list
        dataFrame = dataFrame.select(value -> store_user_id.contains(value.get(sim_dataFrame.get("user_id")))); //如果商家user_id list 包括了 这一行的的user_id ，就选择这一行
        return dataFrame;
    }

    public static DataFrame<Object> new_store_registers_month(String pattern) throws IOException, ParseException {

        // 读取表
        DataFrame<Object> store = dataFrameOfStore();

        store = store.retain("user_id", "create_time");

        // 先重新格式化所有时间为月度
        DataDealer.format_time(store, "create_time", pattern);

        store = store.groupBy("create_time").count();

        // 重命名列名
        store = store.rename("user_id", "覆盖商家数-定义：填写商家信息账号数（新增）");

//        store.writeXls(Constants.OUT_PUT_PATH+"/覆盖商家数-定义：填写商家信息账号数（新增）.xlsx");


        return store;
    }

    public static DataFrame<Object> all_store_registers_month(String pattern) throws IOException, ParseException {

        // 用已有函数获取每月的注册量，稍后进行累加
        DataFrame<Object> store = new_store_registers_month(pattern);

        // 不断累加所有月份注册的比
        int count = 0;
        int row = 0;
        ListIterator<List<Object>> iterrows = store.iterrows();
        while (iterrows.hasNext()) {
            List<Object> next = iterrows.next();
            count += (int) next.get(1);
            store.set(row, 1, count);
            row++;
        }

        // 重命名列名
        store = store.rename("覆盖商家数-定义：填写商家信息账号数（新增）", "覆盖商家数-定义：填写商家信息账号数（累计）");

        return store;
    }

    public static void main(String[] args) throws IOException, ParseException {
        all_store_registers_month("yyyy-MM");
    }
}
