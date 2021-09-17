package com.jhua.role;

import com.jhua.constants.Constants;
import com.jhua.dao.base.BaseDao;
import com.jhua.event.PlayRecord;
import com.jhua.event.Store;
import com.jhua.event.User;
import com.jhua.utils.DataDealer;
import com.jhua.utils.DateUtil;
import joinery.DataFrame;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class Checy {

    /*
     * @Author liu-miss
     * @Description //TODO 使用限定时间的play_record表，注意使用，读取数据库只会读取一次
     * @Date  2021/4/28
     * @Param
     * @return
     **/

    //checy查询的商家和用户的账号数量
    public static void checy01() throws IOException, ParseException {
        DataFrame<Object> dataFrame = User.dataFrameOfUser();
        DataFrame<Object> dataFrame2 = Store.dataFrameOfStore();

        Date start = DateUtil.date("2020-07-06 00:00:00");

        DataFrame<Object> create_time1 = DataDealer.dataFrameBetweenDate(start, dataFrame).count();

        System.out.println(create_time1);
        DataFrame<Object> create_time2 = DataDealer.dataFrameBetweenDate(start, dataFrame2).count();
        System.out.println(create_time2);

    }

    //checy "Hello 明天可以给我一下2.8到3.8，平台玩游戏时长超过3分钟的商家总数吗"
    public static void checy02() throws IOException {

        String startDate = "2021-02-08 00:00:00";
        String endDate = "2021-03-09 00:00:00";

        DataFrame<Object> playRecord = PlayRecord.dataFrameOfPlayRecord_Store(startDate, endDate); //拉取数据库，返回属于商家的记录
        playRecord = PlayRecord.dataFrameOfPlayRecord_Out3Min(playRecord);//返回超过3分钟的比
        playRecord = playRecord.retain("user_id", "id").groupBy("user_id").collapse().count();
        System.out.println(playRecord);
    }

    //圆梦含 拥有较长游戏使用时长，但是没有完善商家信息的账号数量；这样的商家只能玩免费游戏对吧，我们的目的是想看看这类只白嫖免费游戏的商家只是个例，还是挺普遍的。如果数量多的话，可能就要想想怎么优化了
    public static void main(String[] args) throws IOException, ParseException, SQLException {
        checy07("sv");
        checy07("cr");
    }

//    我可以看5.1-5.16玩节奏空间、荣耀擂台、源代码、逃出糖果山、怒海远征、爆裂球拍这6款游戏的影币消耗情况嘛
    public static void checy03() throws IOException, ParseException {

        String startDate = "2021-05-01 00";
        String endDate = "2021-05-17 00";

        DataFrame<Object> playRecord_store = PlayRecord.playRecord(startDate, endDate);

        Map<String, Integer> index = DataDealer.columns_index(playRecord_store);
        playRecord_store.select(value -> {
            String o = (String) value.get(index.get("游戏名"));
            boolean b = o.contains("节奏空间") || o.contains("荣耀") || o.contains("代码") || o.contains("糖果山") || o.contains("远征") || o.contains("球拍");
            return b;
        });

        DataDealer.writeXls(playRecord_store, "checy.xlsx");
    }

    public static void checy04() throws IOException, ParseException {

        String startDate = "2019-05-01 00";
        String endDate = "2021-07-17 00";

        DataFrame<Object> playRecord_store = PlayRecord.playRecord(startDate, endDate);

        Map<String, Integer> index1 = DataDealer.columns_index(playRecord_store);

        DataFrame<Object> user_id1 = playRecord_store.select(value -> {
            String user_id = (String) value.get(index1.get("user_id"));
//            return user_id.contains("15220065390")
//                    || user_id.contains("13378665744")
//                    || user_id.contains("y17724498848@163.com")
//                    || user_id.contains("yomovtianhe@163.com")
//                    || user_id.contains("chengdulinghao@163.com")
//                    || user_id.contains("xiamenlinghao@163.com");
            return user_id.contains("USR0000000455")
                    || user_id.contains("USR0000000499")
                    || user_id.contains("USR0000000500")
                    || user_id.contains("USR0000001114")
                    || user_id.contains("USR0000001276")
                    || user_id.contains("USR0000001752")
                    || user_id.contains("USR0000001753");
        });

        DataDealer.writeXls(user_id1, Constants.DESKTOP + "checy.xlsx");
    }

    // 筛选出特定商家出来
    public static void checy05() throws IOException, ParseException, SQLException {

//        DataFrame<Object> store = Store.dataFrameOfStore();
        ArrayList<String> files = new ArrayList<>();
        files.add("1-赛点上传（合作商家）.xlsx");
        files.add("2-赛点上传（二级合作伙伴）.xlsx");
        files.add("3-主动报名.xlsx");

        HashSet<String> usernames = new HashSet<>();
        HashSet<String> raw_usernames = new HashSet<>();
        int count = 0;
        for (String file : files) {
            XSSFWorkbook saidian = new XSSFWorkbook("C:\\Users\\wb.xiejiehua\\Documents\\我的POPO\\" + file);
            XSSFSheet sheet1 = saidian.getSheet("商家报名表");

            for (int row = 1; row < sheet1.getPhysicalNumberOfRows(); row++) {
                XSSFRow row_data = sheet1.getRow(row);
                raw_usernames.add(row_data.getCell(0).toString());
                usernames.add("'" + row_data.getCell(0).toString() + "'");
            }
            count++;
//            System.out.println("第" + count + "次，usernames为:" + usernames);
        }

        String saidian_usernames = usernames.toString().replace('[', '(').replace(']', ')');


        DataFrame<Object> dataFrame = DataFrame.readSql(BaseDao.getConnection(), "select * from store where username in " + saidian_usernames);
        
        //比对哪些账号不在表里
        List<Object> usernames_in_dataFrame = dataFrame.col(3);

        HashSet<String> username_not_contain = new HashSet<>();
        for (String username : raw_usernames) {

            if (!usernames_in_dataFrame.contains(username)) {
                username_not_contain.add(username);
            }
        }
        System.out.println("以下账号不存在所获取的表里" + username_not_contain);

        DataDealer.writeXls(dataFrame, "C:\\Users\\wb.xiejiehua\\Desktop\\Future.xlsx");
    }

    //
    public static void checy06() throws IOException {

        DataFrame<Object> dataFrameOfPlayRecord_Store = PlayRecord.dataFrameOfPlayRecord_Store("2020-07-22 00", "2021-08-11");

        DataFrame<Object> select = dataFrameOfPlayRecord_Store.select(value -> {
            String game_name = (String) value.get(7);
            return game_name.equals("节奏空间") || game_name.equals("Creed：荣耀擂台") || game_name.equals("Creed：荣耀擂台(局域网)") || game_name.equals("竞速达人");
        });

        DataDealer.writeXls(select, Constants.DESKTOP + "/游戏记录.xlsx");

    }

    public static String game_sql(String game, String sql) {
        if (game.equals("sv")) {
            sql.replace("creed_play_record", "sprintvector_play_record");
        }
        return sql;
    }

    /*
     * @Author liu-miss
     * @Description //TODO game_name用缩写,sv或者cr
     * @Date  8/10/2021
     * @Param [game_name]
     * @return void
     **/
    public static void checy07(String game_name) throws SQLException, IOException {

        String sql = "select css.union_id, s.sign_user_name, gu.nickname\n" +
                "from creed_play_record css\n" +
                "     left join activity_sign_up s on css.store_id = s.`sign_user_id`\n" +
                "     left join game_user gu on css.union_id = gu.union_id\n" +
                "where css.union_id != ''\n" +
                ";";

        DataFrame<Object> dataFrame_2 = DataFrame
                .readSql(BaseDao.getConnection_xiaochengxu(), game_sql(game_name, sql))
                .retain("sign_user_name", "nickname")
                ;

        DataFrame<Object> wechat_num = dataFrame_2
                .add("wechat_num") // 先加一个列用来统计数据
                .groupBy("sign_user_name", "nickname")
                .count() // 第一次统计，整合店铺名和昵称
                .drop("wechat_num") // 去掉多余的，因为每次只能count一次
                .groupBy("sign_user_name")
                .count() // 最后count一次
                ;

        // 游戏次数
        DataFrame<Object> play_times = dataFrame_2
                .groupBy("sign_user_name")
                .count()

                ;


        DataFrame<Object> result = wechat_num
                .joinOn(play_times, "sign_user_name")
                .drop("wechat_num")
                .drop("sign_user_name_right")
                .rename("sign_user_name_left", "店铺名")
                .rename("nickname_left", "微信数量")
                .rename("nickname_right", "游戏次数")
                ;

        System.out.println("==========================================================================================");
        System.out.println(result);

        DataDealer.writeXls(result, Constants.DESKTOP + "/" + game_name + ".xlsx");

    }

}
