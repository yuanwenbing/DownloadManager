package com.yuan.library.dmanager.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yuan.library.dmanager.db.DaoManager;
import com.yuan.library.dmanager.utils.FileUtils;
import com.yuan.library.dmanager.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

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

    private DownloadTaskListener mListener;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                case TaskStatus.TASK_STATUS_QUEUE:
                    mListener.onQueue(DownloadTask.this);
                    break;
                case TaskStatus.TASK_STATUS_CONNECTING:
                    mListener.onConnecting(DownloadTask.this);
                    break;
                case TaskStatus.TASK_STATUS_DOWNLOADING:
                    mListener.onStart(DownloadTask.this);
                    break;
                case TaskStatus.TASK_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this);
                    break;
                case TaskStatus.TASK_STATUS_CANCEL:
                    mListener.onCancel(DownloadTask.this);
                    break;
                case TaskStatus.TASK_STATUS_REQUEST_ERROR:
                    mListener.onError(DownloadTask.this, TaskStatus.TASK_STATUS_REQUEST_ERROR);
                    break;
                case TaskStatus.TASK_STATUS_STORAGE_ERROR:
                    mListener.onError(DownloadTask.this, TaskStatus.TASK_STATUS_STORAGE_ERROR);
                    break;
                case TaskStatus.TASK_STATUS_FINISH:
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

            mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_CONNECTING);
            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_CONNECTING);

            if (DaoManager.instance().queryWidthId(mTaskEntity.getTaskId()) != null) {
                DaoManager.instance().update(mTaskEntity);
            }

            long completedSize = mTaskEntity.getCompletedSize();
            Request request;
            try {
                request = new Request.Builder().url(mTaskEntity.getUrl()).header("RANGE", "bytes=" + completedSize + "-").build();
            } catch (IllegalArgumentException e) {
                mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_REQUEST_ERROR);
                handler.sendEmptyMessage(TaskStatus.TASK_STATUS_REQUEST_ERROR);
                Log.d("DownloadTask", e.getMessage());
                return;
            }

            if (tempFile.length() == 0) {
                completedSize = 0;
            }
            tempFile.seek(completedSize);

            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    if (DaoManager.instance().queryWidthId(mTaskEntity.getTaskId()) == null) {
                        DaoManager.instance().insertOrReplace(mTaskEntity);
                        mTaskEntity.setTotalSize(responseBody.contentLength());
                    }
                    mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_DOWNLOADING);

                    double updateSize = mTaskEntity.getTotalSize() / 100;
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[1024];
                    int length;
                    int buffOffset = 0;
                    while ((length = bis.read(buffer)) > 0 && mTaskEntity.getTaskStatus() != TaskStatus.TASK_STATUS_CANCEL && mTaskEntity.getTaskStatus() != TaskStatus.TASK_STATUS_PAUSE) {
                        tempFile.write(buffer, 0, length);
                        completedSize += length;
                        buffOffset += length;
                        mTaskEntity.setCompletedSize(completedSize);
                        // 避免一直调用数据库
                        if (buffOffset >= updateSize) {
                            buffOffset = 0;
                            DaoManager.instance().update(mTaskEntity);
                            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_DOWNLOADING);
                        }

                        if (completedSize == mTaskEntity.getTotalSize()) {
                            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_DOWNLOADING);
                            mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_FINISH);
                            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_FINISH);
                            DaoManager.instance().update(mTaskEntity);
                        }
                    }
                }
            } else {
                mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_REQUEST_ERROR);
                handler.sendEmptyMessage(TaskStatus.TASK_STATUS_REQUEST_ERROR);
            }


        } catch (FileNotFoundException e) {
            mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_STORAGE_ERROR);
            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_STORAGE_ERROR);
        } catch (SocketTimeoutException | ConnectException e) {
            mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_REQUEST_ERROR);
            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_REQUEST_ERROR);
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
        mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_PAUSE);
        DaoManager.instance().update(mTaskEntity);
        handler.sendEmptyMessage(TaskStatus.TASK_STATUS_PAUSE);
    }

    void queue() {
        mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_QUEUE);
        handler.sendEmptyMessage(TaskStatus.TASK_STATUS_QUEUE);
    }

    void cancel() {
        mTaskEntity.setTaskStatus(TaskStatus.TASK_STATUS_CANCEL);
        DaoManager.instance().delete(mTaskEntity);
        handler.sendEmptyMessage(TaskStatus.TASK_STATUS_CANCEL);
    }

    void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public void setListener(DownloadTaskListener listener) {
        mListener = listener;
    }


}
