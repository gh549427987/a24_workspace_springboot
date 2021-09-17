package com.jhua.event;

import com.jhua.Singleton.UserSingleton;
import com.jhua.utils.DataDealer;
import joinery.DataFrame;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

public class UserDiscountCardAppl {

    public static DataFrame<Object> dataFrameOfUserDiscountCardAppl() throws IOException {
        return UserSingleton.dataFrameOfUser();
    }

    //卡和用户的绑定消费信息
    public static DataFrame<Object> UserDiscountCardAppl_Record() throws IOException, ParseException {
        DataFrame<Object> dataFrameOfUserDiscountCardAppl = dataFrameOfUserDiscountCardAppl();
        DataFrame<Object> dataFrameOfStore = Store.dataFrameOfStore();

//        截取必要的字段
        DataFrame<Object> dataFrameOfStore_retain = dataFrameOfStore.retain("user_id", "username", "store_name", "store_address", "mobile_phone", "fix_phone",
                "real_name");
        DataFrame<Object> dataFrameOfUserDiscountCardAppl_retain = dataFrameOfUserDiscountCardAppl.retain("matrix_orderid",
                "user_id", "card_id", "purchase_date", "expire_date", "discount_hours", "remain_discount_minutes",
                "level_up_from", "level_up_to", "bind_device_udids", "infinite_card_device_count", "status", "remark",
                "create_by", "update_by", "create_time", "update_time");

        Map<String, Integer> index_s = DataDealer.columns_index(dataFrameOfStore_retain);
        Map<String, Integer> index_udca = DataDealer.columns_index(dataFrameOfUserDiscountCardAppl_retain);

        System.out.println(dataFrameOfStore_retain);
        System.out.println(dataFrameOfUserDiscountCardAppl_retain);

        Set<Object> columns = dataFrameOfStore_retain.columns();
        System.out.println(columns);
        boolean user_id = columns.remove("user_id");
//        DataFrame<Object> add = dataFrameOfUserDiscountCardAppl_retain.add(columns.toArray());
//        System.out.println(add.columns());


        return null;
    }

    public static void main(String[] args) throws IOException, ParseException {
        DataFrame<Object> dataFrame = UserDiscountCardAppl_Record();
    }
}
