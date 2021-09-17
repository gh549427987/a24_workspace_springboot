package com.jhua.event;

import com.jhua.Singleton.GhydraOrderSingleton;
import com.jhua.Singleton.PlayRecordSingleton;
import com.jhua.constants.Constants;
import joinery.DataFrame;

import java.io.File;
import java.io.IOException;

/**
 * @author xiejiehua
 * @DATE 8/11/2021
 */

public class GhydraOrder {

    // 获取PlayRecord表
    public static DataFrame<Object> dataFrameOfGhydraOrder() throws IOException {
        return GhydraOrderSingleton.dataFrameOfGhydraOrder();
    }
    public static DataFrame<Object> dataFrameOfGhydraOrder(String startDate, String endDate) throws IOException {
        return GhydraOrderSingleton.dataFrameOfGhydraOrder(startDate, endDate);
    }

    // 从本地读取数据，减轻数据库压力
    public static DataFrame<Object> dataFrameOfGhydraOrder_fromLocalCsv() throws IOException {
        File file = new File(Constants.OUT_PUT_PATH + "/source/GhydraOrder.csv");
        if (file.exists()) {
            System.out.println("读取本地文件" + file.getAbsolutePath());
            DataFrame<Object> dataFrame = DataFrame.readCsv(file.getAbsolutePath());
            return dataFrame;
        }
        return null;
    }
}
