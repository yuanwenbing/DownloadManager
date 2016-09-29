package com.yuan.downloadmanager;

import android.app.Application;

import com.yuan.library.db.download.DownloadManager;

/**
 * Created by Yuan on 27/09/2016:1:33 PM.
 * <p/>
 * Description:com.yuan.downloadmanager.DownApplication
 */

public class DownApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DownloadManager.init(this);

//        File cacheFile = new File(getCacheDir(), "okcache");
//        Cache cache = new Cache(cacheFile, 100 * 1024 * 1024);// 100mb
//
//        // 程序初始化时，初始okhttp配置
//        OKHttpConfig OKHttpConfig = new OKHttpConfig.Builder()
//                .setBaseResponseClass(BaseResponse.class)
//                .setLogLevel(HttpLoggingInterceptor.Level.BODY)// log level
//                .setConnectTimeout(10) // connect time out
//                .setReadTimeout(10) // read time out
//                .setWriteTimeout(10) // write time out
//                .setCacheTime(1000) // cache time
//                .setCache(cache) // cache
//                .build();
//        OKHttpManager.init(this, OKHttpConfig);

    }

}
