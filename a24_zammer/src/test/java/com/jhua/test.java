package com.jhua;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhua.config.Constant;
import com.jhua.dao.GamesImagesMapper;
import com.jhua.dao.ZammerGamesMapper;
import com.jhua.model.GamesImages;
import com.jhua.model.ZammerGames;
import com.jhua.service.impl.ZammerGamesServiceImpl;
import com.jhua.utils.ZammerUtils;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */

@MapperScan({"com.jhua.dao"})
@SpringBootTest
public class test {

    @Autowired
    ZammerGamesMapper zammerGamesMapper;

    @Autowired
    GamesImagesMapper gamesImagesMapper;

    @Test
    public void t1() throws IOException {
        String info = "{\"2087\":{\"contentid\":\"2087\",\"version_code\":101,\"apiversion\":\"40\",\"resmsg\":\"获取成功\",\"file_num\":491,\"url\":\"http://baishan.zamertech.com/2087FoodGirlsVR101U3D.zip\",\"rescode\":\"0\",\"exe文件\":[\"FoodGirlsBDS.exe\",\"FoodGirlsBDS.exe\",\"UnityCrashHandler64.exe\",\"UnityCrashHandler64.exe\"],\"version_name\":\"1.01\",\"file_length\":613179019,\"contentname\":\"小圆的手摇奶茶店\",\"taskid\":\"\",\"md5\":\"0BEA00C3B1620762207BC5906182A000\"}";
        String file = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\YongQi\\1629010810668game_exes.txt";

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
        String s = bufferedReader.readLine();
        JSONObject jsonObject = JSONObject.parseObject(s);
//        System.out.println(jsonObject.keySet());
        JSONObject o = (JSONObject) jsonObject.get("2087");

        ZammerGames zammerGames = new ZammerGames();
        int contentid = Integer.parseInt(o.getString("contentid"));
//        int contentid = Integer.parseInt(o.getString("apiversion"));
//        int contentid = Integer.parseInt(o.getString("resmsg"));
//        int contentid = Integer.parseInt(o.getString("url"));
//        int contentid = Integer.parseInt(o.getString("exe文件"));
//        int contentid = Integer.parseInt(o.getString("file_length"));
//        int contentid = Integer.parseInt(o.getString("file_num"));
//        int contentid = Integer.parseInt(o.getString("contentname"));
//        int contentid = Integer.parseInt(o.getString("version_name"));
        zammerGames.setZammerGameId(contentid);
        System.out.println(zammerGames.getZammerGameId());
        zammerGamesMapper.insert(zammerGames);
    }

    @Test
    void t2() {

        ZammerUtils zammerUtils = new ZammerUtils();

        ZammerGames zammerGames = zammerGamesMapper.selectByGameId(2087);
        System.out.println(zammerGames);
        ZammerGames zammerGames2 = zammerGamesMapper.selectByGameId(11111);
        System.out.println(zammerGames2);

    }

    @Test
    static void t3() throws IOException {
        BufferedReader bufferedReader = null;
        File file = new File("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\YongQi\\create at 1629020832657game_exes.txt");

        // 读取所有信息
        bufferedReader = new BufferedReader(new FileReader(file));
        System.out.println("读取文件：" + file.getAbsolutePath());
        String s = bufferedReader.readLine();
        String s1 = s.replaceAll("=\\{", ":{");
        System.out.println(s1);
        JSONObject allGames = JSONObject.parseObject(s1);
        System.out.println(allGames);
    }

    @Test
    void t4() throws Exception {

        ZammerGamesServiceImpl zammerGamesService = new ZammerGamesServiceImpl();
        ZammerGames zammerGames = zammerGamesMapper.selectByGameId(1220);
        System.out.println(zammerGames);
        int i = zammerGamesService.collectZammerGameInfo();
    }

    @Test
    void t5() throws Exception {

        gamesImagesMapper.selectByGameId(1);
    }



    public static void main(String[] args) throws IOException {

        t3();
    }
}
