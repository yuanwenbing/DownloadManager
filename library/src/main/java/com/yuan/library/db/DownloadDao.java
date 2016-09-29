package com.yuan.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * 下载状态Dao
 */

public class DownloadDao {

    private SQLiteHelper mHelper;

    public DownloadDao(Context context) {
        mHelper = new SQLiteHelper(context, "download", null, 1);
    }

    public boolean insert(DownloadEntity entity) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long insert = database.insert("download_status", null, getContentValues(entity));
        System.out.println("insert");
        database.close();
        return insert != -1;
    }

    public DownloadEntity query(String id) {
        try {
            SQLiteDatabase database = mHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = database.query("download_status", null, "downloadId=?", new String[]{id}, null, null, null, null);
                if (cursor.moveToNext()) {
                    DownloadEntity.Builder builder = new DownloadEntity.Builder();
                    String downloadId = cursor.getString(cursor.getColumnIndex("downloadId"));
                    int totalSize = cursor.getInt(cursor.getColumnIndex("totalSize"));
                    int completedSize = cursor.getInt(cursor.getColumnIndex("completedSize"));
                    String url = cursor.getString(cursor.getColumnIndex("url"));
                    String saveDirPath = cursor.getString(cursor.getColumnIndex("saveDirPath"));
                    String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex("downloadStatus"));

                    return builder.downloadId(downloadId)
                            .totalSize(totalSize)
                            .completedSize(completedSize)
                            .url(url)
                            .saveDirPath(saveDirPath)
                            .fileName(fileName)
                            .downloadStatus(downloadStatus)
                            .build();

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public List<DownloadEntity> queryAll() {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = null;
        List<DownloadEntity> list = new ArrayList<>();
        try {
            cursor = database.query("download_status", null, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                DownloadEntity.Builder builder = new DownloadEntity.Builder();
                String downloadId = cursor.getString(cursor.getColumnIndex("downloadId"));
                int totalSize = cursor.getInt(cursor.getColumnIndex("totalSize"));
                int completedSize = cursor.getInt(cursor.getColumnIndex("completedSize"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String saveDirPath = cursor.getString(cursor.getColumnIndex("saveDirPath"));
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                int downloadStatus = cursor.getInt(cursor.getColumnIndex("downloadStatus"));

                list.add(builder.downloadId(downloadId)
                        .totalSize(totalSize)
                        .completedSize(completedSize)
                        .url(url)
                        .saveDirPath(saveDirPath)
                        .fileName(fileName)
                        .downloadStatus(downloadStatus)
                        .build());


            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            database.close();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean update(DownloadEntity entity) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long update = database.update("download_status", getContentValues(entity), "downloadId=?", new String[]{entity.getDownloadId()});
        database.close();
        return update != -1;
    }

    public boolean delete(DownloadEntity entity) {
        if (entity == null) return false;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long delete = database.delete("download_status", "downloadId=?", new String[]{entity.getDownloadId()});
        database.close();
        return delete != -1;
    }

    public boolean drop() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.execSQL("DROP TABLE download_status");
        database.close();
        return true;
    }

    private ContentValues getContentValues(DownloadEntity entity) {
        ContentValues cv = new ContentValues();
        cv.put("downloadId", entity.getDownloadId());
        cv.put("totalSize", entity.getTotalSize());
        cv.put("completedSize", entity.getCompletedSize());
        cv.put("url", entity.getUrl());
        cv.put("saveDirPath", entity.getSaveDirPath());
        cv.put("fileName", entity.getFileName());
        cv.put("downloadStatus", entity.getDownloadStatus());
        return cv;
    }


}
