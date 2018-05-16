package com.androiders.walknearn.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.androiders.walknearn.fitbitfiles.FitbitAuthApplication;

/**
 * Created by Dmytro Denysenko on 5/6/15.
 */
public class CanaroTextView extends TextView {


    public CanaroTextView(Context context) {
        this(context, null);
    }

    public CanaroTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanaroTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FitbitAuthApplication.canaroExtraBold);
    }

}
