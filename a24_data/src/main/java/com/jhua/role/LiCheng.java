package com.jhua.role;

import com.jhua.constants.Constants;
import com.jhua.event.PlayRecord;
import com.jhua.event.Store;
import com.jhua.utils.DataDealer;
import joinery.DataFrame;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LiCheng {



    public static void main(String[] args) throws IOException {
        licheng03();
    }

    // 2021-04-01 》 2021-04-30启动过Racket的商家账号
    public static void licheng01() throws IOException {

        String startDate = "2021-04-01 00:00:00";
        String endDate = "2021-05-01 00:00:00";

        DataFrame<Object> playRecord = PlayRecord.dataFrameOfPlayRecord_Store(startDate, endDate); //拉取数据库，返回属于商家的记录
        DataFrame<Object> retain = playRecord.retain("play_record_id", "user_id", "game_id");
        Map<String, Integer> index = DataDealer.columns_index(retain);
        retain = retain.select(value -> {
            String game_id = (String) value.get(index.get("game_id"));
            return game_id.equals("VR000020");
        });
        DataFrame<Object> user_id = retain.groupBy("user_id").count();

        DataFrame<Object> store = Store.dataFrameOfStore().reindex("user_id");
        System.out.println(store);
        System.out.println(user_id);
        DataFrame<Object> join = user_id.join(store, DataFrame.JoinType.LEFT);
        System.out.println(join);

    }

    // 获得商家最早和最晚的游戏记录
    public static void licheng02() throws IOException {
        /**
         * @Author liu-miss
         * @Description //TODO
         * @Date  2021/5/28
         * @Param []
         * @return void
         **/

        // 游戏记录表
        DataFrame<Object> playRecord = PlayRecord.dataFrameOfPlayRecord_Store(); //拉取数据库，返回属于商家的记录
        Map<String, Integer> index_pr = DataDealer.columns_index(playRecord);
        // 商家记录表
        DataFrame<Object> store = Store.dataFrameOfStore();

        // 设置最早时间变量和最晚时间变量
        Date first_time_record ;
        String first_id_record ;
        Date last_time_record ;
        String last_id_record ;

        // 遍历商家表，给表后添加字段 最早游戏记录id和最早游戏时间 、 最后游戏记录id和最后游戏时间
        store.add("最早游戏记录id", "最早游戏时间", "最后游戏记录id", "最后游戏时间");
        int count = 0;
        for (List<Object> s : store) {

            String user_id_s = (String) s.get(2);// 2 是user_id

            // 获得user_id，扔进playRecord里面查询这个用户的user_id下的所有记录
            DataFrame<Object> user_id_pr_DF = playRecord.select(value -> {
                String user_id_pr = (String) value.get(index_pr.get("user_id"));
                return user_id_pr.equals(user_id_s);
            })
                    .sortBy("create_time"); // 第一条是最早的，最后一条是最晚的;

            // 跳过空的
            if (user_id_pr_DF.isEmpty()) {
                count++;
                continue;
            }

            // 获取所需的两条记录，共四个变量
            DataFrame<Object> head = user_id_pr_DF.head(1);
            DataFrame<Object> tail = user_id_pr_DF.tail(1);

            first_time_record = (Date) head.get(0, index_pr.get("create_time"));
            first_id_record = (String) head.get(0, index_pr.get("play_record_id"));
            last_time_record = (Date) tail.get(0, index_pr.get("create_time"));
            last_id_record = (String) tail.get(0, index_pr.get("play_record_id"));

            store.set(count, "最早游戏记录id", first_id_record);
            store.set(count, "最早游戏时间", first_time_record);
            store.set(count, "最后游戏记录id", last_id_record);
            store.set(count, "最后游戏时间", last_time_record);

            count++;
        }

        DataDealer.writeXls(store, Constants.OUT_PUT_PATH+"store_with_begin_end.xlsx");
    }

    // 输出商家的过滤后的源数据
    public static void licheng03() throws IOException {
        DataFrame<Object> dataFrame = Store.dataFrameOfStore();
        DataDealer.writeXls(dataFrame, Constants.DESKTOP+"Store.xlsx");
    }
}
