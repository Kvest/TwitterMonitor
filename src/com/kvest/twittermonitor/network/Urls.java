package com.kvest.twittermonitor.network;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */
public abstract class Urls {
    //urls
    public static final String TWITTER_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    public static final String TWITTER_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json";
    //request params
    public static final String TWITTER_SEARCH_PARAM_Q = "q";
    public static final String TWITTER_SEARCH_PARAM_RESULT_TYPE = "result_type";
    public static final String TWITTER_SEARCH_PARAM_COUNT = "count";
    public static final String TWITTER_SEARCH_PARAM_SINCE_ID = "since_id";
    public static final String TWITTER_SEARCH_PARAM_MAX_ID = "max_id";
}
