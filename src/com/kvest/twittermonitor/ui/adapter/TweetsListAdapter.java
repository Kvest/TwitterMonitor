package com.kvest.twittermonitor.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.twittermonitor.R;
import com.kvest.twittermonitor.network.VolleyHelper;

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
        if (view.getId() == R.id.profile_image) {
            ((NetworkImageView)view).setDefaultImageResId(R.drawable.default_profile);
            ((NetworkImageView)view).setImageUrl(cursor.getString(columnIndex), VolleyHelper.getInstance().getImageLoader());

            return true;
        }

        return false;
    }
}
