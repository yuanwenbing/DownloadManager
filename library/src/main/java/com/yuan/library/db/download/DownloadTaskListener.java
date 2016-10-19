package com.yuan.library.db.download;

import java.io.File;

/**
 * Created by Yuan on 27/09/2016:10:47 AM.
 * <p/>
 * Description:com.yuan.library.db.download.DownloadTaskListener
 */

public interface DownloadTaskListener {

    /**
     * 连接中
     */
    void onConnecting(DownloadTask downloadTask);

    /**
     * 下载中
     */
    void onStart(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * 下载暂停
     */
    void onPause(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * 下载取消
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * 下载成功
     */
    void onFinish(DownloadTask downloadTask, File file);

    /**
     * 下载失败
     */
    void onError(DownloadTask downloadTask, int errorCode);

}
