package com.kvest.twittermonitor.contentprovider;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11/25/13
 * Time: 9:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterMonitorProviderMetadata {
    //Don't allow to create this class
    private TwitterMonitorProviderMetadata(){}

    public static final String AUTHORITY = "com.kvest.twittermonitor.contentprovider.TwitterMonitorProvider";

    public static final String CONTENT_TYPE_TWEET_COLLECTION = "vnd.android.cursor.dir/vnd.kvest.tweet";
    public static final String CONTENT_TYPE_TWEET_SINGLE = "vnd.android.cursor.item/vnd.kvest.tweet";

    public static final String TWEETS_PATH = "tweets";

    public static final Uri TWEETS_URI = Uri.parse("content://" + AUTHORITY + "/" + TWEETS_PATH);

}
