package com.yuan.library.dmanager.download;

import java.io.File;

/**
 * Created by Yuan on 27/09/2016:10:47 AM.
 * <p/>
 * Description:com.yuan.library.dmanager.download.DownloadTaskListener
 */

public interface DownloadTaskListener {


    void onQueue(DownloadTask downloadTask);

    /**
     * connecting
     */
    void onConnecting(DownloadTask downloadTask);

    /**
     * downloading
     */
    void onStart(DownloadTask downloadTask);

    /**
     * pauseTask
     */
    void onPause(DownloadTask downloadTask);

    /**
     * cancel
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * success
     */
    void onFinish(DownloadTask downloadTask);

    /**
     * failure
     */
    void onError(DownloadTask downloadTask, int code);

}
