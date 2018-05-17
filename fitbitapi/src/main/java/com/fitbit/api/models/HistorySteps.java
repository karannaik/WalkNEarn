package com.fitbit.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by KARAN on 16-05-2018.
 */
public class HistorySteps {

    @SerializedName("activities-steps")
    @Expose
    private List<HistoryStepsValue> activity;

    /**
     * @return The activity
     */
    public List<HistoryStepsValue> getActivity() {
        return activity;
    }

    /**
     * @param activity The activity
     */
    public void setActivity(List<HistoryStepsValue> activity) {
        this.activity = activity;
    }


}
