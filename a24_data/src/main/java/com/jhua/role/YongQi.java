package com.jhua.role;

import com.jhua.constants.Constants;
import com.jhua.dao.base.BaseDao;
import com.jhua.event.*;
import com.jhua.utils.DataDealer;
import com.jhua.utils.WinUtils;
import joinery.DataFrame;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * @author xiejiehua
 * @DATE 8/5/2021
 */

public class YongQi {

    public static void yongQi_01() throws IOException, SQLException {

        DataFrame<Object> dataFrame_store = Store.dataFrameOfStore();
        DataFrame<Object> dataFrame_user = User.dataFrameOfUser();
        DataFrame<Object> dataFrame_pr = DataFrame.readSql(BaseDao.getConnection(), "select user_id, sum(play_minutes) as `累计游玩时间（分钟）`" +
                "from play_record pr group by user_id;");
        DataFrame<Object> dataFrame_rh = DataFrame.readSql(BaseDao.getConnection(), "select user_id, sum(paymoney) as `累计充值金额（元）` from recharge_history rh group by user_id;");

        // 充值表
        DataFrame<Object> dataFrame_rh_reindex = dataFrame_rh
                .reindex("user_id", false);

        // 游戏表
        DataFrame<Object> dataFrame_pr_reindex = dataFrame_pr
                .reindex("user_id", false);


        // 最终 店铺表
        DataFrame<Object> dataFrame_store_retain = dataFrame_store
                .retain(2, 3, 4, 5, 6, 7, 9, 14)
                .rename("username", "店铺影核号（若为空，请使用mobile_no或者email作为影核号")
                .rename("store_name", "店铺名称")
                .rename("store_address", "店铺地址")
                .rename("mobile_phone", "店铺联系电话")
                .rename("fix_phone", "店铺固定电话")
                .rename("real_name", "真实姓名")
                .rename("create_time", "店铺信息完善时间")
                .reindex("user_id", false);

        // 用户表
        DataFrame<Object> dataFrame_user_retain_create_time = dataFrame_user
                .retain("user_id", "create_time", "email", "mobile_no")
                .reindex("user_id", false)
                .join(dataFrame_store_retain, DataFrame.JoinType.LEFT)
                .rename("create_time", "账号注册时间")
                .drop("user_id_right") // 清除合并出来的
                .join(dataFrame_pr_reindex, DataFrame.JoinType.LEFT)
                .drop("user_id")
                .join(dataFrame_rh_reindex, DataFrame.JoinType.LEFT)
                .drop("user_id")
                .rename("user_id_left", "user_id");

        String file_name = "yongqi_01" + new Date().getTime() + ".xlsx";
        DataDealer.writeXls(dataFrame_user_retain_create_time, Constants.DESKTOP + file_name);
        Desktop desktop = Desktop.getDesktop();
        File file = new File(Constants.DESKTOP + file_name);
        desktop.open(file);
    }

    /*
     * @Author liu-miss
     * @Description //TODO 平台2021年截至目前，所有游戏记录根据付费方式做个区分统计（免费游戏时长，直接使用影币来进行的游戏时长，通过小时卡的游戏时长，通过月卡的游戏时长）
     * @Date  8/11/2021
     * @Param []
     * @return void
     **/
    public static void yongQi_02() throws IOException, SQLException, InterruptedException {

        // region 生成本地数据
//        获取数据之后，就可以不使用以下两条代码了;
//        GhydraOrder.dataFrameOfGhydraOrder("2021-01-01", "2021-09-01");
//        PlayRecord.dataFrameOfPlayRecord("2021-01-01", "2021-09-01");
        // endregion

        // region 生成基本dataframe数据
        DataFrame<Object> dataFrameOfStore = Store.dataFrameOfStore();
        DataFrame<Object> dataFrameOfPlayRecord_fromLocalCsv = PlayRecord.dataFrameOfPlayRecord_fromLocalCsv();
        DataFrame<Object> dataFrameOfGhydraOrder_fromLocalCsv = GhydraOrder.dataFrameOfGhydraOrder_fromLocalCsv();
        // endregion

        // region 生成基本dataframe数据
        DataFrame<Object> dataFrameOfBill = yongQi_03(dataFrameOfGhydraOrder_fromLocalCsv);
        DataFrame<Object> dataFrameOfPlayRecord_game_begin_end = yongQi_04(dataFrameOfPlayRecord_fromLocalCsv);
        // endregion 通过sql查询增加所需数据

        // region 添加店铺信息
        assert dataFrameOfPlayRecord_fromLocalCsv != null;
        DataFrame<Object> play_record_id = dataFrameOfPlayRecord_fromLocalCsv
                .retain("play_record_id", "user_id")
                .add("店铺id", "店铺名称", "店铺地址", "店铺联系电话", "店铺固话")
                ;

        // 遍历,添加Store信息
        int row_1 = 0;
        for (List<Object> objects : play_record_id) {
            // 匹配user_id
            String user_id_left = (String) objects.get(1);
            DataFrame<Object> select = dataFrameOfStore.select(value -> {
                String user_id_store = (String) value.get(2);
                return user_id_store.equals(user_id_left);
            });

            // 为空跳过
            if (select.isEmpty()) {
                row_1++;
                continue;
            }

            String user_id = (String) select.get(0, 2);
            String store_id = (String) select.get(0, 1);
            String store_name = (String) select.get(0, 4);
            String store_address = (String) select.get(0, 5);
            String mobile_phone = (String) select.get(0, 6);
            String fix_phone = (String) select.get(0, 7);

            play_record_id.set(row_1, 2, store_id);
            play_record_id.set(row_1, 3, store_name);
            play_record_id.set(row_1, 4, store_address);
            play_record_id.set(row_1, 5, mobile_phone);
            play_record_id.set(row_1, 6, fix_phone);
            row_1++;
            System.out.println("需要匹配的:" + user_id_left + " 匹配的：" + user_id + " 店铺名：" + store_name);
        }
        // endregion

        // region 最后整理
        DataFrame<Object> dataFrame = play_record_id
                .joinOn(dataFrameOfPlayRecord_game_begin_end, DataFrame.JoinType.LEFT, "play_record_id")
                .rename("play_record_id_left", "play_record_id")
                .drop("play_record_id_right")
                .joinOn(dataFrameOfBill, DataFrame.JoinType.LEFT, "play_record_id")
                .rename("play_record_id_left", "play_record_id")
                .drop("play_record_id_right")
                .rename("play_minutes", "游戏时长(min)")
                .rename("play_start_time", "游戏开始时间")
                .rename("play_finish_time", "游戏结束时间")
                .rename("group_concat(remark)", "type")
                .rename("pay_amount", "影币付费额")
                .rename("user_id", "用户id")
                ;
        // endregion

        DataDealer.writeXls(dataFrame, Constants.DESKTOP+"/pr.xlsx");
        WinUtils.wpsOpen("pr.xlsx");
    }

