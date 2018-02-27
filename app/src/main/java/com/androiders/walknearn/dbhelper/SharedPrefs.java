package com.androiders.walknearn.dbhelper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by K@R@N on 22-03-2016.
 */
public class SharedPrefs {

    private Context mContext;
    private static final String PREFS = "PREFS";

    private static final String SYNC_TIME = "sync_time";

    public SharedPrefs(Context context) {
        mContext = context;
    }


    public void setSyncTime(String time) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SYNC_TIME, time);
        editor.apply();
    }

    public String getSyncTime() {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SYNC_TIME, null);
    }

    public void clearSharedPreferences() {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
