package com.jhua.constants;

import com.jhua.Singleton.StoreSingleton;
import joinery.DataFrame;

import java.io.IOException;
import java.sql.SQLException;

public class Constants {

    public final static String USER_DIR = System.getProperty("user.dir");
    public final static String DESKTOP = "C:\\Users\\wb.xiejiehua\\Desktop\\";
    public static final String ALL_ACCOUNTS_AFTER_SELECT_PATH = "/A24_Data/src/main/resources/LiCheng/所有账号-筛选.xlsx";
    public static final String RESOURCE_PATH = USER_DIR + "/A24_Data/src/main/resources/";
    public final static String OUT_PUT_PATH = USER_DIR+"/A24_Data/src/main/output/";
    public final static String OUT_PUT_SOURCE_PATH = USER_DIR+"/A24_Data/src/main/output/source";

    public final static String user_SelectAll = "select * from vr_game_platform.user";
    public final static String store_SelectAll = "select *\n" +
            "from vr_game_platform.store s\n" +
            "where s.store_id != 'STR0000000516'\n" +
            "  and s.store_id != 'STR0000000498'\n" +
            ";";
    public final static String playRecord_SelectAll = "select * from vr_game_platform.play_record";
    public final static String GhydraOrder_SelectAll = "select * from vr_game_platform.ghydra_order";
    public final static String rechargeHistory_SelectAll = "select * from vr_game_platform.recharge_history";
    public final static String userDiscountCardAppl_SelectAll = "select * from vr_game_platform.user_discount_card_appl";

    public final static String TIME_PATTERN_01 = "yyyy-MM-dd HH:mm:ss";

    public final static String[] unnormal_user_account_list = {"web_qa",
            "ad_nsqa",
            "mtlvr",
            "checychen0509@163.com",
            "1829102306918691989860abc1257692123@163.com",
            "c549427987@163.com",
            "15000045855",
            "aaronxuedu@163.com",
            "leefound@163.com",
            "oliviazkq@163.com",
            "zzzwt0001@163.com",
            "netviosvr@163.com",
            "18256403240",
            "13416361007",
            "18686843274",
            "zaowuworld",
            "18291023069",
            "18565658129",
            "13480945722"};

    //所购卡的信息-店铺信息
    public static final String discount_store_info = "select udca.matrix_orderid             as `ghydra订单号`,\n" +
            "       udca.user_id,\n" +
            "       udca.card_id                    as `卡ID`,\n" +
            "       dc.card_name,\n" +
            "       udca.purchase_date,\n" +
            "       udca.expire_date,\n" +
            "       udca.remain_discount_minutes    as `剩余分钟数`,\n" +
            "       udca.bind_device_udids          as `绑定设备`,\n" +
            "       udca.infinite_card_device_count as `绑定上限`,\n" +
            "       udca.create_time                as `记录创建时间`,\n" +
            "       s.username,\n" +
            "       s.store_name,\n" +
            "       s.mobile_phone,\n" +
            "       s.store_address,\n" +
            "       s.real_name\n" +
            "from user_discount_card_appl udca\n" +
            "         left join discount_card dc on udca.card_id = dc.card_id\n" +
            "         left join store s on udca.user_id = s.user_id\n" +
            "where dc.status=3\n" +
            "  and s.username not like 'web_qa%'\n" +
            "  and s.username not like 'ad_nsqa%'\n" +
            "  and s.username not like '%mtlvr%'\n" +
            "  and s.username not like '%checychen0509@163.com%'\n" +
            "  and s.username not like '%1829102306918691989860abc1257692123@163.com%'\n" +
            "  and s.username not like '%c549427987@163.com%'\n" +
            "  and s.username not like '%15000045855%'\n" +
            "  and s.username not like '%aaronxuedu@163.com%'\n" +
            "  and s.username not like '%leefound@163.com%'\n" +
            "  and s.username not like '%oliviazkq@163.com%'\n" +
            "  and s.username not like '%zzzwt0001@163.com%'\n" +
            "  and s.username not like '%netviosvr@163.com%'\n" +
            "  and s.username not like '%18256403240%'\n" +
            "  and s.username not like '%13416361007%'\n" +
            "  and s.username not like '%18686843274%'\n" +
            "  and s.username not like '%zaowuworld%'\n" +
            "  and s.username not like '%18291023069%'\n" +
            "  and s.username not like '%18565658129%'\n" +
            "  and s.username not like '%13480945722%'\n" +
            ";";

    // 计算设备数
    public static final String device_total = "  select u.user_id, d.udid as `设备udid`\n" +
            "    from user u\n" +
            "    left join device d on u.user_id = d.user_id;";

//    public final static String user_SelectAll = "select * from vr_game_platform.user where create_time>'2021-04-21 00:00:00' and create_time<'2021-04-22 00:00:00' ";
//    public final static String store_SelectAll = "select * from vr_game_platform.store where create_time>'2021-04-21 00:00:00' and create_time<'2021-04-22 00:00:00'";
//    public final static String playRecord_SelectAll = "select * from vr_game_platform.play_record where create_time>'2021-04-21 00:00:00' and create_time<'2021-04-22 00:00:00'";
//    public final static String rechargeHistory_SelectAll = "select * from vr_game_platform.recharge_history where create_time>'2021-04-21 00:00:00' and create_time<'2021-04-22 00:00:00'";
//    public final static String userDiscountCardAppl_SelectAll = "select * from vr_game_platform.user_discount_card_appl where create_time>'2021-04-21 00:00:00' and create_time<'2021-04-22 00:00:00'";

}
