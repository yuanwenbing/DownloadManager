package com.yuan.library.dmanager.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SoundEffectConstants;

import com.yuan.library.BuildConfig;
import com.yuan.library.dmanager.db.DownloadDao;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DownloadManager {

    // manager instance
    private static DownloadManager mInstance;

    // quess
    private BlockingQueue<Runnable> mQueue;

    // download database dao
    private DownloadDao mDownloadDao;

    // ok http
    private OkHttpClient mClient;

    // ThreadPoolExecutor
    private ThreadPoolExecutor mExecutor;


    private int mMaximumPoolSize = 1;

    //
    private Map<String, DownloadTask> mCurrentTaskList;

    private DownloadManager() {

    }

    public static synchronized DownloadManager getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadManager();
        }
        return mInstance;
    }


    /**
     * default 1
     *
     * @param context Context
     */
    public void init(Context context) {
        init(context, 1);
    }

    public void init(Context context, int downloadSize) {
        initOkHttpClient();
        mDownloadDao = new DownloadDao(context);
        initDBState();
        int size = downloadSize < 1 ? 1 : downloadSize;
        mMaximumPoolSize = size;
        mExecutor = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mExecutor.prestartAllCoreThreads();
        mCurrentTaskList = new HashMap<>();
        mQueue = mExecutor.getQueue();

    }

    /**
     * init okhttp
     */
    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        mClient = builder.build();
    }

    /**
     * addTask task
     */
    public void addTask(@NonNull DownloadTask task) {

        TaskEntity taskEntity = task.getTaskEntity();

        if (taskEntity != null && taskEntity.getTaskStatus() != DownloadStatus.DOWNLOAD_STATUS_START) {
            task.setDownloadDao(mDownloadDao);
            task.setClient(mClient);
            mCurrentTaskList.put(taskEntity.getTaskId(), task);
            if (!mQueue.contains(task)) {
                mExecutor.execute(task);
            }

            if (mExecutor.getTaskCount() > mMaximumPoolSize) {
                task.queue();
            }


        }
    }

    /**
     * pauseTask task
     */
    public void pauseTask(@NonNull DownloadTask task) {
        removeFromQueue(task);
        task.pause();
    }

    /**
     * resumeTask task
     */
    public void resumeTask(@NonNull DownloadTask task) {
        addTask(task);
    }

    private void removeFromQueue(DownloadTask task) {
        if (mQueue.contains(task)) {
            try {
                mQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * cancel task
     */
    public void cancelTask(@NonNull DownloadTask task) {
        TaskEntity taskEntity = task.getTaskEntity();
        if (taskEntity != null) {
            removeFromQueue(task);
            mCurrentTaskList.remove(taskEntity.getTaskId());
            mDownloadDao.delete(mDownloadDao.query(taskEntity.getTaskId()));
            task.cancel();
            if (!TextUtils.isEmpty(taskEntity.getFilePath()) && !TextUtils.isEmpty(taskEntity.getFileName())) {
                File temp = new File(taskEntity.getFilePath(), taskEntity.getFileName());
                if (temp.exists()) {
                    if (temp.delete()) {
                        if (BuildConfig.DEBUG) Log.d("DownloadManager", "delete temp file!");
                    }
                }
            }
        }
    }

    /**
     * @return task
     */
    public DownloadTask getTask(String id) {
        DownloadTask currTask = mCurrentTaskList.get(id);
        if (currTask == null) {
            TaskEntity entity = mDownloadDao.query(id);
            if (entity != null) {
                int status = entity.getTaskStatus();
                currTask = new DownloadTask(entity);
                if (status != DownloadStatus.DOWNLOAD_STATUS_FINISH) {
                    mCurrentTaskList.put(id, currTask);
                }
            }
        }
        return currTask;
    }

    public boolean isPauseTask(String id) {
        TaskEntity entity = mDownloadDao.query(id);
        if (entity != null) {
            File file = new File(entity.getFilePath(), entity.getFilePath());
            if (file.exists()) {
                long totalSize = entity.getTotalSize();
                return totalSize > 0 && file.length() < totalSize;
            }
        }
        return false;
    }

    public boolean isFinishTask(String id) {
        TaskEntity entity = mDownloadDao.query(id);
        if (entity != null) {
            File file = new File(entity.getFilePath(), entity.getFileName());
            if (file.exists()) {
                return file.length() == entity.getTotalSize();
            }
        }
        return false;
    }

    private void initDBState() {
        List<TaskEntity> entities = mDownloadDao.queryAll();
        for (TaskEntity entity : entities) {
            long completedSize = entity.getCompletedSize();
            long totalSize = entity.getTotalSize();
            if (completedSize > 0 && completedSize != totalSize && entity.getTaskStatus() == DownloadStatus.DOWNLOAD_STATUS_START) {
                entity.setTaskStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
            }
            mDownloadDao.update(entity);
        }
    }

}