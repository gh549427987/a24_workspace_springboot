package com.jhua.utils;

import org.openqa.selenium.JavascriptExecutor;
import sun.security.provider.MD5;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.sql.SQLException;

/**
 * @author xiejiehua
 * @DATE 8/13/2021
 */

public class ThunderUtils {

    public static void main(String[] args) throws SQLException, ScriptException, NoSuchMethodException, FileNotFoundException, UnsupportedEncodingException {

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        if (!(engine instanceof Invocable)) {
            System.out.println("Invoking methods is not supported");
            return;
        }
        Invocable inv = (Invocable) engine;

//        MD5 md5 = new MD5();
//        ChargingDownloadManager.download("https://open.thunderurl.com/thunder-link.js", new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\js"), md5.toString(), new ChargingDownloadListenManager.DownloadListener() {
//            @Override
//            public void onDownloadSize(long size) {
//
//            }
//
//            @Override
//            public void onFailed(int code) {
//
//            }
//
//            @Override
//            public void onSuccess(File file) {
//
//            }
//        });

        String scriptPath = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\js\\thunder-link.js";
        String scriptPath_1 = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\js\\thunder-link_copy.js";
//        engine.eval("load(\"" + scriptPath + "\")");
        FileInputStream fileInputStream = new FileInputStream(new File(scriptPath_1));
        Reader scriptReader = new InputStreamReader(fileInputStream, "utf-8");

        FileReader reader = new FileReader(scriptPath_1);


        engine.eval(reader);


        Object thunder = engine.get("thunder-link");

        Object newTask = inv.invokeMethod(thunder, "newTask", "{\n" +
                "  downloadDir: 'C:\\Users\\wb.xiejiehua\\Desktop', // 指定当前任务的下载目录名称，迅雷会在用户剩余空间最大的磁盘根目录中创建这个目录。【若不填此项，会下载到用户默认下载目录】\n" +
                "  tasks: [{\n" +
                "    name: '', // 指定下载文件名（含扩展名）。【若不填此项，将根据下载 URL 自动获取文件名】\n" +
                "    url: 'http://baishan.zamertech.com/1469Mad_Farm.zip' // 指定下载地址【必填项】\n" +
                "  }]\n" +
                "}");
    }
}
