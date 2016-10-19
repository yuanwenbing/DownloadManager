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
        DownloadManager.getInstance().init(this);
    }

}
