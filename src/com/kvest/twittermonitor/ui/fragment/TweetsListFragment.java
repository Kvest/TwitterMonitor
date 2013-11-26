package com.kvest.twittermonitor.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import com.kvest.twittermonitor.R;
import com.kvest.twittermonitor.contentprovider.TwitterMonitorProviderMetadata;
import com.kvest.twittermonitor.datastorage.table.TweetsCache;
import com.kvest.twittermonitor.ui.adapter.TweetsListAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */
public class TweetsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ORDER = "\"" + TweetsCache.CREATION_DATE_COLUMN + "\" DESC";
    private static final int LOAD_TWEETS_ID = 0;

    private TweetsListAdapter adapter;
    private LoadMoreTweetsListener loadMoreTweetsListener;

    //for monitoring list's visible items
    private int firstVisibleTweet;
    private int lastVisibleTweet;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstVisibleTweet = 0;
        lastVisibleTweet = 0;

        //don't show click on items
        getListView().setSelector(new ColorDrawable(Color.TRANSPARENT));
        getListView().setCacheColorHint(Color.TRANSPARENT);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleTweet > 0 && firstVisibleItem == 0) {
                    //user has scrolled to the top of the list
                    reload();
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount && (firstVisibleItem + visibleItemCount) != lastVisibleTweet) {
                    //user has scrolled to the bottom of the list
                    loadMore(adapter.getItemId(totalItemCount - 1));
                }

                //remember visible items
                firstVisibleTweet = firstVisibleItem;
                lastVisibleTweet = firstVisibleItem + visibleItemCount;
            }
        });

        //create and set adapter
        String[] from = {TweetsCache.CREATION_DATE_COLUMN, TweetsCache.TEXT_COLUMN, TweetsCache.USER_NAME_COLUMN, TweetsCache.USER_PROFILE_IMAGE_COLUMN };
        int[] to = {R.id.tweet_date, R.id.tweet_text, R.id.user_name, R.id.profile_image};
        adapter = new TweetsListAdapter(getActivity(), R.layout.tweet_list_item, null, from, to, TweetsListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(adapter);

        //load cursor
        getActivity().getSupportLoaderManager().initLoader(LOAD_TWEETS_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            loadMoreTweetsListener = (LoadMoreTweetsListener) activity;
        } catch (ClassCastException cce) {}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOAD_TWEETS_ID : return new CursorLoader(getActivity(), TwitterMonitorProviderMetadata.TWEETS_URI, TweetsCache.FULL_PROJECTION, null, null, ORDER);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //if tweets list is empty - load first "page"
        if (cursor.getCount() == 0) {
            reload();
        }

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    public void reload() {
        if (loadMoreTweetsListener != null) {
            loadMoreTweetsListener.reloadTweets();
        }
    }

    public void loadMore(long fromId) {
        if (loadMoreTweetsListener != null) {
            loadMoreTweetsListener.loadMoreTweets(fromId);
        }
    }

    public interface LoadMoreTweetsListener {
        public void reloadTweets();
        public void loadMoreTweets(long fromId);
    }
}
