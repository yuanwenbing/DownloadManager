package com.yuan.library.db;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Yuan on 27/09/2016:11:22 AM.
 * <p/>
 * Description:com.yuan.library.db.OkHttpManager
 */

public class OKHttpManager {
    /**
     * 默认链接，读取，写入超时时间
     */
    public static final long DEFAULT_SECONDS = 10;

    private Context mContext;
    private OKHttpConfig mConfig;
    private static OKHttpManager mInstance;
    private OkHttpClient mOkHttpClient;
    private OkHttpClient.Builder mBuilder;

    private OKHttpManager(Context context, OKHttpConfig config) {
        mContext = context;
        if (config == null) {
            throw new NullPointerException("Null okhttp config, did you forget initialize the OKHttpManager?");
        }
        if (config.getBaseResponseClass() == null) {
            throw new IllegalArgumentException("Base response class is null, please set from OKHttpConfig.Builder ");
        }
        if (config.getBaseResponseClass().isInterface()) {
            throw new IllegalArgumentException("Base response class must be a concrete class");
        }

        mConfig = config;
        buildInfo();
    }

    private void buildInfo() {
        mBuilder = new OkHttpClient.Builder();
        long connectTimeout = mConfig.getConnectTimeout() > 0 ? mConfig.getConnectTimeout() : DEFAULT_SECONDS;
        long readTimeout = mConfig.getReadTimeout() > 0 ? mConfig.getReadTimeout() : DEFAULT_SECONDS;
        long writeTimeout = mConfig.getWriteTimeout() > 0 ? mConfig.getWriteTimeout() : DEFAULT_SECONDS;

        mBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        mBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
        mBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);

        //mBuilder.addInterceptor(new LoggingInterceptor());
        // cache time
        if (mConfig.getCacheTime() > 0) {
            mBuilder.addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    return response.newBuilder().removeHeader("Pragma").header("Cache-Control", String.format(
                            "max-age=%d", mConfig.getCacheTime())).build();
                }
            });
        }

        // 添加log监听器，打印所有log
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(mConfig.getLogLevel() == null ? HttpLoggingInterceptor.Level.NONE : mConfig.getLogLevel());
        mBuilder.addInterceptor(interceptor);

        if (mConfig.getCache() != null) {
            mBuilder.cache(mConfig.getCache());
        }

        mOkHttpClient = mBuilder.build();
    }

    static OKHttpManager getInstance() {
        return mInstance;
    }

    public static void init(Context context, Class<? extends OKBaseResponse> baseResponseClass) {
        init(context, new OKHttpConfig.Builder().setBaseResponseClass(baseResponseClass).build());
    }

    /**
     * 程序初始化时，初始okhttp配置
     *
     * @param context
     * @param config
     */
    public static void init(Context context, OKHttpConfig config) {
        mInstance = new OKHttpManager(context, config);
    }

    public static OKHttpConfig getConfig() {
        return getInstance().mConfig;
    }

    public static OkHttpClient getOkHttpClient() {
        return getInstance().mOkHttpClient;
    }

    public Context getContext() {
        return mContext;
    }

    public void clearCached() {
        try {
            mOkHttpClient.cache().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