    /*
     * @Author liu-miss
     * @Description //TODO 通过bill history分析游戏 2021-01-01 ~ 今天的扣费类型
     * @Date  8/11/2021
     * @Param
     * @return
     **/
    public static DataFrame<Object> yongQi_03(DataFrame<Object> dataFrameOfGhydraOrder_fromLocalCsv) throws IOException, SQLException, InterruptedException {

        // 每个记录单扣了多少钱
        assert dataFrameOfGhydraOrder_fromLocalCsv != null;
        DataFrame<Object> pay_amount = dataFrameOfGhydraOrder_fromLocalCsv
                .retain("play_record_id", "pay_amount")
                .groupBy("play_record_id")
                .sum()
                ;

        // 每个记录单的扣费方式
        String sql = "select play_record_id, group_concat(remark)\n" +
                "from ghydra_order go\n" +
                "where create_time>'2021-01-01'\n" +
                "group by play_record_id\n" +
                ";";
        DataFrame<Object> concat = DataFrame.readSql(BaseDao.getConnection(), sql);

        // 修改type
        int row_01 = 0;
        for (List<Object> objects : concat) {
            Object o = objects.get(1);
            if (!(o instanceof String)) {
                System.out.println(o.toString());
                continue;
            }
            String remark = (String) o;
            ArrayList<String> type = analysisType(remark);
            concat.set(row_01, 1, type);
            row_01++;
        }

        DataFrame<Object> result = pay_amount
                .joinOn(concat, DataFrame.JoinType.LEFT, "play_record_id")
                .drop("play_record_id_right")
                .rename("play_record_id_left", "play_record_id")
                ;

        DataDealer.writeXls(result, Constants.DESKTOP + "/result.xlsx");
        WinUtils.wpsOpen("result.xlsx");

        return result;

    }

    /*
     * @Author liu-miss
     * @Description //TODO 返回 2021-01-01 ~ 今天 的数据
     * @Date  8/12/2021
     * @Param [dataFrameOfPlayRecord_fromLocalCsv]
     * @return joinery.DataFrame<java.lang.Object>
     **/
    public static DataFrame<Object> yongQi_04(DataFrame<Object> dataFrameOfPlayRecord_fromLocalCsv) throws IOException, SQLException {

        String sql = "select play_record_id, play_minutes, play_start_time, play_finish_time\n" +
                "from play_record where create_time>'2021-01-01';";
        DataFrame<Object> dataFrame = DataFrame.readSql(BaseDao.getConnection(), sql);

        return dataFrame;
    }

    public static ArrayList<String> analysisType(String remark) {

        ArrayList<String> types = new ArrayList<>();

        String keyWord_1 = "matrix_orderid";
        String keyWord_2 = "无限卡";
        String keyWord_3 = "免费游戏";

        String type_1 = "小时卡";
        String type_2 = "无限卡";
        String type_3 = "免费游戏";
        String type_4 = "影币扣费";

        if (remark.contains(keyWord_1)) {
            types.add(type_1);
        } else if (remark.contains(keyWord_2)) {
            types.add(type_2);
        } else if (remark.contains(keyWord_3)) {
            types.add(type_3);
        } else {
            types.add(type_4);
        }

        return types;
    }

        public static void main(String[] args) throws ParseException, SQLException, IOException, InterruptedException {
        yongQi_02();

    }


}