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
public class TweetsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, TweetsListAdapter.LoadMoreListener {
    private static final String ORDER = "\"" + TweetsCache.CREATION_DATE_COLUMN + "\" DESC";

    private static final int LOAD_TWEETS_ID = 0;
    private TweetsListAdapter adapter;

    private LoadMoreTweetsListener loadMoreTweetsListener;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //don't show click on items
        getListView().setSelector(new ColorDrawable(Color.TRANSPARENT));
        getListView().setCacheColorHint(Color.TRANSPARENT);

        //create and set adapter
        String[] from = {TweetsCache.CREATION_DATE_COLUMN, TweetsCache.TEXT_COLUMN, TweetsCache.USER_NAME_COLUMN, TweetsCache.USER_PROFILE_IMAGE_COLUMN };
        int[] to = {R.id.tweet_date, R.id.tweet_text, R.id.user_name};
        adapter = new TweetsListAdapter(getActivity(), R.layout.tweet_list_item, null, from, to, TweetsListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.setLoadMoreListener(this);
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
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    @Override
    public void reload() {
        if (loadMoreTweetsListener != null) {
            loadMoreTweetsListener.reloadTweets();
        }
    }

    @Override
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
