package com.jhua.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jhua.dao.GamesImagesMapper;
import com.jhua.dao.ZammerGamesMapper;
import com.jhua.model.ZammerGames;
import com.jhua.service.ZammerGamesService;
import com.jhua.utils.ZammerUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */

@Service
public class ZammerGamesServiceImpl implements ZammerGamesService {

    @Autowired
    ZammerGamesMapper zammerGamesMapper;

    //新增代码 开始   注意此段
    public static ZammerGamesServiceImpl zammerGamesServiceImpl;

    @PostConstruct
    public void init() {
        System.out.println("zammerGamesMapper init");
        zammerGamesServiceImpl = this;
    }
    //新增代码 结束

    /*
     * @Author liu-miss
     * @Description //TODO 收集所有数据
     * @Date  9/1/2021
     * @Param []
     * @return int
     **/
    @Override
    public int collectZammerGameInfo() throws Exception {

        int total = 0;
        int count = 0;
        int page = 1;
//        ZammerGames zammerGames2 = zammerGamesMapper.selectByGameId(1);
        ZammerGames zammerGames1 = zammerGamesServiceImpl.zammerGamesMapper.selectByPrimaryKey(1);


        // 获取所有的id
        List<Integer> allID = zammerGamesMapper.selectByAllID();
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
                ZammerGames zammerGames = zammerGamesMapper.selectByGameId(contentid);
                if (zammerGames == null) {
                    continue;
                }
                zammerGames.setTagIds((String) game.get("tagids"));
                zammerGames.setTagNames((String) game.get("tagnames"));
                zammerGames.setSummary((String) game.get("summary"));
                zammerGames.setOnline(1);

                Integer id = zammerGames.getId();
                allID.remove(id);
                zammerGamesMapper.updateByPrimaryKey(zammerGames);

                count++;
            }

            // 遍历所有游戏
            for (Integer id : allID) {
                ZammerGames zammerGames = zammerGamesMapper.selectByPrimaryKey(id);
                zammerGames.setOnline(0);
            }
            page++;
        }
        return 0;
    }

    @Override
    @Deprecated
    public int collectZammerGameInfo(File file) throws Exception {

        ZammerUtils zammerUtils = new ZammerUtils();
        List<ZammerGames> zammerGames = zammerUtils.getZammerGames(file);


        // 先判断游戏是否已经存在数据库了
        // 不存在的话，就插入到数据库中
        for (ZammerGames zammerGame : zammerGames) {
            Integer zammerGameId = zammerGame.getZammerGameId();

            // 先查找出对应de游戏
            ZammerGames zammerGames_checkInDB = selectByGameId(zammerGameId);

            // 已经存在该游戏记录
            if (!(zammerGames_checkInDB == null)) {
                String exeFiles = zammerGames_checkInDB.getExeFiles();
                String exeFiles_fromzammerGame = zammerGame.getExeFiles();

                //且exe文件数为空
                if (exeFiles == null && exeFiles_fromzammerGame != null) {

                    Integer zammerGames_checkInDB_id = zammerGames_checkInDB.getId();
                    zammerGame.setId(zammerGames_checkInDB_id);

                    // 更新
                    zammerGamesMapper.updateByPrimaryKey(zammerGame);
                    System.out.println("已经存在该游戏");


                }

                continue;
            }

            // 数据库不存在
            zammerGamesMapper.insert(zammerGame);

        }

        return 1;
    }

    @Override
    public ZammerGames selectByGameId(int game_id) {

        // 先判断游戏是否已经存在数据库了
        ZammerGames zammerGames = zammerGamesMapper.selectByGameId(game_id);
        if (zammerGames == null) {
            return null;
        }
        return zammerGames;
    }

    @Override
    public ZammerGames selectByPrimaryKey(Integer id) {
        return zammerGamesMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Integer> selectByAllID() {
        return zammerGamesMapper.selectByAllID();
    }

    @Override
    public int updateByPrimaryKey(ZammerGames zammerGames) {
        int i = zammerGamesMapper.updateByPrimaryKey(zammerGames);
        return i;
    }

}
