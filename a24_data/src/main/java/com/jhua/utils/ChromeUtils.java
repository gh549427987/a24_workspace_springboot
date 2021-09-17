package com.jhua.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author xiejiehua
 * @DATE 8/13/2021
 */

public class ChromeUtils {
    WebDriver chrome;
    JavascriptExecutor chrome_jsExe;

    static {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    }

    public ChromeUtils(boolean displayUI) {
        if (!displayUI) {
            //设置 chrome 的无头模式
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(Boolean.TRUE);
            chrome = new ChromeDriver(chromeOptions);
        } else {
            chrome = new ChromeDriver();
        }

        chrome_jsExe= ((JavascriptExecutor) chrome);


    }

    public void test(String path) {
        chrome_jsExe.executeScript("document.getElementsByTagName('head')[0].innerHTML += '<script src=\"//open.thunderurl.com/thunder-link.js\" type=\"text/javascript\"></script>';");
//        chrome_jsExe.executeScript("var s=window.document.createElement('script');");
//        chrome_jsExe.executeScript("s.src='" + path + "';");
//        chrome_jsExe.executeScript("window.document.head.appendChild(s)");

        chrome.get("http://127.0.0.1:5500/download.html");
        System.out.println("尝试下载");
        chrome_jsExe.executeScript("        thunderLink.newTask({\n" +
                "            downloadDir: 'C:\\Users\\wb.xiejiehua\\Desktop', // 指定当前任务的下载目录名称，迅雷会在用户剩余空间最大的磁盘根目录中创建这个目录。【若不填此项，会下载到用户默认下载目录】\n" +
                "            tasks: [{\n" +
                "                name: '', // 指定下载文件名（含扩展名）。【若不填此项，将根据下载 URL 自动获取文件名】\n" +
                "                url: 'http://baishan.zamertech.com/1469Mad_Farm.zip' // 指定下载地址【必填项】\n" +
                "            }]\n" +
                "            });");

    }

    public static void main(String[] args) {
        ChromeUtils chromeUtils = new ChromeUtils(true);
        String path = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\js\\thunder-link.js";
        String path1 = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\js\\1.js";
        chromeUtils.test(path);

    }


}
