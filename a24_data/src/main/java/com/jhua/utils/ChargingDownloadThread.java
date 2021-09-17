package com.jhua.utils;

import com.jhua.role.Zammer;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author xiejiehua
 * @DATE 8/12/2021
 */

public class ChargingDownloadThread extends Thread {

    private static final String TAG = ChargingDownloadThread.class.getSimpleName();
    private File saveFile;
    private URL downUrl;
    private long filesize;
    private List<String> mList;
    private String MD5;
    private String game_id;
    private ChargingDownloadListenManager.DownloadListener mListener;
    private FileDownloader downloader;

    public ChargingDownloadThread(FileDownloader downloader, URL downUrl,
                                  File saveFile, long filesize, ChargingDownloadListenManager.DownloadListener listener, List<String> list, String MD5) {
        this.saveFile = saveFile;
        this.downUrl = downUrl;
        this.filesize = filesize;
        this.mList = mList;
        this.MD5 = MD5;
        this.mListener = mListener;
        this.downloader = downloader;
    }

    public ChargingDownloadThread(String game_id, FileDownloader downloader, URL downUrl,
                                  File saveFile, long filesize, ChargingDownloadListenManager.DownloadListener listener, List<String> list, String MD5) {
        this.game_id = game_id;
        this.saveFile = saveFile;
        this.downUrl = downUrl;
        this.filesize = filesize;
        this.mList = mList;
        this.MD5 = MD5;
        this.mListener = mListener;
        this.downloader = downloader;
    }

    @Override
    public String toString() {
        return "ChargingDownloadThread{" +
                "saveFile=" + saveFile +
                ", downUrl=" + downUrl +
                ", filesize=" + filesize +
                ", mList=" + mList +
                ", MD5='" + MD5 + '\'' +
                ", game_id='" + game_id + '\'' +
                ", mListener=" + mListener +
                ", downloader=" + downloader +
                '}';
    }

    public void OkHttpClient_download() throws IOException {
        boolean downFlag = true;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        System.out.println("开始下载 url：" + this.downUrl + toString());
        try {
            long filelen = 0;
            if (saveFile.isFile() && saveFile.exists()) {
                filelen = saveFile.length();
                if (filelen == filesize) {
                    downFlag = false;
                    if (mListener != null) {
                        mListener.onSuccess(saveFile);
                        System.out.println(TAG + " 文件已下载，直接回调success------------>>>>>>>>>>>>>");
                    }
                    if (null != mList && mList.contains(MD5)) {
                        mList.remove(MD5);
                    }
                }
            } else {
                saveFile.createNewFile();
            }
            if (downFlag) {
                HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
                http.setConnectTimeout(5 * 1000);
                http.setRequestMethod("GET");

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url(downUrl)
                        .method("GET", null)
                        .build();

                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();

                FileOutputStream fileOutputStream = null;
                // 得到输入流
                bis = new BufferedInputStream(inputStream);
                fileOutputStream = new FileOutputStream(saveFile.getAbsoluteFile(), true);
                bos = new BufferedOutputStream(fileOutputStream);

                byte[] buffer = new byte[4096];
                int offset = 0;
                int midset = 0;
                while ((offset = bis.read(buffer)) != -1) {
                    midset = offset;
                    bos.write(buffer, 0, offset);
                    filelen += offset;
                    if (mListener != null) {
                        mListener.onDownloadSize((filelen));// 通知目前已经下载完成的数据长度
                    }

                    if (filelen / filesize > 0.01) {
                        long l = filelen / filesize * 100;
                        BigDecimal bd = new BigDecimal(l);
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        System.out.println("游戏id:" + game_id + "下载进度: " + bd + "%");
                    }

                }
                if (filelen == filesize) {
                    mListener.onSuccess(this.saveFile);
                    System.out.println(TAG + "文件下载成功，回调success------------>>>>>>>>>>>>>");
                }
                bos.flush();
                System.out.println("download finish");
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (null != mList && mList.contains(MD5)) {
                mList.remove(MD5);
            }
            closeQuietly(bos);
            closeQuietly(bis);

        }

        // 造梦
        Zammer.analysis(game_id, saveFile);
    }

    public void HttpURLConnection_download() throws IOException {
        boolean downFlag = true;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        System.out.println("开始下载 url：" + this.downUrl);
        try {
            long filelen = 0;
            if (saveFile.isFile() && saveFile.exists()) {
                filelen = saveFile.length();
                if (filelen == filesize) {
                    downFlag = false;
                    if (mListener != null) {
                        mListener.onSuccess(saveFile);
                        System.out.println(TAG + " 文件已下载，直接回调success------------>>>>>>>>>>>>>");
                    }
                    if (null != mList && mList.contains(MD5)) {
                        mList.remove(MD5);
                    }
                }
            } else {
                saveFile.createNewFile();
            }
            if (downFlag) {
                HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
                http.setConnectTimeout(5 * 1000);
                http.setRequestMethod("GET");

                // 浏览器可接受的MIME类型
                http.setRequestProperty("Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"
                );
                http.setRequestProperty("Accept-Language", "zh-CN"); // 浏览器所希望的语言种类，当服务器能够提供一种以上的语言版本时要用到
                http.setRequestProperty("Referer", downUrl.toString());// 包含一个URL，用户从该URL代表的页面出发访问当前请求的页面。
                http.setRequestProperty("Charset", "UTF-8"); // 字符集

                http.setRequestProperty("RANGE", "bytes=" + filelen + "-");// 设置获取实体数据的范围
                // 浏览器类型，如果Servlet返回的内容与浏览器类型有关则该值非常有用。
                http.setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                http.setRequestProperty("Connection", "Keep-Alive"); // 设置为持久连接

                FileOutputStream fileOutputStream = null;
                // 得到输入流
                bis = new BufferedInputStream(http.getInputStream());
                fileOutputStream = new FileOutputStream(saveFile.getAbsoluteFile(), true);
                bos = new BufferedOutputStream(fileOutputStream);

                byte[] buffer = new byte[4096];
                int offset = 0;
                while ((offset = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, offset);
                    filelen += offset;
                    if (mListener != null) {
                        mListener.onDownloadSize((filelen));// 通知目前已经下载完成的数据长度
                    }
                }
                if (filelen == filesize) {
                    mListener.onSuccess(this.saveFile);
                    System.out.println(TAG + "文件下载成功，回调success------------>>>>>>>>>>>>>");
                }
                bos.flush();
                System.out.println("download finish");
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (null != mList && mList.contains(MD5)) {
                mList.remove(MD5);
            }
            closeQuietly(bos);
            closeQuietly(bis);

        }

        // 造梦
        Zammer.analysis(game_id, saveFile);
    }

    @SneakyThrows
    @Override
    public void run() {

//        OkHttpClient_download();

    }

    public void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable throwable) {

            }
        }
    }
}
