package com.yuan.library.dmanager.download;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * download status
 */

@Entity(nameInDb = "download_status")
public class TaskEntity {

    /**
     *  + "id INTEGER PRIMARY KEY autoincrement,"
     + "taskId TEXT,"
     + "totalSize LONG,"
     + "completedSize LONG,"
     + "url TEXT,"
     + "filePath TEXT,"
     + "fileName TEXT,"
     + "taskStatus INTEGER" + ");";
     */
    @Id(autoincrement = true)
    private Long id;
    @Property
    private String taskId;
    @Property
    private long totalSize;
    @Property
    private long completedSize;
    @Property
    private String url;
    @Property
    private String filePath;
    @Property
    private String fileName;
    @Property
    private int taskStatus;


    private TaskEntity(Builder builder) {
        this.taskId = builder.taskId;
        this.totalSize = builder.totalSize;
        this.completedSize = builder.completedSize;
        this.url = builder.url;
        this.filePath = builder.filePath;
        this.fileName = builder.fileName;
        this.taskStatus = builder.taskStatus;
    }

    @Generated(hash = 1689179221)
    public TaskEntity(Long id, String taskId, long totalSize, long completedSize, String url,
            String filePath, String fileName, int taskStatus) {
        this.id = id;
        this.taskId = taskId;
        this.totalSize = totalSize;
        this.completedSize = completedSize;
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
        this.taskStatus = taskStatus;
    }

    @Generated(hash = 397975341)
    public TaskEntity() {
    }

    public String getTaskId() {
        taskId = TextUtils.isEmpty(taskId) ? String.valueOf(url.hashCode()) : taskId;
        return taskId;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(Long completedSize) {
        this.completedSize = completedSize;
    }

    public String getUrl() {
        return url;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public static class Builder {
        // file id
        private String taskId;
        // file length
        private long totalSize;
        // file complete length
        private long completedSize;
        // file url
        private String url;
        // file save path
        private String filePath;
        // file name
        private String fileName;
        // file download status
        private int taskStatus;

        public Builder downloadId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder totalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        public Builder completedSize(long completedSize) {
            this.completedSize = completedSize;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder filePath(String saveDirPath) {
            this.filePath = saveDirPath;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder downloadStatus(int downloadStatus) {
            this.taskStatus = downloadStatus;
            return this;
        }

        public TaskEntity build() {
            return new TaskEntity(this);
        }

    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "taskId='" + taskId + '\'' +
                ", totalSize=" + totalSize +
                ", completedSize=" + completedSize +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
