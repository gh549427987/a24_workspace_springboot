package com.jhua.role.ChecyCode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cedarsoftware.util.io.GroovyJsonReader;
import com.cedarsoftware.util.io.GroovyJsonWriter;
import com.jhua.constants.Constants;
import com.jhua.utils.DataDealer;
import joinery.DataFrame;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiejiehua
 * @DATE 8/2/2021
 */


public class SF {

    public void test() {
        String name_number = "王林    132****8801";

        Pattern compile_nam = Pattern.compile(".* ");
        Pattern compile_num = Pattern.compile(" .*");
        Matcher matcher_nam = compile_nam.matcher(name_number);
        Matcher matcher_num = compile_num.matcher(name_number);

        if(matcher_nam.find() && matcher_num.find()){

            System.out.println(matcher_nam.group(0));
            System.out.println(matcher_num.group(0));
        }
    }

    public void test_2() {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
    }

    public void login() throws InterruptedException, IOException {
        WebDriver chrome = new ChromeDriver();

        chrome.get("https://www.sf-express.com/cn/sc/");
        // 接受cookie
        chrome.findElement(By.cssSelector("#headwarp_0_cookieDialog > div.policyOperate > span.agreeCookie.cookie")).click();
        chrome.findElement(By.cssSelector(("#header > div > ul.topright > li.topli.log.quickLogin > a"))).click();

        Set<Cookie> cookies = chrome.manage().getCookies();

        //需要扫码
        while (true) {
            try {
                chrome.findElement(By.linkText("帐号登录"));
                cookies = chrome.manage().getCookies();
            } catch (Exception e) {
                System.out.println("找不到登录框，应扫完码了!");
                break;
            }
        }

        //判断文件是否存在
        File file = new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\cookies");
        String jsonstrFoo = GroovyJsonWriter.objectToJson(cookies);

        if (file.exists()) {
            System.out.println("文件存在");
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("文件创建成功");
        }

        //将list写入文件
        Writer out = new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(out);
        bw.write(jsonstrFoo);
        bw.close();

//        System.out.println(jsonstrFoo);
        chrome.close();
    }

