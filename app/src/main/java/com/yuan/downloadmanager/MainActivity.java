package com.yuan.downloadmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        DownloadEntity.Builder builder = new DownloadEntity.Builder();
//        builder.url("http://www.baidu.com");
//        builder.completedSize(2343);
//        builder.totalSize(10000);
//        builder.downloadId(UUID.randomUUID() + "");
//        builder.downloadStatus(0);
//        builder.fileName("a.txt");
//        builder.saveDirPath("sdcard/");
//
//        DownloadDao downloadDao = new DownloadDao(this);
//        DownloadEntity entity = builder.build();
//        downloadDao.insert(entity);

//        entity = downloadDao.query(entity.getDownloadId());
//        if (BuildConfig.DEBUG) Log.d("MainActivity", "entity:" + entity);


    }

}
