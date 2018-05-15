package com.androiders.walknearn.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiders.walknearn.R;

public class TotalCaloriesFragment extends Fragment {

    private View view;
    private TextView mTextViewCalories;

    public TotalCaloriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_total_calories, container, false);

        initializeViews(view);

        return view;
    }


    private void initializeViews(View view) {

        mTextViewCalories = (TextView) view.findViewById(R.id.textViewCalories);

    }

    public void updateText(String total) {

        ((TextView)view.findViewById(R.id.textViewCalories)).setText(""+total);
    }
}
