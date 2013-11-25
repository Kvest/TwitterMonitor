package com.kvest.twittermonitor.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.kvest.twittermonitor.R;
import com.kvest.twittermonitor.network.VolleyHelper;
import com.kvest.twittermonitor.ui.fragment.TweetsListFragment;

public class TweetsListActivity extends FragmentActivity implements TweetsListFragment.LoadMoreTweetsListener {

    public static final String REQUESTS_TAG = "requests";
    public static final String SEARCH_KEYWORD = "Android";
    public static final String SEARCH_RESULT_TYPE = "recent";
    public static final int SEARCH_COUNT = 25;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String token = "AAAAAAAAAAAAAAAAAAAAAK26QgAAAAAAg9ESLMhBCsYa9PpP5Z5kuw0kLuU%3D21UG0hSRin9s45fVcf7dqoG5AvR8PaLGlGByOXHaWrFJA8xHbp";
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

    @Override
    public void onPause() {
        if (isFinishing()) {
            VolleyHelper.getInstance().cancelAll(REQUESTS_TAG);
        }
    }

    @Override
    public void reloadTweets() {
        Log.d("KVEST_TAG", "reloadTweets");
    }

    @Override
    public void loadMoreTweets(long fromId) {
        Log.d("KVEST_TAG", "loadMoreTweets " + fromId);
    }
}
