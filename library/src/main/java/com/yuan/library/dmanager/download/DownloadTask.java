package com.yuan.library.dmanager.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yuan.library.BuildConfig;
import com.yuan.library.dmanager.db.DownloadDao;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Yuan on 27/09/2016:10:44 AM.
 * <p/>
 * Description:com.yuan.library.dmanager.download.DownloadTask
 */

public class DownloadTask implements Runnable {

    private OkHttpClient mClient;
    //
    private RandomAccessFile mDownLoadFile;

    private DownloadEntity dbEntity;

    private DownloadDao mDownloadDao;

    private DownloadTaskListener mListener;
    // task id
    private String mTaskId;
    // file size
    private long mTotalSize;
    // download size
    private long mCompletedSize;
    // file url
    private String mUrl;
    // file path
    private String mFilePath;
    // file name
    private String mFileName;
    // down status
    private int mDownloadStatus;
    // error code
    private int mErrorCode;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                case DownloadStatus.DOWNLOAD_STATUS_CONNECTING:
                    mListener.onConnecting(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                    mListener.onError(DownloadTask.this, mErrorCode);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_START:
                    mListener.onStart(DownloadTask.this, mCompletedSize, mTotalSize, getDownLoadPercent());
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                    mListener.onCancel(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_FINISH:
                    mListener.onFinish(DownloadTask.this, new File(getFilePath()));
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this, mCompletedSize, mTotalSize, getDownLoadPercent());
                    break;
            }
        }
    };


    private DownloadTask(Builder builder) {
        this.mTaskId = builder.id;
        this.mUrl = builder.url;
        this.mFilePath = builder.saveDirPath;
        this.mFileName = builder.fileName;
        this.mDownloadStatus = builder.downloadStatus;
        this.mTotalSize = builder.totalSize;
        this.mCompletedSize = builder.completedSize;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            // db query
            dbEntity = mDownloadDao.query(mTaskId);
            if (dbEntity != null) {
                mCompletedSize = dbEntity.getCompletedSize();
                mTotalSize = dbEntity.getTotalSize();
            }

            String filepath = getFilePath();
            mDownLoadFile = new RandomAccessFile(filepath, "rwd");

            long fileLength = mDownLoadFile.length();
            if (fileLength < mCompletedSize) {
                mCompletedSize = mDownLoadFile.length();
            }
            // if finish update status
            if (fileLength != 0 && mTotalSize <= fileLength) {
                mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_FINISH;
                mTotalSize = mCompletedSize = fileLength;
                dbEntity = new DownloadEntity(mTaskId, mTotalSize, mTotalSize, mUrl, mFilePath, mFileName, mDownloadStatus);
                mDownloadDao.update(dbEntity);
                return;
            }

            // start download
            Request request = new Request.Builder().url(mUrl).header("RANGE", "bytes=" + mCompletedSize + "-").build();
            mDownLoadFile.seek(mCompletedSize);
            Response response = mClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_START;
                if (mTotalSize <= 0) {
                    mTotalSize = responseBody.contentLength();
                }

                double updateSize = mTotalSize / 100;
                inputStream = responseBody.byteStream();
                bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[2 * 1024];
                int length;
                int buffOffset = 0;
                if (dbEntity == null) {
                    dbEntity = new DownloadEntity(mTaskId, mTotalSize, 0L, mUrl, mFilePath, mFileName, mDownloadStatus);
                    mDownloadDao.insert(dbEntity);
                }
                while ((length = bis.read(buffer)) > 0 && mDownloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL
                        && mDownloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    mDownLoadFile.write(buffer, 0, length);
                    mCompletedSize += length;
                    buffOffset += length;

                    if (buffOffset >= updateSize) {
                        buffOffset = 0;
                        dbEntity.setCompletedSize(mCompletedSize);
                        mDownloadDao.update(dbEntity);
                        handler.sendEmptyMessage(mDownloadStatus);
                    }
                }

                handler.sendEmptyMessage(mDownloadStatus);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            mErrorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            mErrorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        } finally {
            if (isFinish()) {
                handler.sendEmptyMessage(mDownloadStatus);
            }
            if (dbEntity != null) {
                dbEntity.setCompletedSize(mCompletedSize);
                dbEntity.setDownloadStatus(mDownloadStatus);
                mDownloadDao.update(dbEntity);
            }
            if (bis != null) {
                close(bis);
            }
            if (inputStream != null) {
                close(inputStream);
            }
            if (mDownLoadFile != null) {
                close(mDownLoadFile);
            }
        }
    }

    public boolean isFinish() {
        boolean finish = false;
        if (mTotalSize > 0 && mCompletedSize > 0 && mTotalSize == mCompletedSize) {
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_FINISH;
            finish = true;
        }
        return finish;
    }

    void create() {
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CONNECTING);
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_CONNECTING);
    }


    void cancel() {
        mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL;
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
    }

    void pause() {
        mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_PAUSE;
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
    }


    private String getDownLoadPercent() {
        if (mTotalSize > 0) {
            double fen = ((double) mCompletedSize / (double) mTotalSize) * 100;
            DecimalFormat df1 = new DecimalFormat("0");
            return df1.format(fen);
        }
        return "0";
    }

    private String getFilePath() {
        // default name , the name is split from url
        if (TextUtils.isEmpty(mFileName)) {
            mFileName = getFileNameFromUrl(mUrl);
        }

        // the default path
        if (TextUtils.isEmpty(mFilePath)) {
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/okhttp/download/";
        }

        File file = new File(mFilePath);
        if (!file.exists()) {
            boolean createDir = file.mkdirs();
            if (createDir) {
                if (BuildConfig.DEBUG) Log.d("DownloadTask", "create file dir success");
            }

        }
        return mFilePath + mFileName;
    }

    private String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return System.currentTimeMillis() + "";
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDownloadDao(DownloadDao mDownloadDao) {
        this.mDownloadDao = mDownloadDao;
    }

    public void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public void setListener(DownloadTaskListener listener) {
        mListener = listener;
    }

    public int getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(int mDownloadStatus) {
        this.mDownloadStatus = mDownloadStatus;
    }

    public String getTaskId() {
        return mTaskId;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public long getCompletedSize() {
        return mCompletedSize;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getSaveDirPath() {
        return mFilePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public static class Builder {
        private String id;// task mTaskId
        private String url;// file mUrl
        private String saveDirPath;// file save path
        private String fileName; // File name when saving
        private int downloadStatus;
        private long totalSize;
        private long completedSize;

        /**
         * task id
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * url
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * path
         */
        public Builder setSaveDirPath(String saveDirPath) {
            this.saveDirPath = saveDirPath;
            return this;
        }

        /**
         * state
         */
        public Builder setDownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
            return this;
        }

        /**
         * file name
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * file length
         */
        public Builder setTotalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        /**
         * file complete length
         */
        public Builder setCompletedSize(long completedSize) {
            this.completedSize = completedSize;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(this);
        }
    }

}
