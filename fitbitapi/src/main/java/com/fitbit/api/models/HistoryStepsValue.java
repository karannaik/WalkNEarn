package com.fitbit.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KARAN on 16-05-2018.
 */

public class HistoryStepsValue {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("value")
    @Expose
    private Integer value;

    /**
     * @return The date
     */
    public String getDate() {
        return dateTime;
    }

    /**
     * @param dateTime The date
     */
    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return The value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(Integer value) {
        this.value = value;
    }
}
