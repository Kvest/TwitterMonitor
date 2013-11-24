package com.kvest.twittermonitor.core;

import android.app.Application;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public class TwitterMonitorApplication extends Application {
    private static Context context;

    @Override
    public void onCreate () {
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
