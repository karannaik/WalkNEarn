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
    private static final String LOCATION_PERMISSION = "location_permission";
    private static final String FITNESS_TRACKER_PERMISSION = "fitness_tracker_permission";

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


    public void setIsLocationGiven(boolean isGiven) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(LOCATION_PERMISSION, isGiven);
        editor.apply();
    }

    public boolean getIsLocationGiven() {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(LOCATION_PERMISSION, false);
    }

    public void setIsFitnessTrackerGiven(boolean isGiven) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(FITNESS_TRACKER_PERMISSION, isGiven);
        editor.apply();
    }

    public boolean getIsFitnessTrackerGiven() {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(FITNESS_TRACKER_PERMISSION, false);
    }

    public void clearSharedPreferences() {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
