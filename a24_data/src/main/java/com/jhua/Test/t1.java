package com.jhua.Test;

import com.jhua.utils.XLDownLoader;

import java.net.HttpURLConnection;
import java.net.URL;

public class t1{

    public static void main(String[] args) throws Exception {

        String downloadUrl = "https://a24.gdl.netease.com/Beat-Saber-pre-release-1.2.3.0.zip";
        String filename = downloadUrl.substring(downloadUrl
                .lastIndexOf('/') + 1);

        String path = "C:\\Users\\wb.xiejiehua\\Desktop\\Beat-Saber-pre-release-1.2.3.0.zip";

        URL url_1 = new URL("http://baishan.zamertech.com/2229VRSkater.zip");
        URL url_2 = new URL("https://a24.gdl.netease.com/Beat-Saber-pre-release-1.2.3.0.zip\n");

        HttpURLConnection conn_1 = (HttpURLConnection) url_1.openConnection();
        HttpURLConnection conn_2 = (HttpURLConnection) url_2.openConnection();
//        conn.setConnectTimeout(10000);
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Accept-Encoding", "GBK");
//        conn.setRequestProperty(
//                "Accept",
//                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//        conn.setRequestProperty("Accept-Language", "zh-CN");
//        conn.setRequestProperty("Referer", downloadUrl);
//        conn.setRequestProperty("Charset", "UTF-8");
//        conn.setRequestProperty(
//                "User-Agent",
//                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//        conn.setRequestProperty("Connection", "close");
//        conn_1.connect(); // 连接
//        conn_2.connect(); // 连接
//
//        System.out.println(conn_1.getResponseCode());
//        System.out.println(conn_1.getContentLength());
//
//        System.out.println(conn_2.getResponseCode());
//        System.out.println(conn_2.getContentLength());
//
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        Request request = new Request.Builder()
//                .url("http://baishan.zamertech.com/2229VRSkater.zip")
//                .method("GET", null)
//                .build();
//        Response response = client.newCall(request).execute();
//        long l = response.body().contentLength();
//        System.out.println(l);


        XLDownLoader instance = new XLDownLoader("C:\\Users\\wb.xiejiehua\\Desktop\\2229VRSkater.zip", "http://baishan.zamertech.com/2229VRSkater.zip", 0);
        instance.startTask(String.format("XLDownload", 1));

    }
}