    public void enter_UI() throws InterruptedException, IOException {
        WebDriver chrome = new ChromeDriver();
        chrome.get("https://www.sf-express.com/cn/sc/");
        // 接受cookie
        chrome.findElement(By.cssSelector("#headwarp_0_cookieDialog > div.policyOperate > span.agreeCookie.cookie")).click();

        // 读取cookie
        InputStream cookies_file = new FileInputStream(new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\cookies"));
        System.out.println(cookies_file);
        GroovyJsonReader groovyJsonReader = new GroovyJsonReader(cookies_file);

        Set<Cookie> cookies = (Set<Cookie>) groovyJsonReader.readObject();

        System.out.println("logined cookis：" + cookies);

        for (Cookie cookie : cookies) {
            chrome.manage().addCookie(cookie);
        }
        chrome.navigate().refresh();
    }

    public WebDriver enter() throws InterruptedException, IOException {
        ChromeOptions chromeOptions=new ChromeOptions();
        //设置 chrome 的无头模式
        chromeOptions.setHeadless(Boolean.TRUE);
        WebDriver chrome = new ChromeDriver(chromeOptions);
//        WebDriver chrome = new ChromeDriver();

        chrome.get("https://www.sf-express.com/cn/sc/");
        chrome.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);

        // 接受cookie
        chrome.findElement(By.cssSelector("#headwarp_0_cookieDialog > div.policyOperate > span.agreeCookie.cookie")).click();

        // 读取cookie
        InputStream cookies_file = new FileInputStream(new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\cookies"));
        System.out.println(cookies_file);
        GroovyJsonReader groovyJsonReader = new GroovyJsonReader(cookies_file);

        Set<Cookie> cookies = (Set<Cookie>) groovyJsonReader.readObject();

        System.out.println("logined cookis：" + cookies);

        for (Cookie cookie : cookies) {
            chrome.manage().addCookie(cookie);
        }
        chrome.navigate().refresh();

        return chrome;
    }

    public void select(WebDriver chrome) throws InterruptedException, IOException {

        // 进入运单页面
        chrome.findElement(By.cssSelector("#small-header > div > ul > li.waybill > a")).click();

        HashMap<String, HashMap<String, String>> yundans = new HashMap<>();
        // 逐个收集
        HashMap<String, String> yundan = new HashMap<>();
        String name = "";
        String number = "";

        //基本属性
        String yeshu_text = chrome.findElement(By.cssSelector("#function > div > div.shipping-detail-page > div.delivery-view > div.my-bills > div.bill-list.send-list > div.bootstrap-pagination > ul > li.page-desc > span")).getText().substring(2);
        yeshu_text = yeshu_text.substring(0, yeshu_text.length() - 1);
        int yeshu = Integer.parseInt(yeshu_text);
        System.out.println(yeshu);

        // 从第一页到第十页
        for (int j = 1 ; j <= yeshu; j++) {
            System.out.println("开始第" + j + "页!!!!!!!!!!");
            // 每一页，从第一个到第十个
            for (int i = 1; i < 11; i++) {

                // 清除运单
                yundan.clear();

                System.out.println("开始第" + i + "单");
                String yundan_number = "";

                // 选择进入入口
                boolean flag = false;
                try {
                    yundan_number = chrome.findElement(By.xpath("//*[@id=\"function\"]/div/div[1]/div[2]/div[1]/div[4]/div[1]/a[" + i + "]/div[1]/div[1]")).getText();
//
                    System.out.println("第一次拿订单号 " + yundan_number);
                    if (yundan_number.isEmpty()) {
                        yundan_number = chrome.findElement(By.xpath("//*[@id=\"function\"]/div/div[1]/div[2]/div[1]/div[4]/div[1]/a[" + i + "]/div[1]/div[2]")).getText();
                        yundan_number = yundan_number.substring(0, 19);
                        System.out.println("第二次拿订单号 " + yundan_number);
                        flag = true;
                    } else if (yundan_number.length() > 19) {
                        yundan_number = yundan_number.substring(0, 19);
                    }

                    // 分开点
                    if (flag) {
                        System.out.println("高端标识:" + flag);
                        chrome.findElement(By.xpath("//*[@id=\"function\"]/div/div[1]/div[2]/div[1]/div[4]/div[1]/a[" + i + "]/div[1]/div[2]")).click();
                    } else {
                        System.out.println("高端标识:" + flag);
                        chrome.findElement(By.xpath("//*[@id=\"function\"]/div/div[1]/div[2]/div[1]/div[4]/div[1]/a[" + i + "]/div[1]/div[1]")).click();
                    }


                } catch (Exception e) {
                    //找不到订单，直接输出
                    output_yundans(yundans);
                }




                String yundan_number_s = yundan_number.substring(4);
                System.out.println("订单号：" + yundan_number_s);

                chrome.findElement(By.cssSelector("#waybill-" + yundan_number_s)).click(); // 订单详情
                String name_number = chrome.findElement(By.cssSelector("#waybillDetail > div > div > dl > dt:nth-child(2) > p:nth-child(2)")).getText();
                String address = chrome.findElement(By.cssSelector("#waybillDetail > div > div > dl > dt:nth-child(2) > p:nth-child(3)")).getText();
                System.out.println(name_number);

                Pattern compile_nam = Pattern.compile(".* ");
                Pattern compile_num = Pattern.compile(" .*");
                Matcher matcher_nam = compile_nam.matcher(name_number);
                Matcher matcher_num = compile_num.matcher(name_number);

                if (matcher_nam.find() && matcher_num.find()) {
                    name = matcher_nam.group(0).trim();
                    number = matcher_num.group(0).trim();
                }

                yundan.put("name", name);
                yundan.put("number", number);
                yundan.put("address", address);

                yundans.put(yundan_number, (HashMap<String, String>) yundan.clone());

                // 退出地址栏，返回订单页
                chrome.findElement(By.xpath("/html/body/div[6]/div/div/div[1]/button")).click();
                chrome.findElement(By.cssSelector("#function > div > div.shipping-detail-page > div.delivery-view > div.route-result.clearfix > div.bill-title > a")).click();

                // 等待1秒
                Thread.sleep(1000);
            }
            // 下一页
            chrome.findElement(By.linkText("下一页")).click();

            Thread.sleep(1500);
            System.out.println("目前已经收集到的单号数量为：" + yundans.keySet().size());
        }
    }

    public HashMap readFile() throws IOException {
        File file = new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\result_json");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = bufferedReader.readLine();


        HashMap yundans = JSONObject.parseObject(s, HashMap.class);

        File file1 = new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\checy\\网易-2个海报1个传单-88个订单.xls");
        File file2 = new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\checy\\网易-易拉宝-33个订单.xls");
//        updateFile_1(yundans, file1);
        return yundans;

    }

    public void output_yundans(HashMap yundans) throws IOException {
        String s = JSON.toJSONString(yundans);
        System.out.println(s);
        //判断文件是否存在
        File file = new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\result_json");

        if (file.exists()) {
            System.out.println("文件存在");
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("文件创建成功");
        }

        //将list写入文件
        Writer out = new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(out);
        bw.write(s);
        bw.close();
    }

    public void updateFile_1(HashMap yundans, File file) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook1 = new HSSFWorkbook(fileInputStream);
        HSSFSheet sheetAt = hssfWorkbook1.getSheetAt(0);
        HSSFRow row = sheetAt.getRow(0);

        int count = 0;
        for (Row cells : sheetAt) {
            if (count == 0) {
                count++;
                continue;
            }
            String addr = String.valueOf(cells.getCell(9));


            for (Object o : yundans.keySet()) {
                String s = o.toString();

                System.out.println(s);
                break;
            }
        }

    }

    public void outputJson() throws IOException {
        HashMap hashMap = readFile();

        ArrayList<Object> objects = new ArrayList<>();


        System.out.println(hashMap.keySet().size());
        for (Object o : hashMap.keySet()) {
            objects.add(o.toString().substring(4));
        }
        System.out.println(objects.size());

        DataFrame<Object> dataFrame = new DataFrame<>();
        dataFrame.add("订单号", objects);
        dataFrame.add("name");
        dataFrame.add("number");
        dataFrame.add("address");

        int rowCount = 0;
        for (List<Object> objectList : dataFrame) {
            String from_dataframe = objectList.get(0).toString();
            for (Object o : hashMap.keySet()) {
                String from_yundans = o.toString().substring(4);
                if (from_dataframe.equals(from_yundans)) {
                    Object jsonObject = hashMap.get("运单号 " + from_dataframe);
                    String s = JSON.toJSONString(jsonObject);

                    HashMap jsonObject1 = JSONObject.parseObject(s, HashMap.class);

                    dataFrame.set(rowCount, "name", jsonObject1.get("name"));
                    dataFrame.set(rowCount, "number", jsonObject1.get("number"));
                    dataFrame.set(rowCount, "address", jsonObject1.get("address"));
                    break;
                }
            }
            rowCount++;
        }

        System.out.println(dataFrame);
        DataDealer.writeXls(dataFrame, Constants.DESKTOP + "checy_SF.xlsx");
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        SF sf = new SF();
//        sf.login();
//        sf.test();
        WebDriver enter = sf.enter();
//        sf.select(enter);
        sf.outputJson();
//        sf.test();
//        sf.outputJson();

    }
}
