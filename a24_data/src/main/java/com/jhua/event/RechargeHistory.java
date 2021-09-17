package com.jhua.event;

import com.jhua.Singleton.RechargeHistorySingleton;
import com.jhua.utils.DataDealer;
import joinery.DataFrame;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RechargeHistory {

    private static String USER_DIR = System.getProperty("user.dir");
    private static String OUT_PUT_PATH = USER_DIR+"/A24_Data/src/main/output/";

    //去掉非正常测试账号的
    public static DataFrame<Object> dataFrameOfRechargeHistory() throws IOException {
        return RechargeHistorySingleton.dataFrameOfRechargeHistory();
    }
    public static DataFrame<Object> dataFrameOfRechargeHistory(String startDate, String endDate) throws IOException {
        return RechargeHistorySingleton.dataFrameOfRechargeHistory(startDate, endDate);
    }


    //平台日付费峰值-定义：最高付费商家付费额
    public static DataFrame<Object> perDayMaxPayAmountAndStore(String fromDate, String toDate) throws ParseException, IOException {
        /*
         * @Author liu-miss
         * @Description //TODO 【按日】平台日付费峰值-定义：最高付费商家付费额
         * @Date  2021/3/17
         * @Param [fromDate, toDate]
         * @return joinery.DataFrame<java.lang.Object>
         **/

        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null)
            return null;

        // 处理并筛选出合适的数据
        DataFrame<Object> dataFrame = RechargeHistory.dataFrameOfRechargeHistory(fromDate, toDate);
        DataDealer.format_time(dataFrame, "create_time", pattern);
        Map<String, Integer> sim1 = DataDealer.columns_index(dataFrame);

        // 全部转换为Double类型，才能sum相加，坑壁
        for (int i = 0; i < dataFrame.index().size(); i++) {
            String paymoney = (String) dataFrame.get(i, sim1.get("paymoney"));

            if (!paymoney.equals(" "))
                dataFrame.set(i, sim1.get("paymoney"), Double.parseDouble(paymoney));
        }

        // 筛选合并
        Calendar cal_create = Calendar.getInstance();
        cal_create.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_fromDate = Calendar.getInstance();
        cal_fromDate.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_toDate = Calendar.getInstance();
        cal_toDate.setFirstDayOfWeek(Calendar.MONDAY);
        final SimpleDateFormat sim_3 = new SimpleDateFormat(pattern);
        Map<String, Integer> stringIntegerMap = DataDealer.columns_index(dataFrame);

        dataFrame = dataFrame
                .select(value -> {
                    // 时间比较
                    String create_time = (String) value.get(stringIntegerMap.get("create_time"));

                    try {
                        cal_fromDate.setTime(sim_3.parse(fromDate));
                        cal_toDate.setTime(sim_3.parse(toDate));
                        cal_create.setTime(sim_3.parse(create_time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Integer cal_fromDate_year = cal_fromDate.get(Calendar.YEAR);
                    Integer cal_toDate_year = cal_toDate.get(Calendar.YEAR);
                    Integer cal_create_year = cal_create.get(Calendar.YEAR);

                    return cal_create.after(cal_fromDate) && cal_create.before(cal_toDate) &&
                            !value.get(stringIntegerMap.get("paymoney")).equals(" ");

                })
                .retain("user_id", "create_time", "paymoney")
                .groupBy("user_id", "create_time")
                .sum()
                .sortBy("create_time")
        ; //每天付费情况

        // 返回指定时间区间的数据
        DataDealer.selectDataFrameOfDetectedTime(dataFrame, fromDate, toDate);

        // 通过isStore判断是否需要进行商家的统计
        boolean isStore = true;
        if (isStore)
            dataFrame = Store.returnIsStoreDataFrame(dataFrame);

        // 分发数据
        // 先创建一个带有时间的空dataFrame，自定以长度
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, DataDealer.createSingleKeyDataFrame(dataFrame, "user_id").size());
        result = result.transpose();

        // 遍历并把所有的东西都写进去
        List<Object> result_userid_list = result.col(0);

        // 把user_id全部写进去
        for (int i = 0; i < dataFrame.col(0).size(); i++) {
            String user_id = (String) dataFrame.get(i, 0);
            if (!(result_userid_list.contains(user_id))) {
                // 如果不存在这个user_id的话，就给他加上
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j) == null) {
                        result.set(j, 0, user_id);
                        break;
                    }
                }
            } else {
                // 如果存在这个user_id,确定为同一行
                for (Object o : result_userid_list) {
                    if (o.equals(user_id)) {
                        break;
                    }
                }
            }
        }


        // 遍历result每一列时间
        // 遍历这一天的第一个人
        Object[] result_date_list = result.columns().toArray();
        HashMap<String, Double> Max_Pay = new HashMap<>(); // 记录一个商家一天最多花多少钱
        for (int i = 1; i < result_date_list.length; i++) {


            Calendar result_cal = Calendar.getInstance();
            result_cal.setFirstDayOfWeek(Calendar.MONDAY);
            Calendar dataFrame_cal = Calendar.getInstance();
            dataFrame_cal.setFirstDayOfWeek(Calendar.MONDAY);
            SimpleDateFormat sim = new SimpleDateFormat(pattern);
            Max_Pay.clear(); //到第二天了 ，清除掉前一天的
            Double Max_Paymoney = 0.0;
            // 遍历result每一行user_id
            for (int j = 0; j < result_userid_list.size()-1; j++) {
                String result_user_id = (String) result.get(j, 0); //获得这个单元格 (user_id, 时间)
                String result_date = (String) result_date_list[i];
                result_cal.setTime(sim.parse(result_date)); // 时间

                Max_Paymoney = 0.0; //初始化

                // 判断dataFrame是否有充值金额
                // 遍历dataFrame每一行的user_id ， 对比user_id
                for (int k = 0; k < dataFrame.index().size(); k++) {

                    // 获得<user_id, paymoney> map对象，写入的是最大的付费商家
                    String dataFrame_user_id = (String) dataFrame.get(k, 0); // 获得这个单元格的user_id // paymoney
                    String dataFrame_date = (String) dataFrame.get(k, 1);
                    dataFrame_cal.setTime(sim.parse(dataFrame_date)); // 这一行的时间
                    Double paymoney = (Double) dataFrame.get(k, 2); // paymoney

//                    System.out.println("i:"+ i + " j:" + j + " k:" + k + " result_user_id:" + result_user_id + " dataFrame_user_id:" + dataFrame_user_id + " result_date:" + result_date + " dataFrame_date:" + dataFrame_date);
                    if (result_user_id.equals(dataFrame_user_id) && result_cal.equals(dataFrame_cal)) {// 比较user_id 和 time
                            Max_Paymoney = paymoney;// 表已经经过处理，只要匹配成功一次，你就相当于获得你这个人今天消费了多少了
                    }
                }

                if (Max_Paymoney != 0)
                    Max_Pay.put(result_user_id, Max_Paymoney); // 这一天，你这个人，你就充值了这么多
            }


            // 知道这个商家今天花了多少之后，继续计算其他的,最终获得 Max_Pay 字典
            Max_Paymoney = 0.0;
            HashMap<String, Double> MaxOfMax = new HashMap<>();
            for (String user_id : Max_Pay.keySet()) {
                // 如果大于等于初始值，就给他加到新的字典上
                if (Max_Pay.get(user_id) >= Max_Paymoney) {
                    Max_Paymoney = Max_Pay.get(user_id);
                    MaxOfMax.put(user_id, Max_Paymoney);
                }
            }

            // 最后环节，写上去
            for (String user_id : MaxOfMax.keySet()) {
                // 遍历result每一行user_id
                for (int j = 0; j < result_userid_list.size()-1; j++) {
                    String result_user_id = (String) result.get(j, 0); //获得这个单元格 (user_id, 时间)

                    if (user_id.equals(result_user_id))
                        result.set(j, i, MaxOfMax.get(user_id));
                }
            }
        }
        result.rename("Content", "用户ID/付费额(元）/日期（游玩大于3分钟）");
        DataDealer.writeXls(result, OUT_PUT_PATH+"平台日付费-定义：付费商家付费额.xlsx");

        return result;
    }

    // 商家账号维度月度付费
    public static DataFrame<Object> monthStoresRechargeHistory(String fromDate, String toDate) throws IOException, ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO 商家账号维度月度付费
         * @Date  2021/3/18
         * @Param [fromDate, toDate]
         * @return joinery.DataFrame<java.lang.Object>
         **/

        // 处理传参，获得pattern
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        if (pattern == null)
            return null;

        // 获得商家的消费表
        DataFrame<Object> rechargeHistory = dataFrameOfRechargeHistory();

        DataDealer.format_time(rechargeHistory, "create_time", pattern);// 处理时间为月度
        Map<String, Integer> sim = DataDealer.columns_index(rechargeHistory);// 获得行列对应关系，可通过key直接获得索引
        rechargeHistory = Store.returnIsStoreDataFrame(rechargeHistory);

        // 获得商家列表
        DataFrame<Object> store = Store.dataFrameOfStore();

        // 尝试join
        rechargeHistory = rechargeHistory.joinOn(store, DataFrame.JoinType.LEFT, "user_id");

        return null;
    }

    // 商家指定时间内的消费情况 不按周
    public static DataFrame<Object> storePointTimeConsume(String fromDate, String toDate) throws ParseException, IOException {

        // 检查传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        assert pattern != null;
        if (pattern.length() == 13 )
            return null;

        // 处理原始数据
        DataFrame<Object> rechargeHistory = dataFrameOfRechargeHistory();
        DataFrame<Object> playRecord = PlayRecord.dataFrameOfPlayRecord();
        DataFrame<Object> store = Store.dataFrameOfStore();

        Map<String, Integer> stringIntegerMap_rh = DataDealer.columns_index(rechargeHistory);
        Map<String, Integer> stringIntegerMap_pr = DataDealer.columns_index(playRecord);
        Map<String, Integer> stringIntegerMap_st = DataDealer.columns_index(store);

        // select特定日期区间的时间
        String pattern_s = "yyyy-MM-dd HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        SimpleDateFormat simpleDateFormat_s = new SimpleDateFormat(pattern_s);

        Calendar cal_start = Calendar.getInstance();
        cal_start.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_end = Calendar.getInstance();
        cal_end.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_date = Calendar.getInstance();
        cal_date.setFirstDayOfWeek(Calendar.MONDAY);
            // 设置后给定的开始日期和结束日期
        cal_start.setTime(simpleDateFormat.parse(fromDate));
        cal_end.setTime(simpleDateFormat.parse(toDate));

            // 开始选择rechargeHistory
        System.out.println(rechargeHistory.types());
        rechargeHistory = rechargeHistory.select(value -> {
            Date date = (Date) value.get(stringIntegerMap_rh.get("create_time"));


            cal_date.setTime(date); // 此处是"记录"的时间

            // 判断是否在此区间内
            boolean c1 = cal_date.after(cal_start);
            boolean c2 = cal_date.before(cal_end);
            boolean c3 = !value.get(stringIntegerMap_rh.get("paymoney")).equals(" ");
            return c1 && c2 && c3;
        }).retain("user_id", "pay_coin", "paymoney", "pay_time")
                .rename("user_id", "用户")
                .rename("pay_coin", "商品")
                .rename("paymoney", "付费")
                .rename("pay_time", "创建时间")
        ;

            // 开始选择playRecord
        playRecord = playRecord.select(value -> {
            Date date = (Date) value.get(stringIntegerMap_pr.get("create_time"));

            cal_date.setTime(date); // 此处是"记录"的时间;

            // 判断是否在此区间内
            boolean c1 = cal_date.after(cal_start);
            boolean c2 = cal_date.before(cal_end);
            boolean c3 = ((String)value.get(stringIntegerMap_pr.get("game_name"))).contains("购买");

            return c1 && c2 && c3;
        }).retain("user_id", "game_name", "pay_amount", "create_time")
                .rename("user_id", "用户")
                .rename("game_name", "商品")
                .rename("pay_amount", "付费")
                .rename("create_time", "创建时间")
        ;

        // 两个表合在一起
        Map<String, Integer> stringIntegerMap_rh_2 = DataDealer.columns_index(rechargeHistory);
        Map<String, Integer> stringIntegerMap_pr_2 = DataDealer.columns_index(playRecord);

        for (List<Object> objects : playRecord) {
            Object account = objects.get(stringIntegerMap_pr_2.get("用户"));
            Object sp = objects.get(stringIntegerMap_pr_2.get("商品"));
            Object fz = objects.get(stringIntegerMap_pr_2.get("付费"));
            Object cj = objects.get(stringIntegerMap_pr_2.get("创建时间"));

            ArrayList<Object> objects1 = new ArrayList<Object>();
            objects1.add(account);
            objects1.add(sp);
            objects1.add(fz);
            objects1.add(cj);
            int index = 0;
            while (true) {
                if (rechargeHistory.index().contains(index)) {
                    index++; // 如果index不在行index里，就加1，知道不在为止
                } else {
                    rechargeHistory.append(index, Arrays.asList(account, cj, fz, sp));
                    break;
                }
            }
        }

        // 添加store信息
        rechargeHistory.add("账号名");
        rechargeHistory.add("店铺名");
        rechargeHistory.add("省份");
        rechargeHistory.add("城市");
        rechargeHistory.add("区");
        rechargeHistory.add("详细地址");


        Map<String, Integer> stringIntegerMap_rh_3 = DataDealer.columns_index(rechargeHistory);
        int row = 0;
        for (List<Object> objects : rechargeHistory) {
            for (List<Object> objectList : store) {
                String user_id = (String) objects.get(stringIntegerMap_rh_3.get("用户"));
                String user_id_inStore = (String) objectList.get(stringIntegerMap_st.get("user_id"));
                // 如果用户名相等
                if (user_id.equals(user_id_inStore)) {

                    // 找出所有数据
                    String username = (String) objectList.get(stringIntegerMap_st.get("username"));
                    String store_name = (String) objectList.get(stringIntegerMap_st.get("store_name"));
                    String store_address = (String) objectList.get(stringIntegerMap_st.get("store_address"));

                    String[] split = store_address.split("/");
                    String a1 = split[0];
                    String a2 = split[1];
                    String a3 = split[2];
                    String a4 = split[3];

                    rechargeHistory.set(row, stringIntegerMap_rh_3.get("账号名"), username);
                    rechargeHistory.set(row, stringIntegerMap_rh_3.get("店铺名"), store_name);
                    rechargeHistory.set(row, stringIntegerMap_rh_3.get("省份"), a1);
                    rechargeHistory.set(row, stringIntegerMap_rh_3.get("城市"), a2);
                    rechargeHistory.set(row, stringIntegerMap_rh_3.get("区"), a3);
                    rechargeHistory.set(row, stringIntegerMap_rh_3.get("详细地址"), a4);
                }
            }
            row++;
        }


        if (pattern.length() == 7) {
            rechargeHistory.writeXls(OUT_PUT_PATH + "账号维度-指定日期间付费.xlsx");
            DataDealer.writeXls(rechargeHistory, OUT_PUT_PATH + "账号维度-指定日期间付费.xlsx");
        } else if (pattern.length() == 10) {
            rechargeHistory.writeXls(OUT_PUT_PATH + "账号维度-指定日期间付费.xlsx");
            DataDealer.writeXls(rechargeHistory, OUT_PUT_PATH + "账号维度-指定日期间付费.xlsx");
        }

        return rechargeHistory;
    }

    public static void main(String[] args) throws IOException, ParseException {
//        perDayMaxPayAmountAndStore("2020-11-01", "2021-03-09");
        storePointTimeConsume("2021-01-01", "2021-03-01");
    }

}
