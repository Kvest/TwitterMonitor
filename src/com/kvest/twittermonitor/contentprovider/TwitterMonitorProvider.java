package com.kvest.twittermonitor.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.kvest.twittermonitor.datastorage.TwitterMonitorSQLStorage;
import com.kvest.twittermonitor.datastorage.table.TweetsCache;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11/25/13
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterMonitorProvider extends ContentProvider {
    private TwitterMonitorSQLStorage sqlStorage;

    private static final int TWEETS_URI_INDICATOR = 1;
    private static final int TWEET_URI_INDICATOR = 2;

    private static final UriMatcher uriMatcher;
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TwitterMonitorProviderMetadata.AUTHORITY, TwitterMonitorProviderMetadata.TWEETS_PATH, TWEETS_URI_INDICATOR);
        uriMatcher.addURI(TwitterMonitorProviderMetadata.AUTHORITY, TwitterMonitorProviderMetadata.TWEETS_PATH + "/#", TWEET_URI_INDICATOR);
    }

    @Override
    public boolean onCreate() {
        sqlStorage = new TwitterMonitorSQLStorage(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(uriMatcher.match(uri))
        {
            case TWEETS_URI_INDICATOR :
                queryBuilder.setTables(TweetsCache.TABLE_NAME);
                break;
            case TWEET_URI_INDICATOR :
                queryBuilder.setTables(TweetsCache.TABLE_NAME);
                queryBuilder.appendWhere(TweetsCache._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri = " + uri);
        }

        //make query
        SQLiteDatabase db = sqlStorage.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sqlStorage.getWritableDatabase();
        long rowId = 0;

        switch (uriMatcher.match(uri)) {
            case TWEETS_URI_INDICATOR:
                //replace works as "INSERT OR REPLACE"
                rowId = db.replace(TweetsCache.TABLE_NAME, null, values);
                if (rowId > 0)
                {
                    Uri resultUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                    return resultUri;
                }
                break;
        }

        throw new IllegalArgumentException("Faild to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionParams) {
        int rowsDeleted = 0;
        boolean hasSelection = !TextUtils.isEmpty(selection);
        SQLiteDatabase db = sqlStorage.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case TWEETS_URI_INDICATOR :
                rowsDeleted = db.delete(TweetsCache.TABLE_NAME, selection, selectionParams);
                break;
            case TWEET_URI_INDICATOR :
                rowsDeleted = db.delete(TweetsCache.TABLE_NAME, TweetsCache._ID + "=" + uri.getLastPathSegment() +
                                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionParams : null));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri,ContentValues values, String selection, String[] selectionParams) {
        int rowsUpdated = 0;
        boolean hasSelection = !TextUtils.isEmpty(selection);
        SQLiteDatabase db = sqlStorage.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case TWEETS_URI_INDICATOR :
                rowsUpdated = db.update(TweetsCache.TABLE_NAME, values, selection, selectionParams);
                break;
            case TWEET_URI_INDICATOR :
                rowsUpdated = db.update(TweetsCache.TABLE_NAME, values, TweetsCache._ID + "=" + uri.getLastPathSegment() +
                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionParams : null));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri))
        {
            case TWEETS_URI_INDICATOR : return TwitterMonitorProviderMetadata.CONTENT_TYPE_TWEET_COLLECTION;
            case TWEET_URI_INDICATOR : return TwitterMonitorProviderMetadata.CONTENT_TYPE_TWEET_SINGLE;
            default: throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }


}
