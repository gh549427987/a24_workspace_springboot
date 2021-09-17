package com.jhua.utils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiejiehua
 * @DATE 8/12/2021
 */

public class ChargingDownloadManager {

    private static List<String> loadingList = new ArrayList<String>();

    public static void download(String url, File saveDir, String MD5, ChargingDownloadListenManager.DownloadListener listener) throws SQLException {
        if (!loadingList.contains(MD5)) {
            loadingList.add(MD5);
            DownloadTask task = new DownloadTask(url, saveDir, MD5, listener);
            ExecutorService instance = MyThreadPool.getInstance();
            instance.execute(task);
        }

    }

    public static void download(String game_id, String url, File saveDir, String MD5, ChargingDownloadListenManager.DownloadListener listener) throws SQLException {
        if (!loadingList.contains(MD5)) {
            loadingList.add(MD5);
            DownloadTask task = new DownloadTask(game_id, url, saveDir, MD5, listener);
            ExecutorService instance = MyThreadPool.getInstance();
            instance.execute(task);
        }

    }

    private static final class DownloadTask implements Runnable {
        private String game_id;
        private String path;
        private File saveDir;
        private String MD5;
        private ChargingDownloadListenManager.DownloadListener mDownloadListener;
        private FileDownloader loader;

        public DownloadTask(String path, File saveDir, String MD5, ChargingDownloadListenManager.DownloadListener listener) {
            this.path = path;
            this.saveDir = saveDir;
            this.MD5 = MD5;
            this.mDownloadListener = listener;
        }

        public DownloadTask(String game_id, String path, File saveDir, String MD5, ChargingDownloadListenManager.DownloadListener listener) {
            this.game_id = game_id;
            this.path = path;
            this.saveDir = saveDir;
            this.MD5 = MD5;
            this.mDownloadListener = listener;
        }

        public void run() {
            try {
                // 实例化一个文件下载器
                loader = new FileDownloader(game_id, MD5, path, saveDir);
                loader.download(loadingList,mDownloadListener,MD5);
            } catch (Exception e) {
                if (null != loadingList && loadingList.contains(MD5)) {
                    loadingList.remove(MD5);
                }
                mDownloadListener.onFailed(ChargingDownloadListenManager.FAILURE);
            }
        }
    }

}
