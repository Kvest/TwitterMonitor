package com.kvest.twittermonitor.ui.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Window;
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

public class TweetsListActivity extends ActionBarActivity implements TweetsListFragment.LoadMoreTweetsListener {
    private static final String TWITTER_CONSUMER_KEY = "JQ4LFL30eTA86eELxwnhA";
    private static final String TWITTER_CONSUMER_SECRET = "MI22TsCgYij2BTVjmtD3DpNwdL2nW7O0JT78y3xt2BM";
    private static final String TARGET_TOKEN_TYPE = "bearer";
    private static final String REQUESTS_TAG = "requests";
    private static final String SEARCH_KEYWORD = "Android";
    private static final String SEARCH_RESULT_TYPE = "recent";
    private static final int SEARCH_COUNT = 25;

    //search request params
    private TwitterSearchRequest.SearchParams reloadParams;
    private TwitterSearchRequest.SearchParams loadMoreParams;

    //listeners for requests
    private Response.ErrorListener getAccessTokenErrorListener;
    private Response.ErrorListener getTweetsErrorListener;
    private Response.Listener<TwitterSearchResponse> responseListener;
    private TwitterSearchRequest.TweetsResultProcessor loadMoreTweetsProcessor;
    private TwitterSearchRequest.TweetsResultProcessor reloadTweetsProcessor;

    private String accessToken;
    //flag for protecting sending request twice
    private boolean isLoading;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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

    private void cacheTweets(List<Tweet> tweets) {
        //put all tweets in cache
        ContentValues[] contentValues = new ContentValues[tweets.size()];
        for (int i = 0; i < tweets.size(); ++i) {
            contentValues[i] = new ContentValues(5);
            contentValues[i].put(TweetsCache._ID, tweets.get(i).getId());
            contentValues[i].put(TweetsCache.CREATION_DATE_COLUMN, tweets.get(i).getCreatedDate());
            contentValues[i].put(TweetsCache.TEXT_COLUMN, tweets.get(i).getText());
            contentValues[i].put(TweetsCache.USER_NAME_COLUMN, tweets.get(i).getUser().getName());
            contentValues[i].put(TweetsCache.USER_PROFILE_IMAGE_COLUMN, tweets.get(i).getUser().getProfileImageUrl());
        }
        getContentResolver().bulkInsert(TwitterMonitorProviderMetadata.TWEETS_URI, contentValues);
    }

    private void createListeners() {
        //response listeners
        responseListener = new Response.Listener<TwitterSearchResponse>() {
            @Override
            public void onResponse(TwitterSearchResponse response) {
                isLoading = false;
                notifyLoadingFinished();
            }
        };
        loadMoreTweetsProcessor = new TwitterSearchRequest.TweetsResultProcessor() {
            @Override
            public void processTweets(List<Tweet> tweets) {
                //save loaded tweets
                cacheTweets(tweets);
            }
        };
        reloadTweetsProcessor = new TwitterSearchRequest.TweetsResultProcessor() {
            @Override
            public void processTweets(List<Tweet> tweets) {
                //clear cache
                getContentResolver().delete(TwitterMonitorProviderMetadata.TWEETS_URI, null, null);

                //save loaded tweets
                cacheTweets(tweets);
            }
        };

        //error listeners
        getAccessTokenErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                notifyLoadingFinished();

                showErrorMessage(getString(R.string.get_access_token_error));
            }
        };

        getTweetsErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                notifyLoadingFinished();

                showErrorMessage(getString(R.string.get_tweets_error));
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isFinishing()) {
            //if activity is finishing - cancel requests
            setSupportProgressBarIndeterminateVisibility(false);
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
        //don't make request twice
        if (isLoading) {
            return;
        }
        isLoading = true;
        notifyLoading();

        //if we have valid access token - send tweets request, otherwise - send request access token
        if (isAccessTokenValid()) {
            loadTweets(reloadParams, reloadTweetsProcessor, responseListener);
        } else {
            getAccessToken(new Response.Listener<ApplicationAuthenticationResponse>() {
                @Override
                public void onResponse(ApplicationAuthenticationResponse response) {
                    //check if we received required token type
                    if (TARGET_TOKEN_TYPE.equals(response.getTokenType())) {
                        //save access token
                        setAccessToken(response.getAccessToken());

                        //send tweets request
                        loadTweets(reloadParams, reloadTweetsProcessor, responseListener);
                    } else {
                        onWrongTokenType();
                    }
                }
            });
        }
    }

    @Override
    public void loadMoreTweets(long fromId) {
        //don't make request twice
        if (isLoading) {
            return;
        }
        isLoading = true;
        notifyLoading();

        //set max id
        loadMoreParams.setMaxId(fromId);

        //if we have valid access token - send tweets request, otherwise - send request access token
        if (isAccessTokenValid()) {
            loadTweets(loadMoreParams, loadMoreTweetsProcessor, responseListener);
        } else {
            getAccessToken(new Response.Listener<ApplicationAuthenticationResponse>() {
                @Override
                public void onResponse(ApplicationAuthenticationResponse response) {
                    //check if we received required token type
                    if (TARGET_TOKEN_TYPE.equals(response.getTokenType())) {
                        //save access token
                        setAccessToken(response.getAccessToken());

                        //send tweets request
                        loadTweets(loadMoreParams, loadMoreTweetsProcessor, responseListener);
                    } else {
                        onWrongTokenType();
                    }
                }
            });
        }
    }

    private void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        SettingsSharedPreferencesStorage.setAccessToken(this, this.accessToken);
    }

    private void loadTweets(TwitterSearchRequest.SearchParams params,
                            TwitterSearchRequest.TweetsResultProcessor processor,
                            Response.Listener<TwitterSearchResponse> listener) {
        TwitterSearchRequest request = new TwitterSearchRequest(accessToken, params, processor, listener, getTweetsErrorListener);
        request.setTag(REQUESTS_TAG);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void getAccessToken(Response.Listener<ApplicationAuthenticationResponse> listener) {
        ApplicationAuthenticationRequest request = new ApplicationAuthenticationRequest(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, listener, getAccessTokenErrorListener);
        request.setTag(REQUESTS_TAG);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void notifyLoading() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    private void notifyLoadingFinished() {
        setSupportProgressBarIndeterminateVisibility(false);
    }

    private void onWrongTokenType() {
        isLoading = false;
        notifyLoadingFinished();

        showErrorMessage(getString(R.string.wrong_token_type_error));
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
