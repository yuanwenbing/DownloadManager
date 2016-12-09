package com.yuan.library.dmanager.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.util.Log;

import com.yuan.library.BuildConfig;
import com.yuan.library.dmanager.db.DownloadDao;
import com.yuan.library.dmanager.utils.FileUtils;
import com.yuan.library.dmanager.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
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

    private TaskEntity mTaskEntity;

    private DownloadDao mDownloadDao;

    private DownloadTaskListener mListener;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                case DownloadStatus.DOWNLOAD_STATUS_QUEUE:
                    mListener.onQueue(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_CONNECTING:
                    mListener.onConnecting(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_START:
                    mListener.onStart(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                    mListener.onCancel(DownloadTask.this);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_REQUEST_ERROR:
                    mListener.onError(DownloadTask.this, DownloadStatus.DOWNLOAD_STATUS_REQUEST_ERROR);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_STORAGE_ERROR:
                    mListener.onError(DownloadTask.this, DownloadStatus.DOWNLOAD_STATUS_STORAGE_ERROR);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_FINISH:
                    mListener.onFinish(DownloadTask.this);
                    break;

            }
        }
    };


    public DownloadTask(TaskEntity taskEntity) {
        mTaskEntity = taskEntity;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        RandomAccessFile tempFile = null;

        try {


            String fileName = TextUtils.isEmpty(mTaskEntity.getFileName()) ? FileUtils.getFileNameFromUrl(mTaskEntity.getUrl()) : mTaskEntity.getFileName();
            String filePath = TextUtils.isEmpty(mTaskEntity.getFilePath()) ? FileUtils.getDefaultFilePath() : mTaskEntity.getFilePath();
            mTaskEntity.setFileName(fileName);
            mTaskEntity.setFilePath(filePath);
            tempFile = new RandomAccessFile(new File(filePath, fileName), "rwd");

            mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_CONNECTING);
            handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_CONNECTING);
            mDownloadDao.update(mTaskEntity);

            long completedSize = mTaskEntity.getCompletedSize();
            Request request = new Request.Builder().url(mTaskEntity.getUrl()).header("RANGE", "bytes=" + completedSize + "-").build();

            if (tempFile.length() == 0) {
               completedSize = 0;
            }
            tempFile.seek(completedSize);

            Response response = mClient.newCall(request).execute();
            if(response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    if (mDownloadDao.query(mTaskEntity.getTaskId()) == null) {
                        mDownloadDao.insert(mTaskEntity);
                        mTaskEntity.setTotalSize(responseBody.contentLength());
                    }
                    mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_START);

                    double updateSize = mTaskEntity.getTotalSize() / 100;
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[1024];
                    int length;
                    int buffOffset = 0;
                    while ((length = bis.read(buffer)) > 0 && mTaskEntity.getTaskStatus() != DownloadStatus.DOWNLOAD_STATUS_CANCEL && mTaskEntity.getTaskStatus() != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                        tempFile.write(buffer, 0, length);
                        completedSize += length;
                        buffOffset += length;
                        mTaskEntity.setCompletedSize(completedSize);
                        // 避免一直调用数据库
                        if (buffOffset >= updateSize) {
                            buffOffset = 0;
                            mDownloadDao.update(mTaskEntity);
                            handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_START);
                        }

                        if (completedSize == mTaskEntity.getTotalSize()) {
                            handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_START);
                            mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_FINISH);
                            handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_FINISH);
                            mDownloadDao.update(mTaskEntity);
                        }
                    }
                }
            }else{
                mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_REQUEST_ERROR);
                handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_REQUEST_ERROR);
                mDownloadDao.update(mTaskEntity);
            }


        } catch (FileNotFoundException e) {
            mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_STORAGE_ERROR);
            handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_STORAGE_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(bis, inputStream, tempFile);
        }
    }

    public TaskEntity getTaskEntity() {
        return mTaskEntity;
    }

    void pause() {
        mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
    }

    void queue() {
        mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_QUEUE);
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_QUEUE);
    }

    void cancel() {
        mTaskEntity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        handler.sendEmptyMessage(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
    }

    void setDownloadDao(DownloadDao mDownloadDao) {
        this.mDownloadDao = mDownloadDao;
    }

    void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public void setListener(DownloadTaskListener listener) {
        mListener = listener;
    }


}
