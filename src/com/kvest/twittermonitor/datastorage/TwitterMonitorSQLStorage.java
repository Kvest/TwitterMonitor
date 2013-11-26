package com.kvest.twittermonitor.datastorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kvest.twittermonitor.datastorage.table.TweetsCache;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11/25/13
 * Time: 8:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterMonitorSQLStorage  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "twittermonitor_db";
    private static final int DATABASE_VERSION = 1;

    public TwitterMonitorSQLStorage(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //create all tables
        database.execSQL(TweetsCache.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //Nothing to do yet
    }
}
