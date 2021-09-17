package com.jhua.role;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhua.constants.Constants;
import com.jhua.utils.*;
import lombok.SneakyThrows;
import sun.security.provider.MD5;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * @author xiejiehua
 * @DATE 8/12/2021
 */

public class Zammer {

    public static HashSet<String> zip_files_toDelete = new HashSet<>();
    public static HashSet<String> zip_files_toDecompress = new HashSet<>();
    public static ArrayList<String> unindentifyed_game = new ArrayList<>();

    public static String tongyiTime = "create at " + new Date().getTime();

    public static Map<String, Map> game_info = null;

    public JSONObject getGames(int game_id) throws IOException {
        URL url = new URL("http://vrservice.zamertech.com/client/api/default.aspx?&action=task_get&taskid=malldown&contentid=" + game_id + "&deviceNumber=00FF6F575DD2&no=00FF6F575DD2&downtag=down40&test_category=0&t21=0");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        int responseCode = urlConnection.getResponseCode();
        if (responseCode != 200) {
            System.out.println("失败了");
            return null;
        }

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String response = bufferedReader.readLine();
        JSONObject response_j = JSONObject.parseObject(response);

        return response_j;
    }

    public HashMap<String, JSONObject> getGames() throws IOException {

        String url = "";
        String name = "";
        HashMap<String, JSONObject> urls = new HashMap<>();
        for (int i = 1; i < 3000; i++) {
            url = "";
            name = "";

            JSONObject games = getGames(i);
            urls.put(String.valueOf(i), games);

            if (games.containsKey("url")) {
                url = (String) games.get("url");
            }
            if (games.containsKey("contentname")) {
                name = (String) games.get("contentname");
            }

            System.out.println("游戏id: " + i + " 游戏名: " + name + " 游戏url: " + url);
        }
        return urls;
    }

    public void write(HashMap<String, JSONObject> games) throws IOException {

        // HashMap转json
        Map<String, Object> map = new HashMap<String, Object>(games);
        JSONObject json = new JSONObject(map);

        System.out.println(json);
        Date date = new Date();
        File file = new File("C:\\Users\\wb.xiejiehua\\Desktop\\urls_" + date.getTime() + ".txt");

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
        BufferedWriter bw = new BufferedWriter(out);
        bw.write(String.valueOf(json));
        bw.close();
    }

    /*
     * @Author liu-miss
     * @Description //TODO 返回下载信息
     * @Date  8/13/2021
     * @Param []
     * @return
     **/
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

    public void urlList() {
        for (String s : game_info.keySet()) {
            String url = (String) game_info.get(s).get("url");
            System.out.println(url);
        }
    }

