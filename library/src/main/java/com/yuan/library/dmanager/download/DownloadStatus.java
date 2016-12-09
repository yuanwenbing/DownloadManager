package com.yuan.library.dmanager.download;

/**
 * Created by Yuan on 27/09/2016:10:48 AM.
 * <p/>
 * Description:com.yuan.library.dmanager.download.DownloadStatus
 */

public class DownloadStatus {

    /**
     * init download
     */
    public static final int DOWNLOAD_STATUS_INIT = 0;

    /**
     * queue download
     */
    public static final int DOWNLOAD_STATUS_QUEUE = DOWNLOAD_STATUS_INIT + 1;

    /**
     * resume download
     */
    public static final int DOWNLOAD_STATUS_CONNECTING = DOWNLOAD_STATUS_QUEUE + 1;

    /**
     * start download
     */
    public static final int DOWNLOAD_STATUS_START = DOWNLOAD_STATUS_CONNECTING + 1;

    /**
     * cancel download
     */
    public static final int DOWNLOAD_STATUS_CANCEL = DOWNLOAD_STATUS_START + 1;

    /**
     * pause download
     */
    public static final int DOWNLOAD_STATUS_PAUSE = DOWNLOAD_STATUS_CANCEL + 1;

    /**
     * request error
     */
    public static final int DOWNLOAD_STATUS_REQUEST_ERROR = DOWNLOAD_STATUS_PAUSE + 1;

    /**
     * storage error
     */
    public static final int DOWNLOAD_STATUS_STORAGE_ERROR = DOWNLOAD_STATUS_REQUEST_ERROR + 1;

    /**
     * finish download
     */
    public static final int DOWNLOAD_STATUS_FINISH = DOWNLOAD_STATUS_STORAGE_ERROR + 1;
}
