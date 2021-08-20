package com.jhua.fake;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhua.config.Constant;
import com.jhua.config.GameConstant;
import com.jhua.enumeration.BackendURL;
import com.jhua.enumeration.Games;
import com.jhua.enumeration.SprintVector;
import com.jhua.model.Store;
import com.jhua.model.Store;
import com.jhua.model.User;
import com.jhua.utils.HmacSHAUtils;
import com.jhua.utils.RandomUtils;
import com.jhua.vo.CreedOriginGameData;
import com.jhua.vo.GameUploadModel;
import com.jhua.vo.SprintVectorOriginGameData;
import okhttp3.*;
import sun.rmi.runtime.Log;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public class FakeGameDataUpload {

    static String backendURL = FakeConfig.backendURL;

    /*
     * @Author liu-miss
     * @Description //TODO 使用方法
     *                 1. 创建自定义的执行类 play_vx()
     *                  2.先创建神类(本类FakeGameDataUpload)
     *                      FakeGameDataUpload fakeGameDataUpload = new FakeGameDataUpload();
     *                      fakeGameDataUpload.play_v1(fakeGameDataUpload);
     *                  3. 调用整合区方法：
     *                      SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(0));
     * @Date  8/18/2021
     * @Param
     * @return
     **/

    // region 基本数据加密区
    /*
     * @Author liu-miss
     * @Description //TODO 给body添加签名
     * @Date  8/17/2021
     * @Param [raw, apiSecret]
     * @return com.alibaba.fastjson.JSONObject
     **/
    JSONObject sign(JSONObject postData, String apiSecret) throws NoSuchAlgorithmException, InvalidKeyException {

        List<String> objects = new ArrayList<>();
        for (String s : postData.keySet()) {
            // 除了嵌套式结构体字段如game_data字段, 除了appkey, sign字段
            if (s.equals("game_data") || s.equals("appkey") || s.equals("sign")) {
                continue;
            }

            Object value = postData.get(s);
            String s1 = JSONObject.toJSONString(value);
            String s2 = s + "=" + s1.replace("\"", "");

            objects.add(s2);
        }

        // Step：1 正向排序
        objects.sort(Comparator.naturalOrder());

        // Step：2 拼接sortedQueryString1
        String[] strings = objects.toArray(new String[objects.size()]);
        String sortedQueryString1 = String.join("&", strings);
        System.out.println("拼接sortedQueryString1: " + sortedQueryString1);

        // Step：3 拼接拼接sortedQueryString2
        JSONObject game_data = (JSONObject) postData.get("game_data");
        List<String> objects2 = new ArrayList<>();
        for (String s : game_data.keySet()) {
            // 除了嵌套式结构体字段如game_data字段, 除了appkey, sign字段
            Object value = game_data.get(s);

            if (s.equals("game_data") || s.equals("appkey") || s.equals("sign") || value == null) {

                continue;
            }

            String s1 = JSONObject.toJSONString(value);
            String s2 = s + "=" + s1.replace("\"", "");

            objects2.add(s2);
        }
        System.out.println(objects2);

        // Step：4 正向排序
        objects2.sort(Comparator.naturalOrder());

        // Step：3 拼接拼接sortedQueryString2
        String[] strings2 = objects2.toArray(new String[objects2.size()]);
        String sortedQueryString2 = String.join("&", strings2);
        System.out.println("拼接sortedQueryString2: " + sortedQueryString2);

        // 拼接所有字符串，获得sortedQueryString
        String sortedQueryString = sortedQueryString1 + '&' + sortedQueryString2 + "&key=" + apiSecret;
        System.out.println("拼接sortedQueryString: " + sortedQueryString);


        // 加密
        String hash = HmacSHAUtils.hmacSHA256(sortedQueryString, apiSecret);

        postData.put("sign", hash);

        return postData;
    }

    /*
     * @Author liu-miss
     * @Description //TODO base64加密店铺信息
     * @Date  8/17/2021
     * @Param [user]
     * @return java.lang.String
     **/
    String extra_data(Store store) {
        
        String originalInput = "";
        String username = store.getAccountId();
        String storeName = store.getStoreName();
        String storeAddress = store.getStoreAddress();
        String wechat_mp_vr = store.getLogin_channel();

        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(store);

        List<String> objects = new ArrayList<>();
        for (String s : jsonObject.keySet()) {
            // 除了嵌套式结构体字段如game_data字段, 除了appkey, sign字段
            if (s.equals("game_data") || s.equals("appkey") || s.equals("sign")) {
                continue;
            }

            Object value = jsonObject.get(s);
            String s1 = JSONObject.toJSONString(value);
            String s2 = s + "=" + s1.replace("\"", "");

            objects.add(s2);
        }

        objects.sort(Comparator.naturalOrder());

        String sortedQueryString2 = String.join("&", objects);

        String encodedString = Base64.getEncoder().encodeToString(sortedQueryString2.getBytes());

        return encodedString;
    }

    // endregion

    // region 基本用户和店铺数据获得区
    User getUser(String user_id) throws IOException {

        if (!user_id.startsWith("USR") || user_id.length() != "USR0000000028".length()) {
            return null;
        } else if (FakeConfig.auth.equals("")) {
            LoginAuth.getLoginAuth();//先登录
            System.out.println("先登录");
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"user_id\":\"" + user_id + "\"}");

        System.out.println("进入后台获取user:" + backendURL);
        Request request = new Request.Builder()
                .url(backendURL + "/admin/user/getUser")
                .method("POST", body)
                .addHeader("Authorization", FakeConfig.auth)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", "Authorization=" + FakeConfig.auth) // 添加鉴权
                .build();
        Response response = client.newCall(request).execute();

        assert response.body() != null;
        String string = response.body().string();

        System.out.println(string);

        JSONObject jsonObject = JSONObject.parseObject(string);

        JSONObject data = (JSONObject) jsonObject.get("data");

        User user = JSON.parseObject(JSON.toJSONString(data), User.class);

        return user;
    }

    Store getStore(String store_id) throws IOException {

        if (!store_id.startsWith("STR") || store_id.length() != "STR0000000032".length()) {
            return null;
        } else if (FakeConfig.auth.equals("")) {
            LoginAuth.getLoginAuth();//先登录
            System.out.println("先登录");
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"storeId\":\"" + store_id + "\"}");

        System.out.println("进入后台获取store:" + backendURL);
        Request request = new Request.Builder()
                .url(backendURL + "/admin/store/getStore")
                .method("POST", body)
                .addHeader("Authorization", FakeConfig.auth)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", "Authorization=" + FakeConfig.auth) // 添加鉴权
                .build();
        Response response = client.newCall(request).execute();

        assert response.body() != null;
        String string = response.body().string();

        System.out.println(string);

        JSONObject jsonObject = JSONObject.parseObject(string);

        JSONObject data = (JSONObject) jsonObject.get("data");

        String string1 = JSON.toJSONString(data).replace("createBy", "accountId");

        Store store = JSON.parseObject(string1, Store.class);
        store.setAccountId(store.getAccountId() + "163.com");

        return store;
    }

    // endregion

    // region game_data 所有游戏类型 创建区
    /*
     * @Author liu-miss
     * @Description //TODO 返回 竞速达人 json字符串
     * @Date  8/17/2021
     * @Param [user, EventType]
     * @return java.lang.String
     **/
    JSONObject createGameData_SV_Start(SprintVectorOriginGameData sprintVectorOriginGameData, Store store) {

//        "user_id": "25",
        sprintVectorOriginGameData.setUser_id(store.getUserId());

//        "time": "1626691092",
        sprintVectorOriginGameData.setTime(new Date().getTime() / 1000);

//        "event_name": "match_start",
        sprintVectorOriginGameData.setEvent_name(SprintVector.SV_EVENT_START.event);

        // time_since_race_started
        sprintVectorOriginGameData.setTime_since_race_started("5");

        // finished_match 结束事件 true 开始时间 false
        sprintVectorOriginGameData.setFinished_match("false");

//      "ranking":"0",
        sprintVectorOriginGameData.setRanking(String.valueOf(RandomUtils.RandomInt(1, 5)));

//      "speed":"1993.64917",
        sprintVectorOriginGameData.setSpeed(String.valueOf(RandomUtils.RandomInt(1700, 2000)));


        JSONObject o = (JSONObject) JSONObject.toJSON(sprintVectorOriginGameData);

        return o;
    }

    JSONObject createGameData_SV_End(SprintVectorOriginGameData sprintVectorOriginGameData, Store store) {

        String s = String.valueOf(RandomUtils.RandomInt(130, 500));
        // time_since_race_started 重点，此处设置成绩
        sprintVectorOriginGameData.setTime_since_race_started(s);



//        "user_id": "25",
        sprintVectorOriginGameData.setUser_id(store.getUserId());

//        "time": "1626691092",
        sprintVectorOriginGameData.setTime(new Date().getTime() / 1000);

//        "event_name": "match_start",
        sprintVectorOriginGameData.setEvent_name(SprintVector.SV_EVENT_END.event);

        // finished_match 结束事件 true 开始时间 false
        sprintVectorOriginGameData.setFinished_match("true");

//      "ranking":"0",
        sprintVectorOriginGameData.setRanking(String.valueOf(RandomUtils.RandomInt(1, 5)));

//      "speed":"1993.64917",
        sprintVectorOriginGameData.setSpeed(String.valueOf(RandomUtils.RandomInt(1700, 2000)));

//        endTime
        sprintVectorOriginGameData.setTime(sprintVectorOriginGameData.getTime() + Long.parseLong(s));
        JSONObject o = (JSONObject) JSONObject.toJSON(sprintVectorOriginGameData);

        return o;
    }

    /*
     * @Author liu-miss
     * @Description //TODO 返回 Creed json字符串
     * @Date  8/17/2021
     * @Param [user]
     * @return java.lang.String
     **/
    JSONObject createGameData_CR(CreedOriginGameData creedOriginGameData) {

//        "fightType": 1,
        creedOriginGameData.setFightType((Integer) RandomUtils.RandomArray(FakeConfig.fightType_CR));
//        "playerWon": 1,
        creedOriginGameData.setPlayerWon((Integer) RandomUtils.RandomArray(FakeConfig.playerWon_CR));
//        "opponentUnionId": "",
//        "playerActor": "Ricky_Conlan",
        creedOriginGameData.setPlayerActor((String) RandomUtils.RandomArray(FakeConfig.playerActor_CR));
//        "opponentActor": "Adonis_Creed",
        creedOriginGameData.setOpponentActor((String) RandomUtils.RandomArray(FakeConfig.playerActor_CR));

//        "playerHitsBlocked": 33,
        creedOriginGameData.setPlayerHitsBlocked(RandomUtils.RandomInt(20, 70));
//        "opponentHitsReceived": 6,
        creedOriginGameData.setOpponentHitsReceived(RandomUtils.RandomInt(20, 70));
//        "opponentHitsBlocked": 1,
        creedOriginGameData.setOpponentHitsBlocked(RandomUtils.RandomInt(20, 70));
//        "playerKnockedDown": 5,
        creedOriginGameData.setPlayerKnockedDown(RandomUtils.RandomInt(0, 5));
//        "playerWasKnockedDown": 0,
        creedOriginGameData.setPlayerWasKnockedDown(RandomUtils.RandomInt(0, 5));
//        "begin": 0,
        creedOriginGameData.setBegin(0);
//        "end": 0
        creedOriginGameData.setEnd(0);

//        String string = JSONObject.toJSONString(creedOriginGameData);
        JSONObject o = (JSONObject) JSONObject.toJSON(creedOriginGameData);

        return o;
    }
    // endregion

    // region post请求的body 创建区
    /*
     * @Author liu-miss
     * @Description //TODO 初始化发送body
     * @Date  8/17/2021
     * @Param [games]
     * @return com.jhua.vo.GameUploadModel
     **/
    GameUploadModel initGameUpload(Games games, GameUploadModel gameUploadModel) {

        if (games.equals(Games.SprintVector)) {
            gameUploadModel.setApp(GameConstant.SV_app);
            gameUploadModel.setAppkey(GameConstant.SV_appkey);
            gameUploadModel.setApp_channel(GameConstant.app_channel);
        } else if (games.equals(Games.Creed)) {
            gameUploadModel.setApp(GameConstant.CR_app);
            gameUploadModel.setAppkey(GameConstant.CR_appkey);
            gameUploadModel.setApp_channel(GameConstant.app_channel);
        }

        return gameUploadModel;
    }

    String createPostData_CR(String unionID, Store store, CreedOriginGameData creedOriginGameData) throws InvalidKeyException, NoSuchAlgorithmException {

        GameUploadModel gameUploadModel = new GameUploadModel();

        // 添加店铺信息
        assert false;
        gameUploadModel.setExtra_data(extra_data(store));

        gameUploadModel = initGameUpload(Games.Creed, gameUploadModel);
        gameUploadModel.setGame_data(createGameData_CR(creedOriginGameData));
        gameUploadModel.setUnionid(unionID);

        // 签名
        JSONObject o = (JSONObject) JSONObject.toJSON(gameUploadModel);
        JSONObject signed = sign(o, GameConstant.CR_apiSecret);
        String string = JSON.toJSONString(signed);

        gameUploadModel = JSONObject.parseObject(string, GameUploadModel.class);

        String s = JSONObject.toJSONString(gameUploadModel);

        return s;

    }

    String createPostData_SV(String unionID, Store store, SprintVectorOriginGameData sprintVectorOriginGameData, Games games) throws InvalidKeyException, NoSuchAlgorithmException {

        GameUploadModel gameUploadModel = new GameUploadModel();

        // 添加店铺信息
        assert false;
        gameUploadModel.setExtra_data(extra_data(store));

        if (games.equals(Games.SprintVector_begin)) {
            gameUploadModel = initGameUpload(Games.SprintVector, gameUploadModel);

            gameUploadModel.setGame_data(createGameData_SV_Start(sprintVectorOriginGameData, store));
            gameUploadModel.setUnionid(unionID);

            // 签名
            JSONObject o = (JSONObject) JSONObject.toJSON(gameUploadModel);
            JSONObject signed = sign(o, GameConstant.SV_apiSecret);
            String string = JSON.toJSONString(signed);

            gameUploadModel = JSONObject.parseObject(string, GameUploadModel.class);

        } else if (games.equals(Games.SprintVector_end)) {
            gameUploadModel = initGameUpload(Games.SprintVector, gameUploadModel);
            gameUploadModel.setGame_data(createGameData_SV_End(sprintVectorOriginGameData, store));
            gameUploadModel.setUnionid(unionID);

            // 签名
            JSONObject o = (JSONObject) JSONObject.toJSON(gameUploadModel);
            JSONObject signed = sign(o, GameConstant.SV_apiSecret);
            String string = JSON.toJSONString(signed);

            gameUploadModel = JSONObject.parseObject(string, GameUploadModel.class);

        }


        String string = JSONObject.toJSONString(gameUploadModel);

        return string;
    }

    // endregion

    // region 所有游戏类型的游戏整合区 以及 基本发送方法 整合区
    JSONObject sendPost(String data) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, data);
            System.out.println("往后端发送请求：" + FakeConfig.xc_backendURL);
            Request request = new Request.Builder()
                    .url(FakeConfig.xc_backendURL + "/wxapp/sync/game/play")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(data);
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            System.out.println(jsonObject);

            return jsonObject;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean SprintVectorPlay(FakeGameDataUpload god, String unionID, Store store, String map) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {

        // SV 游戏数据
        SprintVectorOriginGameData sprintVectorOriginGameData = new SprintVectorOriginGameData(map);

        String sprintVector_begin = god.createPostData_SV(unionID, store, sprintVectorOriginGameData, Games.SprintVector_begin);
        String sprintVector_end = god.createPostData_SV(unionID, store, sprintVectorOriginGameData, Games.SprintVector_end);

        JSONObject begin = god.sendPost(sprintVector_begin);
        TimeUnit.SECONDS.sleep(1);
        JSONObject end = god.sendPost(sprintVector_end);

        if (begin.get("success").equals(true) && end.get("success").equals(true)) {
            System.out.println("上帝看着[" + unionID + "]这位妹子在[" + store.storeName + "]这家店玩了一次SprintVector");
            return true;
        }
        System.out.println("[" + unionID + "]这位妹子在[" + store.storeName + "]这家店玩 SprintVector 玩不了！！！！！！！！");
        return false;
    }

    boolean CreedPlay(FakeGameDataUpload god, String unionID, Store store) throws NoSuchAlgorithmException, InvalidKeyException {

        CreedOriginGameData creedOriginGameData = new CreedOriginGameData();

        String creed = god.createPostData_CR(unionID, store, creedOriginGameData);
        JSONObject sendPost = god.sendPost(creed);

        if (sendPost.get("success").equals(true)) {
            System.out.println("上帝看着[" + unionID + "]这位兄弟在[" + store.storeName + "]这家店玩了一次Creed");
            return true;
        }
        System.out.println("[" + unionID + "]这位妹子在[" + store.storeName + "]这家店玩 Creed 玩不了！！！！！！！！");
        return false;
    }

    // endregion

    /*
     * @Author liu-miss
     * @Description //TODO [上帝] 看着 [某个人] 在 [某家店] 玩了 [某款游戏]
     * @Date  8/17/2021
     * @Param [unionID, store, games]
     * @return int
     **/
    void play(FakeGameDataUpload god, String unionID, Store store) {

        try {

            god.SprintVectorPlay(god, unionID, store, GameConstant.SV_maps.get(0));

            god.CreedPlay(god, unionID, store);

        } catch (NoSuchAlgorithmException | InvalidKeyException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    void play_v1(FakeGameDataUpload fakeGameDataUpload) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException {

        String unionID = FakeUnionID.OnlineUnionID.get(1);
        String storeID = FakeStore.TestFakeStoreList.get(0);

        Store store = getStore(storeID);

        // 跑两个地图，进入总排行
        SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(0));
        SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(1));

    }

    void play_v2_SV(FakeGameDataUpload fakeGameDataUpload) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException {

        Iterator<String> unionID_iterator = FakeUnionID.OnlineUnionID.iterator();

        int count = 0;
        while (unionID_iterator.hasNext() && count < 39) {

            String unionID = "";
            Store store = null;

            // 东部
            for (String storeID : FakeStore.OnlineFakeStoreList_E) {

                if (count > 8) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(0));
                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(1));

                count++;
            }

            // 南部
            for (String storeID : FakeStore.OnlineFakeStoreList_S) {

                if (count > 10) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(0));
                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(1));

                count++;
            }

            // 西部
            for (String storeID : FakeStore.OnlineFakeStoreList_W) {

                if (count > 10) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(0));
                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(1));

                count++;
            }

            // 北部
            for (String storeID : FakeStore.OnlineFakeStoreList_N) {

                if (count > 10) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(0));
                SprintVectorPlay(fakeGameDataUpload, unionID, store, GameConstant.SV_maps.get(1));

                count++;
            }

        }

    }

    void play_v3_CR(FakeGameDataUpload fakeGameDataUpload) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException {

        Iterator<String> unionID_iterator = FakeUnionID.OnlineUnionID.iterator();

        int count = 0;
        int e = 0;
        int s = 0;
        int w = 0;
        int n = 0;
        while (unionID_iterator.hasNext() && count < 39) {

            String unionID = "";
            Store store = null;

            // 东部

            for (String storeID : FakeStore.OnlineFakeStoreList_E) {

                if (e > 5) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                for (int i = 0; i < RandomUtils.RandomInt(1, 5); i++) {
                    CreedPlay(fakeGameDataUpload, unionID, store);
                }

                e++;
                count++;
            }

            // 南部

            for (String storeID : FakeStore.OnlineFakeStoreList_S) {

                if (s > 10) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                for (int i = 0; i < RandomUtils.RandomInt(1, 5); i++) {
                    CreedPlay(fakeGameDataUpload, unionID, store);
                }

                s++;
                count++;
            }

            // 西部

            for (String storeID : FakeStore.OnlineFakeStoreList_W) {

                if (w > 8) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                for (int i = 0; i < RandomUtils.RandomInt(1, 5); i++) {
                    CreedPlay(fakeGameDataUpload, unionID, store);
                }

                w++;
                count++;
            }

            // 北部

            for (String storeID : FakeStore.OnlineFakeStoreList_N) {

                if (n > 15) {
                    break;
                }

                unionID = unionID_iterator.next();
                store = getStore(storeID);

                for (int i = 0; i < RandomUtils.RandomInt(1, 5); i++) {
                    CreedPlay(fakeGameDataUpload, unionID, store);
                }

                n++;
                count++;
            }

        }

    }

    /*
     * @Author liu-miss
     * @Description //TODO 使用之前，记得先去FakeConfig下配置好环境
     * @Date  8/18/2021
     * @Param [args]
     * @return void
     **/
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {


        FakeGameDataUpload fakeGameDataUpload = new FakeGameDataUpload();

        fakeGameDataUpload.play_v3_CR(fakeGameDataUpload);

    }

}
