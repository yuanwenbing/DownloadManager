package com.yuan.library.dmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yuan.library.dmanager.download.TaskEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * download database dao
 */

public class DownloadDao {

    private SQLiteHelper mHelper;

    public DownloadDao(Context context) {
        mHelper = new SQLiteHelper(context, "download", null, 1);
    }

    public boolean insert(TaskEntity entity) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long insert = database.insert("download_status", null, getContentValues(entity));
        database.close();
        return insert != -1;
    }

    public TaskEntity query(String id) {
        try {
            SQLiteDatabase database = mHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = database.query("download_status", null, "taskId=?", new String[]{id}, null, null, null, null);
                if (cursor.moveToNext()) {
                    TaskEntity.Builder builder = new TaskEntity.Builder();
                    String taskId = cursor.getString(cursor.getColumnIndex("taskId"));
                    int totalSize = cursor.getInt(cursor.getColumnIndex("totalSize"));
                    int completedSize = cursor.getInt(cursor.getColumnIndex("completedSize"));
                    String url = cursor.getString(cursor.getColumnIndex("url"));
                    String saveDirPath = cursor.getString(cursor.getColumnIndex("filePath"));
                    String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                    int taskStatus = cursor.getInt(cursor.getColumnIndex("taskStatus"));

                    return builder.downloadId(taskId)
                            .totalSize(totalSize)
                            .completedSize(completedSize)
                            .url(url)
                            .filePath(saveDirPath)
                            .fileName(fileName)
                            .downloadStatus(taskStatus)
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

    public List<TaskEntity> queryAll() {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = null;
        List<TaskEntity> list = new ArrayList<>();
        try {
            cursor = database.query("download_status", null, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                TaskEntity.Builder builder = new TaskEntity.Builder();
                String taskId = cursor.getString(cursor.getColumnIndex("taskId"));
                int totalSize = cursor.getInt(cursor.getColumnIndex("totalSize"));
                int completedSize = cursor.getInt(cursor.getColumnIndex("completedSize"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String saveDirPath = cursor.getString(cursor.getColumnIndex("filePath"));
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                int downloadStatus = cursor.getInt(cursor.getColumnIndex("taskStatus"));

                list.add(builder.downloadId(taskId)
                        .totalSize(totalSize)
                        .completedSize(completedSize)
                        .url(url)
                        .filePath(saveDirPath)
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

    public boolean update(TaskEntity entity) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long update = database.update("download_status", getContentValues(entity), "taskId=?", new String[]{entity.getTaskId()});
        return update != 0;
    }

    public boolean delete(TaskEntity entity) {
        if (entity == null) return false;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long delete = database.delete("download_status", "taskId=?", new String[]{entity.getTaskId()});
        database.close();
        return delete != 0;
    }

    public boolean drop() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.execSQL("DROP TABLE download_status");
        database.close();
        return true;
    }

    private ContentValues getContentValues(TaskEntity entity) {
        ContentValues cv = new ContentValues();
        cv.put("taskId", entity.getTaskId());
        cv.put("totalSize", entity.getTotalSize());
        cv.put("completedSize", entity.getCompletedSize());
        cv.put("url", entity.getUrl());
        cv.put("filePath", entity.getFilePath());
        cv.put("fileName", entity.getFileName());
        cv.put("taskStatus", entity.getTaskStatus());
        return cv;
    }


}
