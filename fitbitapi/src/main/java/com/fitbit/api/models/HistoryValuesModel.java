package com.fitbit.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KARAN on 16-05-2018.
 */

public class HistoryValuesModel {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("value")
    @Expose
    private String value;

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
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
