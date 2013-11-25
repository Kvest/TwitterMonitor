package com.kvest.twittermonitor.datastorage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11/25/13
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsSharedPreferencesStorage {
    private static final String STORAGE_NAME = "com.kvest.twittermonitor.datastorage.SettingsSharedPreferencesStorage.SETTINGS";
    private static final String ACCESS_TOKEN_KEY = "com.kvest.twittermonitor.datastorage.SettingsSharedPreferencesStorage.ACCESS_TOKEN";

    //Don't allow to create this class
    private SettingsSharedPreferencesStorage() {}

    public static String getAccessToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return pref.getString(ACCESS_TOKEN_KEY, "");
    }

    public static void setAccessToken(Context context, String accessToken) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            editor.putString(ACCESS_TOKEN_KEY, accessToken);
        } finally {
            editor.commit();
        }
    }
}
