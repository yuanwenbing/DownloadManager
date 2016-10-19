package com.yuan.library.db.download;

/**
 * Created by Yuan on 27/09/2016:10:48 AM.
 * <p/>
 * Description:com.yuan.library.db.download.DownloadStatus
 */

public class DownloadStatus {

    public static final int DOWNLOAD_STATUS_INIT = 0 ;
    /**
     * resume download
     */
    public static final int DOWNLOAD_STATUS_CONNECTING = DOWNLOAD_STATUS_INIT + 1;
    /**
     * downloading
     */
    public static final int DOWNLOAD_STATUS_START = DOWNLOAD_STATUS_CONNECTING + 1;
    /**
     * download cancel
     */
    public static final int DOWNLOAD_STATUS_CANCEL = DOWNLOAD_STATUS_START + 1;
    /**
     * download error
     */
    public static final int DOWNLOAD_STATUS_ERROR = DOWNLOAD_STATUS_CANCEL + 1;
    /**
     * download finish
     */
    public static final int DOWNLOAD_STATUS_FINISH = DOWNLOAD_STATUS_ERROR + 1;
    /**
     * download pause
     */
    public static final int DOWNLOAD_STATUS_PAUSE = DOWNLOAD_STATUS_FINISH + 1;
    /**
     * download error file not found
     */
    public static final int DOWNLOAD_ERROR_FILE_NOT_FOUND = DOWNLOAD_STATUS_PAUSE + 1;
    /**
     * download error io exception
     */
    public static final int DOWNLOAD_ERROR_IO_ERROR = DOWNLOAD_ERROR_FILE_NOT_FOUND + 1;

}
