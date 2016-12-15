package com.yuan.downloadmanager;

import android.app.Application;

import com.yuan.library.dmanager.download.DownloadManager;

/**
 * Created by Yuan on 27/09/2016:1:33 PM.
 * <p/>
 * Description:com.yuan.downloadmanager.DApplication
 */

public class DApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DownloadManager.getInstance().init(this, 3);
    }

}
