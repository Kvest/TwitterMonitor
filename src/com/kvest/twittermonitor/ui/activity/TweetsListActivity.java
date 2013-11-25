package com.kvest.twittermonitor.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.twittermonitor.R;
import com.kvest.twittermonitor.datastorage.SettingsSharedPreferencesStorage;
import com.kvest.twittermonitor.network.VolleyHelper;
import com.kvest.twittermonitor.network.request.ApplicationAuthenticationRequest;
import com.kvest.twittermonitor.network.request.TwitterSearchRequest;
import com.kvest.twittermonitor.network.response.ApplicationAuthenticationResponse;
import com.kvest.twittermonitor.ui.fragment.TweetsListFragment;

public class TweetsListActivity extends FragmentActivity implements TweetsListFragment.LoadMoreTweetsListener {
    private static final String TWITTER_CONSUMER_KEY = "JQ4LFL30eTA86eELxwnhA";
    private static final String TWITTER_CONSUMER_SECRET = "MI22TsCgYij2BTVjmtD3DpNwdL2nW7O0JT78y3xt2BM";
    private static final String REQUESTS_TAG = "requests";
    private static final String SEARCH_KEYWORD = "Android";
    private static final String SEARCH_RESULT_TYPE = "recent";
    private static final int SEARCH_COUNT = 25;

    private TwitterSearchRequest.SearchParams reloadParams;
    private TwitterSearchRequest.SearchParams loadMoreParams;

    private Response.ErrorListener getAccessTokenErrorListener;

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

        showErrorMessage(getString(R.string.get_access_token_error));

//        String token = "AAAAAAAAAAAAAAAAAAAAAK26QgAAAAAAg9ESLMhBCsYa9PpP5Z5kuw0kLuU%3D21UG0hSRin9s45fVcf7dqoG5AvR8PaLGlGByOXHaWrFJA8xHbp";
//        ApplicationAuthenticationRequest r = new ApplicationAuthenticationRequest("McNdweY7BOJXNYcNrlsRQ", "FdLYAayo6QAauKINXXdYQRXw5ZTx09EUNUKTr5BcUIA", new Response.Listener<ApplicationAuthenticationResponse>() {
//            @Override
//            public void onResponse(ApplicationAuthenticationResponse response) {
//                Log.d("KVEST_TAG", response.getAccessToken());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });

//        TwitterSearchRequest.SearchParams p = new TwitterSearchRequest.SearchParams("Android");
//        p.setResultType("recent");
//        p.setCount(50);
//        //p.setSinceId(404823739680174080l);
//        TwitterSearchRequest r = new TwitterSearchRequest(token, p, new Response.Listener<TwitterSearchResponse>() {
//            @Override
//            public void onResponse(TwitterSearchResponse response) {
//                for (Tweet tweet : response.getStatuses()) {
//                    ContentValues cv = new ContentValues(5);
//                    cv.put(TweetsCache._ID, tweet.getId());
//                    cv.put(TweetsCache.CREATION_DATE_COLUMN, tweet.getCreatedDate());
//                    cv.put(TweetsCache.TEXT_COLUMN, tweet.getText());
//                    cv.put(TweetsCache.USER_NAME_COLUMN, tweet.getUser().getName());
//                    cv.put(TweetsCache.USER_PROFILE_IMAGE_COLUMN, tweet.getUser().getProfileImageUrl());
//                    getContentResolver().insert(TwitterMonitorProviderMetadata.TWEETS_URI, cv);
//                }
//
//                Cursor cursor = getContentResolver().query(TwitterMonitorProviderMetadata.TWEETS_URI, TweetsCache.FULL_PROJECTION, null, null, null);
//                try {
//                    Log.d("KVEST_TAG", "count=" + cursor.getCount());
//                }  finally {
//                    cursor.close();
//                }
//            }
//        },
//        new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//        r.setTag(REQUESTS_TAG);
//        VolleyHelper.getInstance().addRequest(r);
    }

    private void createSearchRequestParams() {
        reloadParams = new TwitterSearchRequest.SearchParams(SEARCH_KEYWORD);
        reloadParams.setResultType(SEARCH_RESULT_TYPE);
        reloadParams.setCount(SEARCH_COUNT);

        loadMoreParams = new TwitterSearchRequest.SearchParams(SEARCH_KEYWORD);
        loadMoreParams.setResultType(SEARCH_RESULT_TYPE);
        loadMoreParams.setCount(SEARCH_COUNT);
    }

    private void createListeners() {
        getAccessTokenErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;

                showErrorMessage(getString(R.string.get_access_token_error));
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

        if (isAccessTokenValid()) {
            //TODO
        } else {
            //TODO
            //getAccessToken()
        }
    }

    @Override
    public void loadMoreTweets(long fromId) {
        //don't load twice
        if (isLoading) {
            return;
        }

        //set max id
        loadMoreParams.setMaxId(fromId);

        if (isAccessTokenValid()) {
            //TODO
        } else {
            //TODO
            //getAccessToken();
        }
        Log.d("KVEST_TAG", "loadMoreTweets " + fromId);
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
