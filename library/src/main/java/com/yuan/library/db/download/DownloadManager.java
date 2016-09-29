package com.yuan.library.db.download;

import android.content.Context;

import com.yuan.library.db.DownloadDao;
import com.yuan.library.db.DownloadEntity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Yuan on 27/09/2016:11:08 AM.
 * <p/>
 * Description:com.yuan.library.db.download.DownloadManager
 */

public class DownloadManager {
    // context
    private static Context mContext;
    // manager instance
    private static DownloadManager mDownloadManager;
    // download database dao
    private DownloadDao mDownloadDao;
    // ok http
    private OkHttpClient mClient;
    // the max download count
    private int mPoolSize = 3;
    //
    private ExecutorService mExecutorService;
    //
    private Map<String, DownloadTask> mCurrentTaskList;

    private DownloadManager() {
        initOkHttpClient();

        if (mContext == null) {
            throw new RuntimeException("Please init context in application!");
        }

        mDownloadDao = new DownloadDao(mContext);
        // 初始化线程池
        mExecutorService = Executors.newFixedThreadPool(mPoolSize);
        mCurrentTaskList = new HashMap<>();
        initDbState();
    }

    public void setTaskPoolSize(int size) {
        if (size < 1) {
            size = 1;
        }
        mPoolSize = size;

    }

    public boolean isTerminated() {
        return mExecutorService.isTerminated();
    }

    /**
     * 方法加锁，防止多线程操作时出现多个实例
     */
    private static synchronized void init() {
        if (mDownloadManager == null) {
            mDownloadManager = new DownloadManager();
        }
    }

    /**
     * 获得当前对象实例
     *
     * @return 当前实例对象
     */
    public static DownloadManager getInstance() {
        if (mDownloadManager == null) {
            init();
        }
        return mDownloadManager;
    }

    /**
     * 管理器初始化，建议在application中调用
     */
    public static void init(Context context) {
        mContext = context;
        getInstance();
    }

    /**
     * 初始化okhttp
     */
    private void initOkHttpClient() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okBuilder.readTimeout(10, TimeUnit.SECONDS);
        okBuilder.writeTimeout(10, TimeUnit.SECONDS);
        mClient = okBuilder.build();
    }

    /**
     * 添加下载任务
     */
    public void add(DownloadTask downloadTask) {
        if (downloadTask != null && !isDownloading(downloadTask)) {
            downloadTask.setDownloadDao(mDownloadDao);
            downloadTask.setClient(mClient);
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CREATE);
            mCurrentTaskList.put(downloadTask.getTaskId(), downloadTask);
            Future future = mExecutorService.submit(downloadTask);
        }
    }

    /**
     * 恢复下载任务
     */
    public void resume(DownloadTask downloadTask) {
        if (downloadTask != null && !isDownloading(downloadTask)) {
            downloadTask.setDownloadDao(mDownloadDao);
            downloadTask.setClient(mClient);
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_RESUME);
            mCurrentTaskList.put(downloadTask.getTaskId(), downloadTask);
            Future future = mExecutorService.submit(downloadTask);
        }
    }

    private boolean isDownloading(DownloadTask task) {
        if (task != null) {
            if (task.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_START) {
                return true;
            }
        }
        return false;
    }

    /**
     * 暂停下载任务
     */
    public void pause(DownloadTask task) {
        if (task != null) {
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
        }
    }

    /**
     * 重新开始已经暂停的下载任务
     *
     * @param id 任务id
     */
    public void resume(String id) {
        DownloadTask task = getDownloadTask(id);
        if (task != null) {
            resume(task);
        }
    }

    /**
     * 取消下载任务(同时会删除已经下载的文件，和清空数据库缓存)
     *
     */
    public void cancel(DownloadTask task) {
        if (task != null) {
            mCurrentTaskList.remove(task.getTaskId());
            task.cancel();
            mDownloadDao.delete(mDownloadDao.query(task.getTaskId()));
            File temp = new File(task.getSaveDirPath() + task.getFileName());
            if (temp.exists()) {
                temp.delete();
            }
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        }
    }

    /**
     * 实时更新manager中的task信息
     *
     * @param task task
     */
    void updateDownloadTask(DownloadTask task) {
        if (task != null) {
            DownloadTask currTask = getDownloadTask(task.getTaskId());
            if (currTask != null) {
                mCurrentTaskList.put(task.getTaskId(), task);
            }
        }
    }

    /**
     * 获得指定的task
     *
     * @param id task id
     * @return task
     */
    public DownloadTask getDownloadTask(String id) {
        DownloadTask currTask = mCurrentTaskList.get(id);
        if (currTask == null) {
            // 从数据库中取出为完成的task
            DownloadEntity entity = mDownloadDao.query(id);
            if (entity != null) {
                int status = entity.getDownloadStatus();
                currTask = parseEntity2Task(entity);
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
    public Map<String, DownloadTask> getAllDownloadTasks() {
        if (mCurrentTaskList != null && mCurrentTaskList.size() <= 0) {
            List<DownloadEntity> entities = mDownloadDao.queryAll();
            for (DownloadEntity entity : entities) {
                DownloadTask currTask = parseEntity2Task(entity);
                mCurrentTaskList.put(entity.getDownloadId(), currTask);
            }
        }

        return mCurrentTaskList;
    }

    /**
     * 主要做一些判断,比如正在下载过程中程序突然终止,
     * 而一些状态来来得及存储,比如正在下载的状态,
     * 没有及时变为暂停状态。
     */
    private void initDbState() {
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

    private DownloadTask parseEntity2Task( DownloadEntity entity) {
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
