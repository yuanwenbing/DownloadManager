package com.yuan.library.db;

import okhttp3.Cache;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Yuan on 27/09/2016:11:23 AM.
 * <p/>
 * Description:com.yuan.library.db.OkHttpConfig
 */

public class OKHttpConfig {
    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;
    private long mCacheTime;
    private Cache mCache;
    private HttpLoggingInterceptor.Level mLogLevel;
    private Class<? extends OKBaseResponse> mBaseResponseClass;

    private OKHttpConfig(Builder builder) {
        this.mBaseResponseClass = builder.baseResponseClass;
        this.mConnectTimeout = builder.connectTimeout;
        this.mWriteTimeout = builder.writeTimeout;
        this.mReadTimeout = builder.readTimeout;
        this.mCacheTime = builder.cacheTime;
        this.mCache = builder.cache;
        this.mLogLevel = builder.level;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public long getCacheTime() {
        return mCacheTime;
    }

    public Cache getCache() {
        return mCache;
    }

    public HttpLoggingInterceptor.Level getLogLevel() {
        return mLogLevel;
    }

    public Class<? extends OKBaseResponse> getBaseResponseClass() {
        return mBaseResponseClass;
    }

    public static class Builder {
        private Class<? extends OKBaseResponse> baseResponseClass;
        private long connectTimeout;
        private long readTimeout;
        private long writeTimeout;
        private long cacheTime;
        private Cache cache;
        private HttpLoggingInterceptor.Level level;

        /**
         * 设置baseresponse class
         *
         * @param baseResponseClass
         * @return
         */
        public Builder setBaseResponseClass(Class<? extends OKBaseResponse> baseResponseClass) {
            this.baseResponseClass = baseResponseClass;
            return this;
        }

        /**
         * 设置连接超时时间
         *
         * @param connectTimeoutSeconds unit seconds
         * @return
         */
        public Builder setConnectTimeout(long connectTimeoutSeconds) {
            this.connectTimeout = connectTimeoutSeconds;
            return this;
        }

        /**
         * 设置读取超时时间
         *
         * @param readTimeoutSeconds unit seconds
         * @return
         */
        public Builder setReadTimeout(long readTimeoutSeconds) {
            this.readTimeout = readTimeoutSeconds;
            return this;
        }

        /**
         * 设置写入超时时间
         *
         * @param writeTimeoutSeconds unit seconds
         * @return
         */
        public Builder setWriteTimeout(long writeTimeoutSeconds) {
            this.writeTimeout = writeTimeoutSeconds;
            return this;
        }

        /**
         * 设置缓存时间
         *
         * @param cacheTimeSeconds unit seconds
         * @return
         */
        public Builder setCacheTime(long cacheTimeSeconds) {
            this.cacheTime = cacheTimeSeconds;
            return this;
        }

        /**
         * 设置缓存
         *
         * @param cache
         * @return
         */
        public Builder setCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder setLogLevel(HttpLoggingInterceptor.Level logLevel) {
            this.level = logLevel;
            return this;
        }

        public OKHttpConfig build() {
            return new OKHttpConfig(this);
        }
    }
}
