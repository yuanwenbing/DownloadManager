package com.yuan.library.db.download;

import java.io.File;

/**
 * Created by Yuan on 27/09/2016:10:47 AM.
 * <p/>
 * Description:com.yuan.library.db.download.DownloadTaskListener
 */

public interface DownloadTaskListener {


    void onWait(DownloadTask downloadTask);

    void onCreate(DownloadTask downloadTask);
    /**
     * 下载中
     *
     * @param completedSize
     * @param totalSize
     * @param percent
     * @param downloadTask
     */
    void onStart(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    void onPause(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * 下载成功
     *
     * @param file
     * @param downloadTask
     */
    void onFinish(DownloadTask downloadTask, File file);

    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    {@link DownloadStatus}
     */
    void onError(DownloadTask downloadTask, int errorCode);
}
