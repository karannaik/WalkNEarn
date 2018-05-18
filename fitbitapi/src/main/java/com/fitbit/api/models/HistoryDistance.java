package com.fitbit.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by KARAN on 17-05-2018.
 */

public class HistoryDistance {

    @SerializedName("activities-distance")
    @Expose
    private List<HistoryValuesModel> activity;

    /**
     * @return The activity
     */
    public List<HistoryValuesModel> getActivity() {
        return activity;
    }

    /**
     * @param activity The activity
     */
    public void setActivity(List<HistoryValuesModel> activity) {
        this.activity = activity;
    }

}
