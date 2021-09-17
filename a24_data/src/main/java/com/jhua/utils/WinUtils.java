package com.jhua.utils;

import java.io.IOException;
import java.util.Timer;

/**
 * @author xiejiehua
 * @DATE 8/12/2021
 */

public class WinUtils {

    public static void wpsEnd() throws IOException {
        Runtime.getRuntime().exec("taskkill /f /im wps.exe");
    }

    public static void wpsStart(String file) throws IOException {
        System.out.println("\"D:\\A24_workSpace\\WPS Office\\11.1.0.10667\\office6\\wps.exe\" C:\\Users\\wb.xiejiehua\\Desktop\\" + file);
        Process exec = Runtime.getRuntime().exec("\"D:\\A24_workSpace\\WPS Office\\11.1.0.10667\\office6\\wps.exe\" C:\\Users\\wb.xiejiehua\\Desktop\\" + file);
        System.out.println("执行结果：" + exec.toString());
    }

    // 无视wps是否启动中，直接启动
    public static void wpsOpen(String file) throws IOException, InterruptedException {
        wpsEnd();
        Thread.sleep(1000);
        wpsStart(file);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        wpsOpen("pr.xlsx");
    }
}
