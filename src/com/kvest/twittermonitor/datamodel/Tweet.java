package com.kvest.twittermonitor.datamodel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 23:07
 * To change this template use File | Settings | File Templates.
 */
public class Tweet {
    public static final String TWITTER_DATE_TIME_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(TWITTER_DATE_TIME_FORMAT, Locale.ENGLISH);

    private long id;
    private User user;
    private String created_at;
    private String text;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public long getCreatedDate() {
        Date date = null;
        try {
            date = dateFormat.parse(created_at);
        } catch (ParseException pe) {};

        if (date != null) {
            return date.getTime();
        } else {
            return 0;
        }
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "[" + id + "][" + created_at + "]" + "[" + user.toString() + "]"  + text;
    }
}
