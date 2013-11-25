package com.kvest.twittermonitor.datastorage.table;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11/25/13
 * Time: 8:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class TweetsCache implements BaseColumns {
    public static final String TABLE_NAME = "tweets_cache";

    public static final String CREATION_DATE_COLUMN = "creation_date";
    public static final String TEXT_COLUMN = "text";
    public static final String USER_NAME_COLUMN = "user_name";
    public static final String USER_PROFILE_IMAGE_COLUMN = "user_profile_image";

    public static final String[] FULL_PROJECTION = {_ID, CREATION_DATE_COLUMN, TEXT_COLUMN, USER_NAME_COLUMN, USER_PROFILE_IMAGE_COLUMN };

    public static final String CREATE_INDEX_SQL = "CREATE INDEX IF NOT EXISTS \"" + CREATION_DATE_COLUMN + "_index\" ON \"" + TABLE_NAME + "\"(\"" + CREATION_DATE_COLUMN + "\") DESC;";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE \"" + TABLE_NAME + "\" (\"" +
                                                  _ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, \"" +
                                                  CREATION_DATE_COLUMN + "\" INTEGER DEFAULT 0, \"" +
                                                  TEXT_COLUMN + "\" TEXT DEFAULT \"\", \"" +
                                                  USER_NAME_COLUMN + "\" TEXT DEFAULT \"\", \"" +
                                                  USER_PROFILE_IMAGE_COLUMN + "\" TEXT DEFAULT \"\");" +
                                                  CREATE_INDEX_SQL;




    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS \"" + TABLE_NAME + "\";";
}
