package com.androiders.walknearn.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androiders.walknearn.Main2Activity;
import com.androiders.walknearn.MainActivity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.dbhelper.SharedPrefs;
import com.androiders.walknearn.fitbitfiles.fragments.InfoFragment;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.widgets.flowingdrawer.LeftDrawerLayout;
import com.fitbit.api.loaders.ResourceLoaderResult;
import com.fitbit.api.models.DailyActivitySummary;
import com.fitbit.api.models.Goals;
import com.fitbit.api.models.Summary;
import com.fitbit.api.services.ActivityService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends InfoFragment<DailyActivitySummary> implements AdapterView.OnItemSelectedListener {

    private View view;
    public static final String TAG = "StepCounter";
    private TotalCaloriesFragment mFragmentTotalCalories;
    private TotalStepsFragment mFragmentTotalSteps;
    private TotalDistanceFragment mFragmentTotalDistance;
    private PageIndicatorView pageIndicatorView;
    private TextView mWalkCoins;
    private int timeSpinnerPos = 1;

    private static final int GRAPH_DAILY = 1;
    private static final int GRAPH_WEEKLY = 2;
    private static final int GRAPH_MONTHLY = 3;

    private static final int GRAPH_STEPS = 1;
    private static final int GRAPH_CALORIES = 2;
    private static final int GRAPH_DISTANCE = 3;
    private BarChart barChart;
    private int detailsSpinnerPos = 1;
    private String currentFitnessTracker;

    public HomeFragment() {
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
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);

        currentFitnessTracker = new SharedPrefs(getActivity()).getFitnessTrackerGiven();


        if (currentFitnessTracker.equals(SharedPrefs.FITNESS_TRACKER_GOOGLE_FIT)) {
            subscribe(); // Querying the daily data about user's physical activity and showing them on main page
        }
        setSpinnerForGraph(view); // Setting up the spinners for selecting the criteria for displaying graph

        return view;
    }

    private void initializeViews(View view) {

        barChart = view.findViewById(R.id.bar_chart);

        ViewPager mViewPager = view.findViewById(R.id.viewPager);
        setupViewPager(mViewPager);

        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(3); // specify total count of indicators
        pageIndicatorView.setAnimationType(AnimationType.WORM);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);

                switch (position) {
                    case 0:
                        detailsSpinnerPos = GRAPH_STEPS;
                        if (currentFitnessTracker.equals(SharedPrefs.FITNESS_TRACKER_GOOGLE_FIT)) {

                            readDailyStepsData();
                        }
                        break;
                    case 1:
                        detailsSpinnerPos = GRAPH_CALORIES;

                        if (currentFitnessTracker.equals(SharedPrefs.FITNESS_TRACKER_GOOGLE_FIT)) {

                            readDailyCaloriesData();
                        }
                        break;
                    case 2:
                        detailsSpinnerPos = GRAPH_DISTANCE;
                        if (currentFitnessTracker.equals(SharedPrefs.FITNESS_TRACKER_GOOGLE_FIT)) {
                            readDailyDistanceData();
                        }
                        break;
                }

                initializeGraph(timeSpinnerPos, detailsSpinnerPos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });

        mViewPager.setOffscreenPageLimit(3);

    }

    // Sets the viewsPager on the main activity to display the user physical activity
    private void setupViewPager(ViewPager viewPager) {
        mFragmentTotalSteps = new TotalStepsFragment();
        mFragmentTotalCalories = new TotalCaloriesFragment();
        mFragmentTotalDistance = new TotalDistanceFragment();
        HomeFragment.ViewPagerAdapter adapter = new HomeFragment.ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(mFragmentTotalSteps, "Total Steps");
        adapter.addFragment(mFragmentTotalCalories, "Total Calories");
        adapter.addFragment(mFragmentTotalDistance, "Total Distance");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    // Class which helps in displaying the user's physical activity in the viewer pages
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Records step data by requesting a subscription to background step data
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Successfully subscribed!");
                            //setAutomaticRefreshTimers();
                            readData();
                        } else {
                            Log.w(TAG, "There was a problem subscribing.", task.getException());
                        }
                    }
                });
    }

    // Reads the current daily steps taken, daily calories burned, and distance walked
    // computed from midnight of the current day on the device's current timezone
    private void readData() {
        // Call to the method which reads the steps taken daily
        readDailyStepsData();
        // Call to the method which reads the calories burned daily
        readDailyCaloriesData();
        // Call to the method which reads the distance walked daily
        readDailyDistanceData();
    }

    public void updateTotalSteps(long total) {
        mFragmentTotalSteps.updateText(total);
        //((Main2Activity)getActivity()).updateWalkcoins(Integer.parseInt(""+total/1000));
    }


    public void updateTotalCalories(String total) {
        mFragmentTotalCalories.updateText(total);
    }

    public void updateTotalDistance(float total) {
        mFragmentTotalDistance.updateText("" + total);
    }

    // This method reads the steps taken daily, and updates it on the first view page of the main activity
    private void readDailyStepsData() {
        Fitness.getHistoryClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        long total = dataSet.isEmpty() ? 0 : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        //call fragment methods
                        updateTotalSteps(total);
//                        mMenuFragment.updateStepcount(Double.toString(total/2000.0));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "There was a problem getting the step count.", e);
                    }
                });
    }

    // This method reads the calories burned daily, and updates it on the second view page of the main activity
    private void readDailyCaloriesData() {
        Fitness.getHistoryClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        if (dataSet.getDataPoints().size() != 0) {
                            String total = dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).toString();
                            //call fragment methods
                            updateTotalCalories("" + Math.round(Float.parseFloat(total)));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "There was a problem getting the calories burned.", e);
                    }
                });
    }

    // This method reads the distance walked daily, and updates it on the third view page of the main activity
    private void readDailyDistanceData() {
        Fitness.getHistoryClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        float total = dataSet.isEmpty() ? 0 : dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat();
                        Log.i(TAG, "Total distance: " + total);
                        updateTotalDistance(total);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "There was a problem getting the distance walked count.", e);
                    }
                });
    }

    // Method helps in selecting the criteria for displaying the graph
    private void setSpinnerForGraph(View view) {
        Spinner timeSpinner = view.findViewById(R.id.time_spinner);

        // Spinner click listener
        timeSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> timeCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.time_spinner)));
        List<String> detailsCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.details_spinner)));

        // Creating adapter for spinners
        ArrayAdapter<String> timeDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, timeCategories);

        // Drop down layout style - list view with radio Button
        timeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        timeSpinner.setAdapter(timeDataAdapter);
    }

    // Method specifies what is to be done on selecting the spinner options
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        String ItemSelected = parent.getSelectedItem().toString();
        if (ItemSelected.compareTo("Daily") == 0)
            timeSpinnerPos = 1;
        else if (ItemSelected.compareTo("Weekly") == 0)
            timeSpinnerPos = 2;
        else if (ItemSelected.compareTo("Monthly") == 0)
            timeSpinnerPos = 3;
        initializeGraph(timeSpinnerPos, detailsSpinnerPos);
    }

    // Method sets default spinner items when no spinner item is selected
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        timeSpinnerPos = 1;
        if (currentFitnessTracker.equals(SharedPrefs.FITNESS_TRACKER_GOOGLE_FIT)) {
            initializeGraph(timeSpinnerPos, detailsSpinnerPos);
        }
    }

    // Input parameters : criteria based on which graph is drawn (Time criteria, Type of History)
    // Method sets the bucket size for retrieving the data,
    // specifies what is to be retrieved, and
    // the time frame within which it is to be retrieved
    private void initializeGraph(int timeType, int detailsType) {
        long startTime = 0, endTime = 0;
        TimeUnit timeUnit = TimeUnit.DAYS;
        DataType dataType = DataType.TYPE_STEP_COUNT_DELTA, aggregateDataType = DataType.AGGREGATE_STEP_COUNT_DELTA;
        Field field = Field.FIELD_STEPS;
        int calendarType = Calendar.DAY_OF_WEEK, groupSize = 1, descriptionCode, typeDesc = 1, detailDesc = 1;
        switch (timeType) {
            case GRAPH_DAILY: {
                calendarType = Calendar.HOUR_OF_DAY;
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                if ((calendar.get(Calendar.HOUR) > 0) || (calendar.get(Calendar.HOUR) == 0 && (calendar.get(Calendar.MINUTE) > 0 || calendar.get(Calendar.SECOND) > 0))) {
                    calendar.add(Calendar.DATE, +1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                }
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.HOUR_OF_DAY, -24); // Past 24 hours
                startTime = calendar.getTimeInMillis();
                timeUnit = TimeUnit.HOURS;
                groupSize = 4;
                typeDesc = 1;
            }
            break;
            case GRAPH_WEEKLY: {
                calendarType = Calendar.DAY_OF_WEEK;
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                if ((calendar.get(Calendar.HOUR) > 0) || (calendar.get(Calendar.HOUR) == 0 && (calendar.get(Calendar.MINUTE) > 0 || calendar.get(Calendar.SECOND) > 0))) {
                    calendar.add(Calendar.DATE, +1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                }
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_WEEK, -7); // Past 7 days
                startTime = calendar.getTimeInMillis();
                timeUnit = TimeUnit.DAYS;
                typeDesc = 2;
            }
            break;
            case GRAPH_MONTHLY: {
                calendarType = Calendar.WEEK_OF_MONTH;
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
                if ((day_of_week < 7) || (day_of_week == 7 && ((calendar.get(Calendar.HOUR) > 0) ||
                        (calendar.get(Calendar.HOUR) == 0 && (calendar.get(Calendar.MINUTE) > 0 || calendar.get(Calendar.SECOND) > 0))))) {
                    calendar.add(Calendar.DATE, +7 - day_of_week + 1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                }
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.WEEK_OF_MONTH, -4); // Past 4 weeks
                startTime = calendar.getTimeInMillis();
                timeUnit = TimeUnit.DAYS;
                groupSize = 7;
                typeDesc = 3;
            }
            break;
        }
        switch (detailsType) {
            case GRAPH_STEPS:
                dataType = DataType.TYPE_STEP_COUNT_DELTA;
                aggregateDataType = DataType.AGGREGATE_STEP_COUNT_DELTA;
                field = Field.FIELD_STEPS;
                detailDesc = 1;
                break;
            case GRAPH_CALORIES:
                dataType = DataType.TYPE_CALORIES_EXPENDED;
                aggregateDataType = DataType.AGGREGATE_CALORIES_EXPENDED;
                field = Field.FIELD_CALORIES;
                detailDesc = 2;
                break;
            case GRAPH_DISTANCE:
                dataType = DataType.TYPE_DISTANCE_DELTA;
                aggregateDataType = DataType.AGGREGATE_DISTANCE_DELTA;
                field = Field.FIELD_DISTANCE;
                detailDesc = 3;
                break;
        }
        descriptionCode = 3 * (detailDesc - 1) + typeDesc;
        // Calling a method which retrieves the data, and thereby plots a graph
        readGraphHistory(startTime, endTime, timeUnit, dataType, aggregateDataType, field, calendarType, groupSize, descriptionCode);
    }

    // Input parameters : startTime, endTime - range within which the data is to be retrieved
    //                    timeUnit - specifies the unit of time (DAY, HOUR, etc) for querying the data
    //                    dataType - specifies the type of data (STEPS, CALORIES, DISTANCE, etc) to be retrieved
    //                    aggregateDataType - same as dataType, but specifies aggregate type (used for querying data)
    //                    field - type of data to be plotted (STEPS, CALORIES, DISTANCE)
    //                    calendarType - specifies the time type (DAY_OF_WEEK,HOUR_OF_DAY,etc)
    //                    groupSize - specifies the size of the bucket i.e data for a group of days or hours
    //                    descriptionCode - used to obtain the description of the bar in the graph
    // This method obtains the data (history) based on the input, and calls a plotGraph() method to plot the graph
    private void readGraphHistory(final long startTime, final long endTime, TimeUnit timeUnit,
                                  DataType dataType, final DataType aggregateDataType, final Field field,
                                  final int calendarType, final int groupSize, final int descriptionCode) {
        // Calling queryGraphData() which queries based on the input passed
        DataReadRequest readRequest = queryGraphData(startTime, endTime, timeUnit, dataType, aggregateDataType, groupSize);
        // Retrieving the history based on the result obtained from querying
        Fitness.getHistoryClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<Integer> YValues = new ArrayList<>();
                        List<String> XValues;
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        //java.text.DateFormat dateFormat = getDateInstance();
                        Log.i(TAG, "NOW No of Buckets = " + dataReadResponse.getBuckets().size());
                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                            DataSet dataSet = bucket.getDataSet(aggregateDataType);
                            if (!dataSet.isEmpty()) {
                                com.google.android.gms.fitness.data.DataPoint dataPoint = dataSet.getDataPoints().get(0);
                                Log.i(TAG, "NOW Bucket Start: " + formatter.format(dataSet.getDataPoints().get(0).getStartTime(TimeUnit.MILLISECONDS)));
                                Log.i(TAG, "NOW Bucket End: " + formatter.format(dataSet.getDataPoints().get(0).getEndTime(TimeUnit.MILLISECONDS)));
                                if(field.equals(Field.FIELD_STEPS))
                                    YValues.add(dataPoint.getValue(field).asInt());
                                else
                                    YValues.add(Math.round(dataPoint.getValue(field).asFloat()));

                            } else {
                                Log.i(TAG, "NOW " + getResources().getStringArray(R.array.graph_descriptions)[descriptionCode - 1] + " = 0");
                                YValues.add(0);
                            }
                        }
                        // Calling a method which returns the list of labels on X-Axis
                        XValues = getXValues(calendarType, startTime);
                        // Calling a method which plots the graph
                        plotGraph(XValues, YValues, descriptionCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "There was a problem reading the data.", e);
                    }
                });
    }

    // Input parameters : startTime,endTime - range within which the data is to be retrieved
    //                    timeUnit - specifies the unit of time (DAY, HOUR, etc) for querying the data
    //                    dataType - specifies the type of data (STEPS, CALORIES, DISTANCE, etc) to be retrieved
    //                    aggregateDataType - same as dataType, but specifies aggregate type (used for querying data)
    //                    groupSize - specifies the size of the bucket i.e data for a group of days or hours
    // This method returns the query result based on the input parameters
    public static DataReadRequest queryGraphData(long startTime, long endTime, TimeUnit timeUnit,
                                                 DataType dataType, DataType aggregateDataType, int groupSize) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "NOW Range Start: " + formatter.format(startTime));
        Log.i(TAG, "NOW Range End: " + formatter.format(endTime));
        return new DataReadRequest.Builder()
                .aggregate(dataType, aggregateDataType)
                .bucketByTime(groupSize, timeUnit)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
    }

    // Input parameters : startTime - start time of the time period for which the graph is being displayed
    //                    calendarType - specifies the time type (DAY_OF_WEEK,HOUR_OF_DAY,etc)
    // Method returns the list of labels to be displayed on X-Axis of the graph
    private List<String> getXValues(int calendarType, long startTime) {
        List<String> XValues = new ArrayList<>();
        switch (calendarType) {
            case Calendar.HOUR_OF_DAY:
                XValues = getXValuesDaily();
                break;
            case Calendar.DAY_OF_WEEK:
                XValues = getXValuesWeekly(startTime);
                break;
            case Calendar.WEEK_OF_MONTH:
                XValues = getXValuesMonthly(startTime);
                break;
        }
        return XValues;
    }

    // Method returns the X-Axis labels (time frames in a day) for the DAILY graph plotted
    private List<String> getXValuesDaily() {
        List<String> XValues = new ArrayList<>();
        XValues.add("0-4am");
        XValues.add("4-8am");
        XValues.add("8-12pm");
        XValues.add("12-4pm");
        XValues.add("4-8pm");
        XValues.add("8-0am");
        return XValues;
    }

    // Method returns the X-Axis labels (day of the week) for the WEEKLY graph plotted
    private List<String> getXValuesWeekly(long startTime) {
        List<String> XValues = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEE"); // the day of the week abbreviated
        for (int i = 0; i < 7; i++) {
            calendar.setTimeInMillis(startTime);
            XValues.add(simpleDateformat.format(new Date(startTime)));
            calendar.add(Calendar.DAY_OF_WEEK, +1);
            startTime = calendar.getTimeInMillis();
        }
        return XValues;
    }

    // Method returns the X-Axis labels (time frames in a month) for the MONTHLY graph plotted
    private List<String> getXValuesMonthly(long startTime) {
        List<String> XValues = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM/dd"); // the day of the week abbreviated
        for (int i = 0; i < 4; i++) {
            calendar.setTimeInMillis(startTime);
            String xValues = simpleDateformat.format(new Date(startTime)) + "-\n";
            calendar.add(Calendar.DAY_OF_WEEK, +6);
            xValues += simpleDateformat.format(new Date(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_WEEK, +1);
            startTime = calendar.getTimeInMillis();
            XValues.add(xValues);
        }
        return XValues;
    }

    // Input parameters : xValues - strings to be displayed on X-Axis of the graph
    //                    yValues - values to be displayed on Y-Axis of the graph
    //                    descriptionCode - specifies what each bar in the graph represents
    // This method plots the bars with the history data on Y-Axis and the time period on X-Axis
    private void plotGraph(final List<String> xValues, List<Integer> yValues, int descriptionCode) {
        // Adding data points
        ArrayList<BarEntry> yData = new ArrayList<>();
        for (int i = 0; i < yValues.size(); i++) {
            yData.add(new BarEntry(i, yValues.get(i)));
        }

        // Setting label  on X-Axis
        final XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((int) value < xValues.size())
                    return xValues.get((int) value);
                else
                    return null;
            }
        });
        xAxis.setTextColor(Color.WHITE); // Sets label text colors to white
        xAxis.setGranularity(1f);        // Avoids duplicate labels when zooming

        // Hiding right Y-Axis
        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);

        // Setting minimum step count and text color for labels
        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setTextColor(Color.WHITE);
        leftYAxis.setAxisMinimum(0);

        // Setting bar features
        BarDataSet barDataSet = new BarDataSet(yData, getResources().getStringArray(R.array.graph_descriptions)[descriptionCode - 1]);
        barDataSet.setColors(getResources().getColor(R.color.colorAccent)); // Sets default colour of bars to colorAccent
        barDataSet.setHighLightColor(Color.WHITE); // Sets the bar color on selection to white
        barDataSet.setValueFormatter(new HomeFragment.IntValueFormatter()); // Sets bar labels to be integers only
        //barDataSet.setHighlightEnabled(false); // Uncomment to disable highlighting bar selection

        // Creating a BarData
        BarData barData = new BarData(barDataSet);
        barData.setValueTextColor(Color.WHITE); // Sets text color of bar labels to white
        barData.setValueTextSize(12f); // Sets text font of bar labels to 12

        // Setting bar chart
        barChart.setData(barData);
        barChart.invalidate();
        barChart.getDescription().setText(""); // Sets description of bars to ""
        barChart.setTouchEnabled(true); // Enables the users to touch the graph
        barChart.setFitBars(true);

        // Splits the labels to two lines for lengthy labels like for MONTHLY graphs
        if (descriptionCode == 3 || descriptionCode == 6 || descriptionCode == 9)
            barChart.setXAxisRenderer(new HomeFragment.CustomXAxisRenderer(barChart.getViewPortHandler(), xAxis, barChart.getTransformer(YAxis.AxisDependency.LEFT)));
    }

    // Class to format the bar labels so that the labels will only be integers
    public class IntValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) value);
        }
    }

    // Class to handle lengthy labels
    // Assuming that the label contains '\n' where a split should occur
    public class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            for (int i = 0; i < line.length; i++) {
                float vOffset = i * mAxisLabelPaint.getTextSize();
                Utils.drawXAxisValue(c, line[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees);
            }
        }
    }


    ////////////////////////////////////Everything Fitbit down

    @Override
    public int getTitleResourceId() {
        return R.string.activity_info;
    }

    @Override
    protected int getLoaderId() {
        return 3;
    }

    @Override
    public Loader<ResourceLoaderResult<DailyActivitySummary>> onCreateLoader(int id, Bundle args) {
        return ActivityService.getDailyActivitySummaryLoader(getActivity(), new Date());
    }

    @Override
    public void onLoadFinished(Loader<ResourceLoaderResult<DailyActivitySummary>> loader, ResourceLoaderResult<DailyActivitySummary> data) {
        super.onLoadFinished(loader, data);
        if (data.isSuccessful()) {
            bindActivityData(data.getResult());
        }
    }

    public void bindActivityData(DailyActivitySummary dailyActivitySummary) {
        StringBuilder stringBuilder = new StringBuilder();

        Summary summary = dailyActivitySummary.getSummary();
        Goals goals = dailyActivitySummary.getGoals();

        stringBuilder.append("<b>TODAY</b> ");
        stringBuilder.append("<br />");
        printKeys(stringBuilder, summary);

        stringBuilder.append("<br /><br />");
        stringBuilder.append("<b>GOALS</b> ");
        stringBuilder.append("<br />");
        printKeys(stringBuilder, goals);

//        setMainText(stringBuilder.toString());
//
//        Toast.makeText(getActivity(), ""+summary.getCaloriesOut(), Toast.LENGTH_LONG).show();
//        updateTotalSteps(summary.getSteps());
//        updateTotalCalories("" + summary.getCaloriesOut());
//        updateTotalDistance(summary.getDistances());
    }


}
