package com.jhua.event;

import com.jhua.utils.DataDealer;
import joinery.DataFrame;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class YingBiXiaoHao {
    private static String USER_DIR = System.getProperty("user.dir");
    private static String OUT_PUT_PATH = USER_DIR+"/A24_Data/src/main/output/";

    public YingBiXiaoHao() throws IOException {
        Calendar start_date = Calendar.getInstance();
        start_date.setFirstDayOfWeek(Calendar.MONDAY);
        start_date.set(2020,11,1,0,0,0);;

        Calendar end_date = Calendar.getInstance();
        end_date.setFirstDayOfWeek(Calendar.MONDAY);
        end_date.set(2022,1,1,1,1,1);

        DataFrame<Object> play_record;
        DataFrame<Object> store;

        store = DataFrame.readCsv("Joinery/src/main/resources/store.csv");
        play_record = DataFrame.readCsv("Joinery/target/classes/play_record.csv");

        DataFrame<Object> agg;
        agg = play_record.join(store);

        //   筛选条件
        HashMap<String, Integer> agg_colIndex = (HashMap<String, Integer>) DataDealer.columns_index(agg);
        DataFrame<Object> agg_r1 = agg.select(values -> {
            // values 每一行的数据
            // 不计算自助机的订单
            Boolean c1 = values.contains("buyDiscountCard");
            Boolean c2 = values.contains("playGame");

            // start_date，需要计算什么日期之后的
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.setTime((Date) values.get(agg_colIndex.get("create_time_left")));
            Boolean c3 = cal.after(start_date);

            // status为2
            Boolean c4 = (long) values.get(agg_colIndex.get("status_left")) == 2;

            // 付费不能为0
            Boolean c5 = (long) values.get(agg_colIndex.get("pay_amount")) != 0;

            return (c1 ^ c2) && c3 && c4 && c5;
        });

        //重新筛选需要的数据出来
        DataFrame<Object> agg_final = agg_r1.retain("user_id_left", "username", "store_name", "game_id", "game_name", "type", "pay_amount", "create_time_left");

        agg_final.rename("user_id_left", "用户ID");
        agg_final.rename("username", "账号名");
        agg_final.rename("store_name", "店铺名");
        agg_final.rename("game_id", "商品ID");
        agg_final.rename("game_name", "商品名");
        agg_final.rename("type", "消费类型");
        agg_final.rename("pay_amount", "支付影币");
        agg_final.rename("create_time_left", "创建时间");

        agg_final.writeXls(OUT_PUT_PATH + "影币消耗记录.xlsx");
    }

    public void output(DataFrame<Object> dataFrame) {

    }

    public static void main(String[] args) throws IOException {
        YingBiXiaoHao yingBiXiaoHao = new YingBiXiaoHao();

    }


}
