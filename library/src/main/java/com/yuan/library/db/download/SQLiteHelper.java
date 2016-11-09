package com.yuan.library.db.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yuan on 8/24/16.
 * <p>
 * SQLite Helper
 */

class SQLiteHelper extends SQLiteOpenHelper {


    SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createSql = "CREATE TABLE if not exists " + "download_status" + " ("
                + "id INTEGER PRIMARY KEY autoincrement,"
                + "downloadId TEXT,"
                + "totalSize LONG,"
                + "completedSize LONG,"
                + "url TEXT,"
                + "saveDirPath TEXT,"
                + "fileName TEXT,"
                + "downloadStatus INTEGER" + ");";
        sqLiteDatabase.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("ALTER TABLE download_status");
    }

}
