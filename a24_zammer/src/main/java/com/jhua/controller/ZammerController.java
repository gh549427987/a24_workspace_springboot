package com.jhua.controller;

import com.alibaba.fastjson.JSONObject;
import com.jhua.model.ZammerGames;
import com.jhua.service.GamesImagesService;
import com.jhua.service.ZammerGamesService;
import com.jhua.service.impl.ZammerGamesServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */

@RequestMapping(value = "/zammer")
@RestController
public class ZammerController {

    @Autowired
    GamesImagesService gamesImagesService;

    @Autowired
    ZammerGamesService zammerGamesService;

    @GetMapping(value = "/getImagesInfoInDB")
    public void getZammerImageDB(@RequestParam int fromGameID, @RequestParam int toGameID) {

        try {
            System.out.println("进来：getImagesInfoInDB");
            gamesImagesService.collectGameImagesInfo(fromGameID, toGameID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/update/zammer")
    public int collectZammerGameInfo() throws Exception {

        int total = 0;
        int count = 0;
        int page = 1;

        // 获取所有的id
        List<Integer> allID = zammerGamesService.selectByAllID();
        System.out.println(allID);
        while (count <= total) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://vrservice.zamertech.com/client48/ajax/malllist.aspx?action=getList&page=" + page + "&pageSize=10&key=&tagid=0&typeid=&filtertype=&downtypeid=0&_=1629358668827")
                    .method("GET", null)
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                    .addHeader("Referer", "http://vrservice.zamertech.com/client48/zstore.aspx?tagid=0&downtype=1")
//                .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Accept-Language", "en-US,en;q=0.8")
                    .addHeader("Cookie", "client_gamelist_hotsearch=%7B%22list%22%3A%5B%5D%7D; client_pid=0; ASP.NET_SessionId=soxg0kojmml4y0y2qjapq4kq; test_category=0; test_showdevtools=0; test_showoffline=0; test_alpha=0; test_onlinepre=0; test_greenchannel1=0; test_greenchannel2=0; copyright=0; client_dn=00FF6F575DD2; client_cv=3105; old_dns=00FF6F575DD2; gri=13480945722; grp=bd59177261eee51d089892e4f0e0ed9f; mall_down_ids=2236%2C; mall_down_count=1")
                    .build();
            Response response = client.newCall(request).execute();
            String s = new String(response.body().bytes());
            JSONObject jsonObject = JSONObject.parseObject(s);
            Object data = jsonObject.get("data");

            JSONObject o = (JSONObject) JSONObject.toJSON(data);
            // 总页数
            if (total == 0) {
                total = (int) o.get("total");
            }

            // 游戏list
            List<JSONObject> list = (List<JSONObject>) o.get("list");
            for (JSONObject game : list) {

                Integer contentid = (Integer) game.get("contentid");
                ZammerGames zammerGames = zammerGamesService.selectByGameId(contentid);
                if (zammerGames == null) {
                    continue;
                }
                zammerGames.setTagIds((String) game.get("tagids"));
                zammerGames.setTagNames((String) game.get("tagnames"));
                zammerGames.setSummary((String) game.get("summary"));
                zammerGames.setOnline(1);

                Integer id = zammerGames.getId();
                allID.remove(id);
                zammerGamesService.updateByPrimaryKey(zammerGames);

                count++;
            }

            // 遍历所有游戏
            for (Integer id : allID) {
                ZammerGames zammerGames = zammerGamesService.selectByPrimaryKey(id);
                zammerGames.setOnline(0);
            }
            page++;
        }
        return 0;
    }


    @GetMapping(value = "/getZammerGamesDB")
    public void getZammerGamesDB() {


        System.out.println("进来：getZammerGamesDB");
        String path = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\YongQi";
        File src = new File(path);
        File[] files = src.listFiles();
        for (File file : files) {

            // 非.txt结尾的直接跳过
            if (!file.getAbsolutePath().endsWith(".txt") || file.getAbsolutePath().startsWith("unidentify")) {
                continue;
            }
            try {
                // 传入单个文件，只读取.txt结尾的
                int i = zammerGamesService.collectZammerGameInfo(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @GetMapping(value = "/getZammerGames")
    public void getZammerGames() throws Exception {

        ZammerGamesServiceImpl zammerGamesService = new ZammerGamesServiceImpl();
        int i = zammerGamesService.collectZammerGameInfo();

    }
}
