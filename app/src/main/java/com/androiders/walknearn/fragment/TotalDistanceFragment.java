package com.androiders.walknearn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiders.walknearn.R;

public class TotalDistanceFragment extends Fragment {
    private View view;
    private TextView mTextViewDistance;

    public TotalDistanceFragment() {
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
        view = inflater.inflate(R.layout.fragment_total_distance, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {

        mTextViewDistance = (TextView) view.findViewById(R.id.textViewDistance);

    }

    public void updateText(String total) {

        ((TextView)view.findViewById(R.id.textViewDistance)).setText(""+total);
    }
}
