package com.jhua.utils;

import com.jhua.role.Zammer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiejiehua
 * @DATE 8/12/2021
 */

public class FileDownloader {

    private static final String TAG = "FileDownloader";
    private long fileSize = 0;
    private ChargingDownloadThread thread;
    private File saveFile;
    private String downloadUrl;
    private String game_id;

    public long getContentLength() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(this.downloadUrl)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        return response.body().contentLength();
    }

    /**
     * 构建文件下载器
     *
     * @param downloadUrl 下载路径
     * @param fileSaveDir 文件保存目录
     */
    public FileDownloader(String game_id, String MD5, String downloadUrl, File fileSaveDir) {

        try {
            this.game_id = game_id;
            this.downloadUrl = downloadUrl;
            URL url = new URL(this.downloadUrl);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Referer", downloadUrl);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            conn.setRequestProperty("Connection", "close");
            conn.connect(); // 连接

            System.out.println(conn.getResponseCode());

            if (conn.getResponseCode() == 200) { // 响应成功
                this.fileSize = conn.getContentLength();// 根据响应获取文件大小
                if (this.fileSize <= 0) {
                    this.fileSize = getContentLength(); // 多尝试一下
                    if (this.fileSize <= 0){
                        throw new RuntimeException("Unkown file size ");
                    }
                }

                //测试
                String filename = this.downloadUrl.substring(this.downloadUrl
                        .lastIndexOf('/') + 1);
                this.saveFile = new File(fileSaveDir, filename);// 构建保存文件
//                this.saveFile = new File(fileSaveDir, MD5);// 构建保存文件
            } else {
                throw new RuntimeException("server no response ");
            }
        } catch (Exception e) {
            System.out.println(downloadUrl);
            System.out.println(e);
        }
    }

    /**
     * 开始下载文件
     *
     * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     * @throws Exception
     */
    public void download(List<String> list, ChargingDownloadListenManager.DownloadListener listener, String MD5) throws Exception {
        try {
            URL url = new URL(this.downloadUrl);
            this.thread = new ChargingDownloadThread(game_id, this, url,
                    this.saveFile, fileSize
                    , listener, list, MD5);
            thread.run();
        } catch (Exception e) {
            Zammer.zip_files_toDelete.add(this.saveFile.getAbsolutePath());
            throw new Exception("file download error");
        }
    }

    /**
     * 获取Http响应头字段
     *
     * @param http
     * @return
     */
    public static Map<String, String> getHttpResponseHeader(
            HttpURLConnection http) {
        Map<String, String> header = new LinkedHashMap<String, String>();
        for (int i = 0; ; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null)
                break;
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }


    /**
     * 打印Http头字段
     *
     * @param http
     */
    public static void printResponseHeader(HttpURLConnection http) {
        Map<String, String> header = getHttpResponseHeader(http);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey() + ":" : "";
            System.out.println(key + entry.getValue());
        }
    }
}
