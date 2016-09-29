package com.yuan.library.db;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * 下载状态
 */

public class DownloadEntity {
    // 下载id
    private String downloadId;
    // 总大小
    private Long totalSize;
    // 已下载大小
    private Long completedSize;
    // 下载Url
    private String url;
    // 存储路径
    private String saveDirPath;
    // 文件名字
    private String fileName;
    // 下载状态
    private Integer downloadStatus;

    public DownloadEntity() {

    }

    public DownloadEntity(String downloadId, Long totalSize, Long completedSize, String url, String saveDirPath, String fileName, Integer downloadStatus) {
        this.downloadId = downloadId;
        this.totalSize = totalSize;
        this.completedSize = completedSize;
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
        this.downloadStatus = downloadStatus;
    }

    public DownloadEntity(Builder builder) {
        this.downloadId = builder.downloadId;
        this.totalSize = builder.totalSize;
        this.completedSize = builder.completedSize;
        this.url = builder.url;
        this.saveDirPath = builder.saveDirPath;
        this.fileName = builder.fileName;
        this.downloadStatus = builder.downloadStatus;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(Long completedSize) {
        this.completedSize = completedSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public static class Builder {
        // 下载id
        private String downloadId;
        // 总大小
        private Long totalSize;
        // 已下载大小
        private Long completedSize;
        // 下载Url
        private String url;
        // 存储路径
        private String saveDirPath;
        // 文件名字
        private String fileName;
        // 下载状态
        private Integer downloadStatus;

        public Builder downloadId(String downloadId) {
            this.downloadId = downloadId;
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

        public Builder saveDirPath(String saveDirPath) {
            this.saveDirPath = saveDirPath;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder downloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
            return this;
        }

        public DownloadEntity build() {
            return new DownloadEntity(this);
        }

    }

    @Override
    public String toString() {
        return "DownloadEntity{" +
                "downloadId='" + downloadId + '\'' +
                ", totalSize=" + totalSize +
                ", completedSize=" + completedSize +
                ", url='" + url + '\'' +
                ", saveDirPath='" + saveDirPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadStatus=" + downloadStatus +
                '}';
    }
}
