package com.kvest.twittermonitor.ui.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.twittermonitor.R;
import com.kvest.twittermonitor.contentprovider.TwitterMonitorProviderMetadata;
import com.kvest.twittermonitor.datamodel.Tweet;
import com.kvest.twittermonitor.datastorage.SettingsSharedPreferencesStorage;
import com.kvest.twittermonitor.datastorage.table.TweetsCache;
import com.kvest.twittermonitor.network.VolleyHelper;
import com.kvest.twittermonitor.network.request.ApplicationAuthenticationRequest;
import com.kvest.twittermonitor.network.request.TwitterSearchRequest;
import com.kvest.twittermonitor.network.response.ApplicationAuthenticationResponse;
import com.kvest.twittermonitor.network.response.TwitterSearchResponse;
import com.kvest.twittermonitor.ui.fragment.TweetsListFragment;

import java.util.List;

public class TweetsListActivity extends FragmentActivity implements TweetsListFragment.LoadMoreTweetsListener {
    private static final String TWITTER_CONSUMER_KEY = "JQ4LFL30eTA86eELxwnhA";
    private static final String TWITTER_CONSUMER_SECRET = "MI22TsCgYij2BTVjmtD3DpNwdL2nW7O0JT78y3xt2BM";
    private static final String TARGET_TOKEN_TYPE = "bearer";
    private static final String REQUESTS_TAG = "requests";
    private static final String SEARCH_KEYWORD = "Android";
    private static final String SEARCH_RESULT_TYPE = "recent";
    private static final int SEARCH_COUNT = 25;

    private TwitterSearchRequest.SearchParams reloadParams;
    private TwitterSearchRequest.SearchParams loadMoreParams;

    private Response.ErrorListener getAccessTokenErrorListener;
    private Response.ErrorListener getTweetsErrorListener;
    private Response.Listener<TwitterSearchResponse> loadMoreTweetsListener;
    private Response.Listener<TwitterSearchResponse> reloadTweetsListener;

    private String accessToken;
    private boolean isLoading;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //create search params
        createSearchRequestParams();

        //create listeners
        createListeners();

        //load saved accessToken
        accessToken = SettingsSharedPreferencesStorage.getAccessToken(this);
        isLoading = false;
    }

    private void createSearchRequestParams() {
        reloadParams = new TwitterSearchRequest.SearchParams(SEARCH_KEYWORD);
        reloadParams.setResultType(SEARCH_RESULT_TYPE);
        reloadParams.setCount(SEARCH_COUNT);

        loadMoreParams = new TwitterSearchRequest.SearchParams(SEARCH_KEYWORD);
        loadMoreParams.setResultType(SEARCH_RESULT_TYPE);
        loadMoreParams.setCount(SEARCH_COUNT);
    }

    private void cacheTweets(final List<Tweet> tweets) {
        //put all tweets in cache(in new thread because we shouldn't stop the main thread for a long time)
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Tweet tweet : tweets) {
                    ContentValues contentValues = new ContentValues(5);
                    contentValues.put(TweetsCache._ID, tweet.getId());
                    contentValues.put(TweetsCache.CREATION_DATE_COLUMN, tweet.getCreatedDate());
                    contentValues.put(TweetsCache.TEXT_COLUMN, tweet.getText());
                    contentValues.put(TweetsCache.USER_NAME_COLUMN, tweet.getUser().getName());
                    contentValues.put(TweetsCache.USER_PROFILE_IMAGE_COLUMN, tweet.getUser().getProfileImageUrl());
                    getContentResolver().insert(TwitterMonitorProviderMetadata.TWEETS_URI, contentValues);
                }
            }
        }).start();
    }

    private void createListeners() {
        loadMoreTweetsListener = new Response.Listener<TwitterSearchResponse>() {
            @Override
            public void onResponse(TwitterSearchResponse response) {
                isLoading = false;

                //save loaded tweets
                cacheTweets(response.getStatuses());
            }
        };
        reloadTweetsListener = new Response.Listener<TwitterSearchResponse>() {
            @Override
            public void onResponse(TwitterSearchResponse response) {
                isLoading = false;

                //clear cache
                getContentResolver().delete(TwitterMonitorProviderMetadata.TWEETS_URI, null, null);

                //save loaded tweets
                cacheTweets(response.getStatuses());
            }
        };

        //error listeners
        getAccessTokenErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;

                showErrorMessage(getString(R.string.get_access_token_error));
            }
        };

        getTweetsErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;

                showErrorMessage(getString(R.string.get_tweets_error));
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isFinishing()) {
            VolleyHelper.getInstance().cancelAll(REQUESTS_TAG);
        }
    }

    private boolean isAccessTokenValid() {
        //TODO
        //add accessToken verification

        return !TextUtils.isEmpty(accessToken);
    }

    @Override
    public void reloadTweets() {
        //don't load twice
        if (isLoading) {
            return;
        }
        isLoading = true;

        if (isAccessTokenValid()) {
            loadTweets(reloadParams, reloadTweetsListener);
        } else {
            getAccessToken(new Response.Listener<ApplicationAuthenticationResponse>() {
                @Override
                public void onResponse(ApplicationAuthenticationResponse response) {
                    if (TARGET_TOKEN_TYPE.equals(response.getTokenType())) {
                        setAccessToken(response.getAccessToken());

                        loadTweets(reloadParams, reloadTweetsListener);
                    }
                }
            });
        }
    }

    @Override
    public void loadMoreTweets(long fromId) {
        //don't load twice
        if (isLoading) {
            return;
        }
        isLoading = true;

        //set max id
        loadMoreParams.setMaxId(fromId);

        if (isAccessTokenValid()) {
            loadTweets(loadMoreParams, loadMoreTweetsListener);
        } else {
            getAccessToken(new Response.Listener<ApplicationAuthenticationResponse>() {
                @Override
                public void onResponse(ApplicationAuthenticationResponse response) {
                    if (TARGET_TOKEN_TYPE.equals(response.getTokenType())) {
                        setAccessToken(response.getAccessToken());

                        loadTweets(loadMoreParams, loadMoreTweetsListener);
                    }
                }
            });
        }
    }

    private void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        SettingsSharedPreferencesStorage.setAccessToken(this, this.accessToken);
    }

    private void loadTweets(TwitterSearchRequest.SearchParams params, Response.Listener<TwitterSearchResponse> listener) {
        TwitterSearchRequest request = new TwitterSearchRequest(accessToken, params, listener, getTweetsErrorListener);
        request.setTag(REQUESTS_TAG);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void getAccessToken(Response.Listener<ApplicationAuthenticationResponse> listener) {
        ApplicationAuthenticationRequest request = new ApplicationAuthenticationRequest(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, listener, getAccessTokenErrorListener);
        request.setTag(REQUESTS_TAG);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
