package com.yuan.library.dmanager.download;

import java.io.File;

/**
 * Created by Yuan on 27/09/2016:10:47 AM.
 * <p/>
 * Description:com.yuan.library.dmanager.download.DownloadTaskListener
 */

public interface DownloadTaskListener {

    /**
     * connecting
     */
    void onConnecting(DownloadTask downloadTask);

    /**
     * downloading
     */
    void onStart(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * pause
     */
    void onPause(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * cancel
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * success
     */
    void onFinish(DownloadTask downloadTask, File file);

    /**
     * failure
     */
    void onError(DownloadTask downloadTask, int errorCode);

}
