package com.kvest.twittermonitor.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;
import com.kvest.twittermonitor.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11/25/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TweetsListAdapter extends SimpleCursorAdapter implements SimpleCursorAdapter.ViewBinder {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SimpleDateFormat dateFormat;
    private LoadMoreListener loadMoreListener;
    private boolean firstCall = true;

    public TweetsListAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        setViewBinder(this);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (view.getId() == R.id.user_name) {
            ((TextView)view).setText("@" + cursor.getString(columnIndex));

            return true;
        }
        if (view.getId() == R.id.tweet_date) {
            Date date = new Date(cursor.getLong(columnIndex));
            ((TextView)view).setText(dateFormat.format(date));

            return true;
        }

        return false;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        if (cursor.getPosition() == 0) {
            if (!firstCall && loadMoreListener != null) {
                loadMoreListener.reload();
            }
            firstCall = false;
        }
        if ((cursor.getCount() - 1) == cursor.getPosition() && loadMoreListener != null) {
            loadMoreListener.loadMore(getItemId(cursor.getPosition()));
        }
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface LoadMoreListener {
        public void reload();
        public void loadMore(long fromId);
    }
}
