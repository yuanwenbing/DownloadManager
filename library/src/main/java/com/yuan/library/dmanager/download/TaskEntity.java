package com.yuan.library.dmanager.download;

import android.text.TextUtils;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * download status
 */

public class TaskEntity {
    private String taskId;
    private long totalSize;
    private long completedSize;
    private String url;
    private String filePath;
    private String fileName;
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
}
