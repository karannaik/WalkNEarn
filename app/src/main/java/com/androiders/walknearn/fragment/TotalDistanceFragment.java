package com.androiders.walknearn.fragment;

import android.app.Fragment;
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
import com.fitbit.api.models.HistoryDistance;
import com.fitbit.api.models.HistoryValuesModel;
import com.fitbit.api.services.HistoryCaloriesService;
import com.fitbit.api.services.HistoryDistancesService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TotalDistanceFragment extends InfoFragment<HistoryDistance> {
    private View view;
    private TextView mTextViewDistance;
    public List<HistoryValuesModel> historyValuesModelList = new ArrayList<>();

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

//        total = String.format("%.3f", Double.parseDouble(total) * 0.000621371);
        ((TextView)view.findViewById(R.id.textViewDistance)).setText(""+total);
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
    public Loader<ResourceLoaderResult<HistoryDistance>> onCreateLoader(int id, Bundle args) {
        return HistoryDistancesService.getHistoryDistanceSummaryLoader(getActivity(), new Date());
    }

    @Override
    public void onLoadFinished(Loader<ResourceLoaderResult<HistoryDistance>> loader, ResourceLoaderResult<HistoryDistance> data) {
        super.onLoadFinished(loader, data);
        if (data.isSuccessful()) {
            historyValuesModelList = data.getResult().getActivity();
            updateText(historyValuesModelList.get(historyValuesModelList.size()-1).getValue());
            ((Main2Activity)getActivity()).updateDistance(historyValuesModelList);
        }
    }
}
