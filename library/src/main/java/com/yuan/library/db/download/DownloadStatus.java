package com.yuan.library.db.download;

/**
 * Created by Yuan on 27/09/2016:10:48 AM.
 * <p/>
 * Description:com.yuan.library.db.download.DownloadStatus
 */

public class DownloadStatus {
    /**
     * init download
     */
    public static final int DOWNLOAD_STATUS_CREATE = 0;
    /**
     * resume download
     */
    public static final int DOWNLOAD_STATUS_RESUME = 1;
    /**
     * downloading
     */
    public static final int DOWNLOAD_STATUS_START = 2;
    /**
     * download cancel
     */
    public static final int DOWNLOAD_STATUS_CANCEL = 3;
    /**
     * download error
     */
    public static final int DOWNLOAD_STATUS_ERROR = 4;
    /**
     * download finish
     */
    public static final int DOWNLOAD_STATUS_FINISH = 5;
    /**
     * download pause
     */
    public static final int DOWNLOAD_STATUS_PAUSE = 6;
    /**
     * download error file not found
     */
    public static final int DOWNLOAD_ERROR_FILE_NOT_FOUND = 7;
    /**
     * download error io exception
     */
    public static final int DOWNLOAD_ERROR_IO_ERROR = 8;

    public static final int DOWNLOAD_STATUS_WAIT = 9;
}
