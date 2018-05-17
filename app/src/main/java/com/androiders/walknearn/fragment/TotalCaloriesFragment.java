package com.androiders.walknearn.fragment;

import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiders.walknearn.Main2Activity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.fitbitfiles.fragments.InfoFragment;
import com.fitbit.api.loaders.ResourceLoaderResult;
import com.fitbit.api.models.HistoryCalories;
import com.fitbit.api.models.HistoryValuesModel;
import com.fitbit.api.services.HistoryCaloriesService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TotalCaloriesFragment extends InfoFragment<HistoryCalories> {

    private View view;
    private TextView mTextViewCalories;
    public List<HistoryValuesModel> historyValuesModelList = new ArrayList<>();

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


    @Override
    public int getTitleResourceId() {
        return R.string.activity_info;
    }

    @Override
    protected int getLoaderId() {
        return 3;
    }

    @Override
    public Loader<ResourceLoaderResult<HistoryCalories>> onCreateLoader(int id, Bundle args) {
        return HistoryCaloriesService.getHistoryCaloriesSummaryLoader(getActivity(), new Date());
    }

    @Override
    public void onLoadFinished(Loader<ResourceLoaderResult<HistoryCalories>> loader, ResourceLoaderResult<HistoryCalories> data) {
        super.onLoadFinished(loader, data);
        if (data.isSuccessful()) {
            historyValuesModelList = data.getResult().getActivity();
            ((Main2Activity)getActivity()).updateCalories(historyValuesModelList);
        }
    }

}