    /*
     * @Author liu-miss
     * @Description //TODO 下载函数
     * @Date  8/12/2021
     * @Param [url]
     * @return void
     **/
    public void download(String game_id, String url) throws SQLException {
        MD5 md5 = new MD5();
//        url = "https://a24.gdl.netease.com/Beat-Saber-pre-release-1.2.3.0.zip";
        String dir = "C:\\Users\\wb.xiejiehua\\Desktop";
        File file = new File(dir);

        ChargingDownloadListenManager.DownloadListener downloadListener = new ChargingDownloadListenManager.DownloadListener() {
            @Override
            public void onDownloadSize(long size) {

            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onSuccess(File file) {

            }
        };
        ChargingDownloadManager.download(game_id, url, file, md5.toString(), downloadListener);

    }

    /*
     * @Author liu-miss
     * @Description //TODO 解压文件并遍历所有exe文件名
     * @Date  8/12/2021
     * @Param
     * @return
     **/
    @Deprecated
    public static void analysis(String game_id, File saveFile) throws IOException {
        // 解压
        String zip = saveFile.getAbsolutePath();
        String dest = saveFile.getAbsolutePath().substring(0, saveFile.getAbsolutePath().length() - 4);
        try {
            ZipUtil.decompress(zip, dest);
        } catch (Exception e) {
            System.out.println();
        } finally {
            zip_files_toDelete.add(zip);
        }

        // 遍历所有文件	//获取其file对象
        File file = new File(dest);
        ArrayList<String> Exe_files = new ArrayList<>();
        ArrayList<String> directory = getDirectory_forExe(file, Exe_files);

        if (!directory.isEmpty()) {
            Map game_id1 = game_info.get(game_id);
            game_id1.put("exe文件", directory);
            FileUtils.JsonWriteInFile(game_info.toString(), 0);
        }
        log_game_info();

        // 删除游戏
        FileUtils.deleteDirectory(file.getAbsolutePath());

        zip_files_toDelete.add(zip);
        System.out.println("zip_files_toDelete : " + zip_files_toDelete);
        Runtime.getRuntime().exec("taskkill /f /im " + saveFile.getAbsolutePath());

    }

    public static void analysis_onDesktop() throws IOException {
        // 解压
        getFiles_forZip(Zammer.zip_files_toDecompress);
        String dest = Constants.DESKTOP;

        // 全部解压到桌面上
        if (!zip_files_toDecompress.isEmpty()) {
            System.out.println("发现有需要解压的游戏包：" + zip_files_toDecompress);
            HashSet<String> clone = (HashSet<String>) zip_files_toDecompress.clone();
            for (String zipFilesToDecompress : clone) {
                String zip = zipFilesToDecompress;
                String destDir = "";
                int i = 0;
                i = zip.lastIndexOf("\\");
                destDir = zip.substring(0, zip.indexOf(".", i));
                try {
                    ZipUtil.decompress(zip, destDir);
                } catch (Exception e) {
                    System.out.println();
                } finally {
                    zip_files_toDelete.add(zip);//解压完之后，去除zip_files_toDecompress中的对应元素
//                    zip_files_toDecompress.remove(zip);//以及增加zip_files_toDelete上去删除

                    System.out.println("zip_files_toDelete : " + zip_files_toDelete);
                    System.out.println("zip_files_toDecompress : " + zip_files_toDecompress);
                }

                // 遍历所有文件	//获取其file对象
                File file = new File(destDir);
                ArrayList<String> Exe_files = new ArrayList<>();
                ArrayList<String> directory = getDirectory_forExe(file, Exe_files);

                // 假定游戏id最多不超过4位数
                String raw_play_id = zip.substring(i, i + 6);
                String game_id = "";
                for (int i1 = 0; i1 < raw_play_id.length(); i1++) {
                    //是数字的进入
                    if (raw_play_id.charAt(i1) >= 48 && raw_play_id.charAt(i1) <= 57) {

                        game_id += raw_play_id.charAt(i1);
                    }
                }

//                String game_id = zip.substring(i, i+6).replaceAll("[^\\d]", "");

                if (!directory.isEmpty()) {
                    Map game_id1 = game_info.get(game_id);
                    game_id1.put("exe文件", directory);
                    FileUtils.JsonWriteInFile(game_info.toString(), 1);
                }
                log_game_info();

                // 删除游戏
                FileUtils.deleteDirectory(file.getAbsolutePath());
            }

        }





    }

    /*
     * @Author liu-miss
     * @Description //TODO 找出exe
     * @Date  8/13/2021
     * @Param [file]
     * @return java.util.ArrayList<java.lang.String>
     **/
    private static ArrayList<String> getDirectory_forExe(File file, ArrayList<String> Exe_files) {

        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return null;
        }
        for (File f : flist) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
//                System.out.println("Dir==>" + f.getAbsolutePath());
                getDirectory_forExe(f, Exe_files);
            } else {
                //这里将列出所有的文件
                String absolutePath = f.getAbsolutePath();

                // 判断exe
                if (absolutePath.endsWith("exe")) {
//                    String absolutePath = f.getAbsolutePath();
                    String exe_name = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1);
                    System.out.println("file==>" + absolutePath);
                    System.out.println("file==>" + exe_name);
                    Exe_files.add(exe_name);
                }
            }
        }

        return Exe_files;
    }

    private static HashSet<String> getFiles(File dir, HashSet<String> files, String endswith) {
        File flist[] = dir.listFiles();

        for (File file : flist) {
            if (file.getAbsolutePath().endsWith(endswith)) {
                files.add(file.getAbsolutePath());
            }
        }

        return files;
    }

    private static void getFiles_forZip(HashSet<String> files) {
        getFiles(new File(Constants.DESKTOP), files, ".zip");
    }

    private void all() throws IOException {

        // 获取game_info
        read();

        //执行间断性运行 遍历分析桌面上满足条件的 桌面上只能保存游戏的zip
        new Timer().schedule(new ZammerTimerTask(), 5000);

        // 获取所有url，交给迅雷下载
//        urlList();

        // region 【废除】逐个游戏下载
//        for (String game_id : game_info.keySet()) {
//            System.out.println("游戏ID: " + game_id);
//            if (game_info.get(game_id).containsKey("url") && !game_info.get(game_id).isEmpty()) {
//                String url = (String) game_info.get(game_id).get("url");
//
//                try {
//                    download(game_id, url);
//                } catch (Exception e) {
//                }
//            } else {
//                continue;
//            }
//        }
        // endregion
    }

    class ZammerTimerTask extends TimerTask{

        public HashSet<String> zip_files_toDelete;
        public HashSet<String> zip_files_toDecompress;

        public ZammerTimerTask() {
            this.zip_files_toDelete = Zammer.zip_files_toDelete;
            this.zip_files_toDecompress = Zammer.zip_files_toDecompress;
        }

        @SneakyThrows
        @Override
        public void run() {


                // 解压 获取桌面的zip
                getFiles_forZip(Zammer.zip_files_toDelete);
                System.out.println("获取桌面的zip包：" + Zammer.zip_files_toDelete);

                System.out.println("检查可删除的zip包...");

                this.zip_files_toDelete = Zammer.zip_files_toDelete;

                if (!zip_files_toDelete.isEmpty()) {
                    System.out.println("发现有需要删除的zip包: " + zip_files_toDelete);
                    HashSet<String> clone = (HashSet<String>) zip_files_toDelete.clone();
                    for (String file : clone) {
                        try {

                            ArrayList<String> directory = null;

                            int i = 0;
                            File file1 = new File(file);
                            String zip = file1.getAbsolutePath();
                            i = zip.lastIndexOf("\\");

                            if (file1.exists()) {


                                    directory = ZipUtil.getExeFromZip(file);


                                // 假定游戏id最多不超过4位数 获取游戏id
                                String raw_play_id = zip.substring(i, i + 6);
                                String game_id = "";
                                for (int i1 = 0; i1 < raw_play_id.length(); i1++) {
                                    //是数字的进入
                                    if (raw_play_id.charAt(i1) >= 48 && raw_play_id.charAt(i1) <= 57) {

                                        game_id += raw_play_id.charAt(i1);
                                    }
                                }

                                if (!directory.isEmpty()) {
                                    Map game_id1 = game_info.get(game_id);
                                    if (!(game_id1 == null)){
                                        game_id1.put("exe文件", directory);
                                        FileUtils.JsonWriteInFile(game_info.toString(), 1);
                                    } else {
                                        unindentifyed_game.add(game_id + " "+directory);
                                        FileUtils.JsonWriteInFile(unindentifyed_game.toString(), 2);
                                    }

                                }
                                log_game_info();

                                // 删除zip包
                                Runtime.getRuntime().exec("cmd /c del \"" + file + "\"").getOutputStream();
                                zip_files_toDelete.remove(file);
                            } else {
                                zip_files_toDelete.remove(file);
                            }
                            if (file1.exists()) {
                                System.out.println("该路径文件被占用中：" + file1.getAbsolutePath());
                            }
                            } catch (Exception ignored){

                            }
                    }
                }

    //            System.out.println("检查可解压的zip包...");
    //            analysis_onDesktop();

            new Timer().schedule(new ZammerTimerTask(), 10000);
        }

    }



    private static void log_game_info() throws IOException {


        HashMap<String, Map> stringMapHashMap = new HashMap<>(game_info);
        Object clone = stringMapHashMap.clone();

        FileUtils.JsonWriteInFile(clone.toString(), 1);

        for (String s : game_info.keySet()) {
            Map map = game_info.get(s);

            if (map.containsKey("exe文件")) {
                System.out.println("======================= " + game_info.get(s) + " =======================");
            }

        }
    }

        public static void main(String[] args) throws IOException, SQLException {
            Zammer zammer = new Zammer();
//        HashMap<String, JSONObject> games = zammer.getGames();
//        zammer.write(games);
//        zammer.read();
//        zammer.read();
//        for (Object o : zammer.game_info.keySet()) {
//            Map o1 = (Map) zammer.game_info.get(o);
//            o1.put("cknima", "我想做渣男");
//        }
//        System.out.println(zammer.game_info);

//            zammer.all();

//            ArrayList<String> files = new ArrayList<>();
//            ArrayList<String> files1 = Zammer.getFiles(new File("C:\\Users\\wb.xiejiehua\\Desktop"), files, ".zip");
//            System.out.println(files1);
//

            zammer.all();
        }
    }
