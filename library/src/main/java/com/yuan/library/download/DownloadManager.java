package com.yuan.library.download;

import android.content.Context;

import com.yuan.library.db.DownloadDao;

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

    // 队列
    private BlockingQueue<Runnable> mQueue;

    // download database dao
    private DownloadDao mDownloadDao;

    // ok http
    private OkHttpClient mClient;

    // ThreadPoolExecutor
    private ThreadPoolExecutor mExecutor;

    //
    private Map<String, DownloadTask> mCurrentTaskList;

    private DownloadManager() {

    }

    /**
     * 获得当前对象实例
     *
     * @return 当前实例对象
     */
    public static synchronized DownloadManager getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadManager();
        }
        return mInstance;
    }


    /**
     * 默认只能并发下载1个
     *
     * @param context Context
     */
    public void init(Context context) {
        init(context, 1);
    }

    public void init(Context context, int downloadSize) {
        // init net
        initOkHttpClient();
        // init db
        mDownloadDao = new DownloadDao(context);
        initDBState();
        // init thread pool
        int size = downloadSize < 1 ? 1 : downloadSize;
        mExecutor = new ThreadPoolExecutor(3, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mExecutor.prestartAllCoreThreads();
        mCurrentTaskList = new HashMap<>();

        mQueue = mExecutor.getQueue();

    }

    /**
     * 初始化OkHttp
     */
    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        mClient = builder.build();
    }

    /**
     * 添加下载任务
     */
    public void add(DownloadTask task) {
        // 如果任务不等于空，且任务没有正在下载
        if (task != null && task.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_START) {
            task.setDownloadDao(mDownloadDao);
            task.setClient(mClient);
            task.create();
            mCurrentTaskList.put(task.getTaskId(), task);
            // 如果队列中没有当前队列，就直接执行
            if (!mQueue.contains(task)) {
                mExecutor.execute(task);
            }

        }
    }

    /**
     * 暂停下载任务
     */
    public void pause(DownloadTask task) {
        if (task != null) {
            removeFromQueue(task);
            task.pause();
        }
    }

    /**
     * 重新开始已经暂停的下载任务
     */
    public void resume(DownloadTask task) {
        if (task != null) {
            add(task);
        }
    }

    private void removeFromQueue(DownloadTask task) {
        if (mQueue.contains(task)) {
            try {
                mQueue.take();
                task.cancel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消下载任务(同时会删除已经下载的文件，和清空数据库缓存)
     */
    public void cancel(DownloadTask task) {
        if (task != null) {
            mCurrentTaskList.remove(task.getTaskId());
            task.cancel();
            mDownloadDao.delete(mDownloadDao.query(task.getTaskId()));
            File temp = new File(task.getSaveDirPath() + task.getFileName());
            if (temp.exists()) temp.delete();
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        }
    }


    /**
     * 获得指定的task
     *
     * @param id task id
     *
     * @return task
     */
    public DownloadTask getTask(String id) {
        DownloadTask currTask = mCurrentTaskList.get(id);
        if (currTask == null) {
            // 从数据库中取出为完成的task
            DownloadEntity entity = mDownloadDao.query(id);
            if (entity != null) {
                int status = entity.getDownloadStatus();
                currTask = createTaskWithEntity(entity);
                if (status != DownloadStatus.DOWNLOAD_STATUS_FINISH) {
                    mCurrentTaskList.put(id, currTask);
                }
            }
        }
        return currTask;
    }

    /**
     * 获得所有的task
     *
     * @return 所有的任务
     */
    public Map<String, DownloadTask> getTaskList() {
        if (mCurrentTaskList != null && mCurrentTaskList.size() <= 0) {
            List<DownloadEntity> entities = mDownloadDao.queryAll();
            for (DownloadEntity entity : entities) {
                DownloadTask currTask = createTaskWithEntity(entity);
                mCurrentTaskList.put(entity.getDownloadId(), currTask);
            }
        }

        return mCurrentTaskList;
    }

    /**
     * 主要做一些判断,比如正在下载过程中程序突然终止,
     * 而一些状态来来得及存储,比如正在下载的状态,没有及时变为暂停状态。
     */
    private void initDBState() {
        List<DownloadEntity> entities = mDownloadDao.queryAll();
        for (DownloadEntity entity : entities) {
            long completedSize = entity.getCompletedSize();
            long totalSize = entity.getTotalSize();
            if (completedSize > 0 && completedSize != totalSize && entity.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_START) {
                entity.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
            }
            mDownloadDao.update(entity);
        }
    }

    private DownloadTask createTaskWithEntity(DownloadEntity entity) {
        if (entity != null) {
            return new DownloadTask.Builder()
                    .setDownloadStatus(entity.getDownloadStatus())
                    .setFileName(entity.getFileName())
                    .setSaveDirPath(entity.getSaveDirPath())
                    .setUrl(entity.getUrl())
                    .setCompletedSize(entity.getCompletedSize())
                    .setTotalSize(entity.getTotalSize())
                    .setId(entity.getDownloadId()).build();

        }
        return null;
    }


}