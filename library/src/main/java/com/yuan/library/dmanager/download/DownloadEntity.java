package com.yuan.library.dmanager.download;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * download status
 */

public class DownloadEntity {
    private String downloadId;
    private Long totalSize;
    private Long completedSize;
    private String url;
    private String saveDirPath;
    private String fileName;
    private Integer downloadStatus;


    public DownloadEntity(String downloadId, Long totalSize, Long completedSize, String url, String saveDirPath, String fileName, Integer downloadStatus) {
        this.downloadId = downloadId;
        this.totalSize = totalSize;
        this.completedSize = completedSize;
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
        this.downloadStatus = downloadStatus;
    }

    private DownloadEntity(Builder builder) {
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

    public Long getTotalSize() {
        return totalSize;
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


    public String getSaveDirPath() {
        return saveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public static class Builder {
        // file id
        private String downloadId;
        // file length
        private Long totalSize;
        // file complete length
        private Long completedSize;
        // file url
        private String url;
        // file save path
        private String saveDirPath;
        // file name
        private String fileName;
        // file download status
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
