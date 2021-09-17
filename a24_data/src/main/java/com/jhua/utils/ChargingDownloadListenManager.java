package com.jhua.utils;

import java.io.File;

/**
 * @author xiejiehua
 * @DATE 8/12/2021
 */

public class ChargingDownloadListenManager {

    public static final int FAILURE = -1;

    // 单例模式
    private static ChargingDownloadListenManager ObserverMode = null;

    public static ChargingDownloadListenManager getInstance() {
        if (ObserverMode == null) {
            ObserverMode = new ChargingDownloadListenManager();
        }
        return ObserverMode;
    }

    public interface DownloadListener {
        void onDownloadSize(long size);

        void onFailed(int code);

        void onSuccess(File file);
    }

}
