package com.yuan.library.db.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yuan.library.BuildConfig;
import com.yuan.library.db.DownloadDao;
import com.yuan.library.db.DownloadEntity;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Yuan on 27/09/2016:10:44 AM.
 * <p/>
 * Description:com.yuan.library.db.download.DownloadTask
 */

public class DownloadTask implements Runnable {
    // request 框架
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
    private String mSaveDirPath;
    // file name
    private String fileName;
    // down status
    private int downloadStatus;
    // error code
    private int errorCode;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                // 创建
                case DownloadStatus.DOWNLOAD_STATUS_CREATE:
                    mListener.onCreate(DownloadTask.this);
                    break;
                // 等待
                case DownloadStatus.DOWNLOAD_STATUS_WAIT:
                    mListener.onWait(DownloadTask.this);
                    break;
                // 失败
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                    mListener.onError(DownloadTask.this, errorCode);
                    break;
                // 开始
                case DownloadStatus.DOWNLOAD_STATUS_START:
                    mListener.onStart(DownloadTask.this, mCompletedSize, mTotalSize, getDownLoadPercent());
                    break;
                // 取消
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                    mListener.onCancel(DownloadTask.this);
                    break;
                // 完成
                case DownloadStatus.DOWNLOAD_STATUS_FINISH:
                    mListener.onFinish(DownloadTask.this, new File(getFilePath()));
                    break;
                // 停止
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this, mCompletedSize, mTotalSize, getDownLoadPercent());
                    break;
            }
        }
    };


    private DownloadTask(Builder builder) {
        mClient = new OkHttpClient();
        this.mTaskId = builder.id;
        this.mUrl = builder.url;
        this.mSaveDirPath = builder.saveDirPath;
        this.fileName = builder.fileName;
        this.downloadStatus = builder.downloadStatus;
        this.mTotalSize = builder.totalSize;
        this.mCompletedSize = builder.completedSize;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            // 数据库中加载数据
            dbEntity = mDownloadDao.query(mTaskId);
            if (dbEntity != null) {
                mCompletedSize = dbEntity.getCompletedSize();
                mTotalSize = dbEntity.getTotalSize();
            }

            // 获得文件路径
            String filepath = getFilePath();
            // 获得下载保存文件
            mDownLoadFile = new RandomAccessFile(filepath, "rwd");

            long fileLength = mDownLoadFile.length();
            if (fileLength < mCompletedSize) {
                mCompletedSize = mDownLoadFile.length();
            }
            // 下载完成，更新数据库数据
            if (fileLength != 0 && mTotalSize <= fileLength) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_FINISH;
                mTotalSize = mCompletedSize = fileLength;
                dbEntity = new DownloadEntity(mTaskId, mTotalSize, mTotalSize, mUrl, mSaveDirPath, fileName, downloadStatus);
                mDownloadDao.update(dbEntity);
                return;
            }

            // 开始下载
            Request request = new Request.Builder().url(mUrl).header("RANGE",
                    "bytes=" + mCompletedSize + "-") // Http value set breakpoints RANGE
                    .build();
            // 文件跳转到指定位置开始写入
            mDownLoadFile.seek(mCompletedSize);
            Response response = mClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_START;
                //onCallBack();
                if (mTotalSize <= 0) {
                    mTotalSize = responseBody.contentLength();
                }

                double updateSize = mTotalSize / 100;

                // 获得文件流
                inputStream = responseBody.byteStream();
                bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[2 * 1024];
                int length = 0;
                int buffOffset = 0;
                // 开始下载数据库中插入下载信息
                if (dbEntity == null) {
                    dbEntity = new DownloadEntity(mTaskId, mTotalSize, 0L, mUrl, mSaveDirPath, fileName, downloadStatus);
                    mDownloadDao.insert(dbEntity);
                }
                while ((length = bis.read(buffer)) > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL
                        && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    mDownLoadFile.write(buffer, 0, length);
                    mCompletedSize += length;
                    buffOffset += length;

                    // 以kb计算
                    if (buffOffset >= updateSize) {
                        // Update download information database
                        buffOffset = 0;
                        // 支持断点续传时，在往数据库中保存下载信息
                        // 此处会频繁更新数据库
                        dbEntity.setCompletedSize(mCompletedSize);
                        mDownloadDao.update(dbEntity);
                        //onStart 回调
                        handler.sendEmptyMessage(downloadStatus);
                    }
                }

                //onStart;
                // 防止最后一次不足UPDATE_SIZE，导致percent无法达到100%
                handler.sendEmptyMessage(downloadStatus);
            }
        } catch (FileNotFoundException e) {
            // file not found
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND;
        } catch (IOException e) {
            // io exception
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        } finally {
            if (isFinish()) {
                handler.sendEmptyMessage(downloadStatus);
            }

            // 下载后新数据库
            if (dbEntity != null) {
                dbEntity.setCompletedSize(mCompletedSize);
                dbEntity.setDownloadStatus(downloadStatus);
                mDownloadDao.update(dbEntity);
            }

            // 回收资源
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
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_FINISH;
            finish = true;
        }
        return finish;
    }

    public void create() {
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CREATE);
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_CREATE);
    }


    public void cancel() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL;
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
    }

    public void waits() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_WAIT;
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_WAIT);
    }

    public void pause() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_PAUSE;
    }


    private String getDownLoadPercent() {
        String baifenbi = "0";// 接受百分比的值
        double baiy = mCompletedSize * 1.0;
        double baiz = mTotalSize * 1.0;
        if (baiz > 0) {
            double fen = (baiy / baiz) * 100;
            DecimalFormat df1 = new DecimalFormat("0");//0.00
            baifenbi = df1.format(fen);
        }
        return baifenbi;
    }

    private String getFilePath() {
        // 默认名字
        if (TextUtils.isEmpty(fileName)) {
            fileName = getFileNameFromUrl(mUrl);
        }

        // 默认路径
        if (TextUtils.isEmpty(mSaveDirPath)) {
            mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yuan/download/";
        }

        File file = new File(mSaveDirPath);
        if (!file.exists()) {
            boolean createDir = file.mkdirs();
            if (createDir) {
                if (BuildConfig.DEBUG) Log.d("DownloadTask", "create file dir success");
            }

        }
        return mSaveDirPath + fileName;
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
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
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
        return mSaveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static class Builder {
        private String id;// task mTaskId
        private String url;// file mUrl
        private String saveDirPath;// file save path
        private String fileName; // File name when saving
        private int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CREATE;
        private long totalSize;
        private long completedSize;
        private ArrayBlockingQueue blockingQueue;

        private DownloadTaskListener listener;

        /**
         * 作为下载task开始、删除、停止的key值，如果为空则默认是url
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * 下载url（not null）
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置保存地址
         */
        public Builder setSaveDirPath(String saveDirPath) {
            this.saveDirPath = saveDirPath;
            return this;
        }

        /**
         * 设置下载状态
         */
        public Builder setDownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
            return this;
        }

        /**
         * 设置文件名
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 文件总大小
         */
        public Builder setTotalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        /**
         * 已经下载大小
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
