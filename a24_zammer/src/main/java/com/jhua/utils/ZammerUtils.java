package com.jhua.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhua.config.Constant;
import com.jhua.dao.GamesImagesMapper;
import com.jhua.model.GamesImages;
import com.jhua.model.ZammerGames;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */

public class ZammerUtils {


    public static Map<String, Map> game_info = null;

    public void read() throws IOException {
        File file = new File("C:\\Users\\wb.xiejiehua\\Desktop\\urls_1629020621941.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String s = bufferedReader.readLine();
        JSONObject jsonObject = JSONObject.parseObject(s);
        Map jsonObject1 = JSON.parseObject(s);

        HashMap hashMap = new HashMap<>(jsonObject1);

        for (Object o : hashMap.keySet()) {
            JSONObject o1 = (JSONObject) jsonObject1.get(o);
            Map map = (Map) o1;

            String url = (String) map.get("url");
            if (url.isEmpty()) {
                jsonObject1.remove(o);
                continue;
            }
            jsonObject1.put(o, map);

        }

        this.game_info = jsonObject1;

    }

    public List<GamesImages> getImageUrls(int game_id) {


        List<GamesImages> gamesImages = new ArrayList<>();

        try {
            URL url = new URL(Constant.ZAMMER_IMAGE_URL);
            //http://vrservice.zamertech.com/client48/ajax/vrcontent_images.aspx?contentid=2074&_=1629099967881
            //http://vrservice.zamertech.com/client48/ajax/vrcontent_images.aspx?contentid=2018&_=1629101729656
            URL dd = new URL(url, "vrcontent_images.aspx?contentid="+game_id+"&_="+new Date().getTime());
            System.out.println(dd);

            HttpURLConnection urlConnection = (HttpURLConnection) dd.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String s = bufferedReader.readLine();

            JSONObject jsonObject = JSONObject.parseObject(s);
            Object data = jsonObject.get("data");


            // json转List<GamesImages>
            gamesImages = JSONObject.parseArray(JSON.toJSONString(data), GamesImages.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return gamesImages;
    }

    public List<ZammerGames> getZammerGames(File file) throws IOException {

        ArrayList<ZammerGames> zammerGames = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            // 读取所有信息
            bufferedReader = new BufferedReader(new FileReader(file));
            System.out.println("读取文件：" + file.getAbsolutePath());
            String s = bufferedReader.readLine();
            s = s.replaceAll("=\\{", ":{");
            JSONObject allGames = JSONObject.parseObject(s);

            // 循环读取每个游戏
            for (String oneGame : allGames.keySet()) {

                // 把中文key值换成英文的
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(allGames.get(oneGame)));
                Object contentid = jsonObject.get("contentid");
                Object version_code = jsonObject.get("version_code");
                Object apiversion = jsonObject.get("apiversion");
                Object resmsg = jsonObject.get("resmsg");
                Object file_num = jsonObject.get("file_num");
                Object url = jsonObject.get("url");
                Object rescode = jsonObject.get("rescode");
                Object exe = jsonObject.get("exe文件");
                Object version_name = jsonObject.get("version_name");
                Object file_length = jsonObject.get("file_length");
                Object contentname = jsonObject.get("contentname");
                Object taskid = jsonObject.get("taskid");
                Object md5 = jsonObject.get("md5");

                jsonObject.remove("contentid");
                jsonObject.remove("apiversion");
                jsonObject.remove("resmsg");
                jsonObject.remove("file_num");
                jsonObject.remove("url");
                jsonObject.remove("rescode");
                jsonObject.remove("exe文件");
                jsonObject.remove("version_name");
                jsonObject.remove("file_length");
                jsonObject.remove("contentname");
                jsonObject.remove("taskid");

                jsonObject.put("zammerGameId", contentid);
                jsonObject.put("contentName", contentname);
                jsonObject.put("apiVersion", apiversion);
                jsonObject.put("versionName", version_name);
                jsonObject.put("resCode", rescode);
                jsonObject.put("resMsg", resmsg);
                jsonObject.put("downloadUrl", url);
                jsonObject.put("exeFiles", exe);
                jsonObject.put("fileNum", file_num);
                jsonObject.put("fileLength", file_length);
                jsonObject.put("taskId", taskid);

                // 转换成对象
                ZammerGames zammerGame = JSON.parseObject(JSON.toJSONString(jsonObject), ZammerGames.class);
                zammerGames.add(zammerGame);
//                System.out.println(zammerGame);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufferedReader.close();
        }

        return zammerGames;
    }

    public static void main(String[] args) {
        ZammerUtils zammerUtils = new ZammerUtils();
        zammerUtils.getImageUrls(2018);
    }

}
