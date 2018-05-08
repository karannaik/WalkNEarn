package com.androiders.walknearn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androiders.walknearn.fragment.TotalCaloriesFragment;
import com.androiders.walknearn.fragment.TotalDistanceFragment;
import com.androiders.walknearn.fragment.TotalStepsFragment;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private ViewPager mViewPager;
    private TotalCaloriesFragment mFragmentTotalCalories;
    private TotalStepsFragment mFragmentTotalSteps;
    private TotalDistanceFragment mFragmentTotalDistance;
    private PageIndicatorView pageIndicatorView;
    private TextView mWalkCoins;
    private ImageView mRefreshButton;
    private int timeSpinnerPos = 1;
    private int detailsSpinnerPos = 1;

    private static final int GRAPH_DAILY = 1;
    private static final int GRAPH_WEEKLY = 2;
    private static final int GRAPH_MONTHLY = 3;

    private static final int GRAPH_STEPS = 1;
    private static final int GRAPH_CALORIES = 2;
    private static final int GRAPH_DISTANCE = 3;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        subscribe();

        setSpinnerForGraph();
    }

    private void setSpinnerForGraph() {
        Spinner timeSpinner = findViewById(R.id.time_spinner);
        Spinner detailsSpinner = findViewById(R.id.details_spinner);

        // Spinner click listener
        timeSpinner.setOnItemSelectedListener(this);
        detailsSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> timeCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.time_spinner)));
        List<String> detailsCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.details_spinner)));

        // Creating adapter for spinners
        ArrayAdapter<String> timeDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeCategories);
        ArrayAdapter<String> detailsDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, detailsCategories);

        // Drop down layout style - list view with radio Button
        timeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        detailsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        timeSpinner.setAdapter(timeDataAdapter);
        detailsSpinner.setAdapter(detailsDataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        String ItemSelected = parent.getSelectedItem().toString();
        if(ItemSelected.compareTo("Daily") == 0)
            timeSpinnerPos = 1;
        else if(ItemSelected.compareTo("Weekly") == 0)
            timeSpinnerPos = 2;
        else if(ItemSelected.compareTo("Monthly") == 0)
            timeSpinnerPos = 3;
        else if(ItemSelected.compareTo("Steps") == 0)
            detailsSpinnerPos = 1;
        else if(ItemSelected.compareTo("Calories") == 0)
            detailsSpinnerPos = 2;
        else if(ItemSelected.compareTo("Distance") == 0)
            detailsSpinnerPos = 3;
        initializeGraph(timeSpinnerPos,detailsSpinnerPos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void setAutomaticRefreshTimers() {
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(4000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {

                                readData();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initializeViews() {

        barChart = findViewById(R.id.bar_chart);
        mWalkCoins = findViewById(R.id.WalkCOins);

        mRefreshButton = findViewById(R.id.graph_refresh_btn);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeGraph(timeSpinnerPos,detailsSpinnerPos);
            }
        });

        /*findViewById(R.id.btnFabCoupons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CouponTypeActivity.class));
            }
        });*/

        findViewById(R.id.ProfilePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });

        mViewPager = findViewById(R.id.viewPager);
        setupViewPager(mViewPager);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(3); // specify total count of indicators
        pageIndicatorView.setAnimationType(AnimationType.WORM);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });

        mViewPager.setOffscreenPageLimit(3);

        final User user = new UserLocalStore(this).getLoggedInUser();
        if (user.getPhotoUrl()!=null && !user.getPhotoUrl().isEmpty()) {
            //load profile pic

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Bitmap bmp = null;
                            try {
                                URL url = new URL(user.getPhotoUrl());
                                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                ((de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.ProfilePic)).setImageBitmap(bmp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    private void initializeGraph(int timeType,int detailsType)
    {
        long startTime = 0,endTime = 0;
        TimeUnit timeUnit = TimeUnit.DAYS;
        DataType dataType = DataType.TYPE_STEP_COUNT_DELTA,aggregateDataType = DataType.AGGREGATE_STEP_COUNT_DELTA;
        Field field = Field.FIELD_STEPS;
        int calendarType = Calendar.DAY_OF_WEEK, groupSize = 1, descriptionCode, typeDesc = 1, detailDesc = 1;
        switch(timeType)
        {
            case GRAPH_DAILY:
            {
                calendarType = Calendar.HOUR_OF_DAY;
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                if((calendar.get(Calendar.HOUR) > 0 ) || (calendar.get(Calendar.HOUR) == 0 && (calendar.get(Calendar.MINUTE) > 0 || calendar.get(Calendar.SECOND) > 0)))
                {
                    //calendar.add(Calendar.DATE,+1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.HOUR, 1);
                }
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.HOUR_OF_DAY,-24); // Past 24 hours
                startTime = calendar.getTimeInMillis();
                timeUnit = TimeUnit.HOURS;
                groupSize = 4;
                typeDesc = 1;
            }
            break;
            case GRAPH_WEEKLY:
            {
                calendarType = Calendar.DAY_OF_WEEK;
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                if((calendar.get(Calendar.HOUR) > 0 ) || (calendar.get(Calendar.HOUR) == 0 && (calendar.get(Calendar.MINUTE) > 0 || calendar.get(Calendar.SECOND) > 0)))
                {
                    calendar.add(Calendar.DATE,+1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.HOUR, 0);
                }
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_WEEK,-7); // Past 7 days
                startTime = calendar.getTimeInMillis();
                timeUnit = TimeUnit.DAYS;
                typeDesc = 2;
            }
            break;
            case GRAPH_MONTHLY:
            {
                calendarType = Calendar.WEEK_OF_MONTH;
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
                if((day_of_week < 7) || (day_of_week == 7 && ((calendar.get(Calendar.HOUR) > 0) ||
                            (calendar.get(Calendar.HOUR) == 0 && (calendar.get(Calendar.MINUTE) > 0 || calendar.get(Calendar.SECOND) > 0)))))
                {
                    calendar.add(Calendar.DATE,+7-day_of_week+1);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.HOUR, 0);
                }
                DateFormat format = getDateInstance();
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.WEEK_OF_MONTH, -4); // Past 4 weeks
                startTime = calendar.getTimeInMillis();
                timeUnit = TimeUnit.DAYS;
                groupSize = 7;
                typeDesc = 3;
            }
            break;
        }
        switch(detailsType)
        {
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
        descriptionCode = 3*(detailDesc-1)+typeDesc;
        readGraphHistory(startTime,endTime,timeUnit,dataType,aggregateDataType,field,calendarType,groupSize,descriptionCode);
    }

    private Task<DataReadResponse> readGraphHistory(final long startTime, final long endTime, TimeUnit timeUnit,
                                                    DataType dataType, final DataType aggregateDataType, final Field field,
                                                    final int calendarType, final int groupSize, final int descriptionCode)
    {
        DataReadRequest readRequest = queryGraphData(startTime,endTime,timeUnit,dataType,aggregateDataType,groupSize);
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
            .readData(readRequest)
            .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    List<Integer> YValues = new ArrayList<>();
                    List<String> XValues;
                    Log.i(TAG, "NOW No of Buckets = " + dataReadResponse.getBuckets().size());
                    for (Bucket bucket : dataReadResponse.getBuckets())
                    {
                        DataSet dataSet = bucket.getDataSet(aggregateDataType);
                        if (!dataSet.isEmpty())
                        {
                            com.google.android.gms.fitness.data.DataPoint dataPoint = dataSet.getDataPoints().get(0);
                            Log.i(TAG, "NOW "+getResources().getStringArray(R.array.graph_descriptions)[descriptionCode-1]+" = " + dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
                            YValues.add(dataPoint.getValue(field).asInt());
                        }
                        else
                        {
                            Log.i(TAG,"NOW "+getResources().getStringArray(R.array.graph_descriptions)[descriptionCode-1]+" = 0");
                            YValues.add(0);
                        }
                    }
                    XValues = getXValues(calendarType,startTime);
                    plotGraph(XValues,YValues,descriptionCode);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "There was a problem reading the data.", e);
                }
            });
    }

    private List<String> getXValues(int calendarType, long startTime)
    {
        List<String> XValues = new ArrayList<>();
        switch (calendarType)
        {
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

    public class CustomXAxisRenderer extends XAxisRenderer
    {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans)
        {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees)
        {
            String line[] = formattedLabel.split("\n");
            for (int i = 0; i < line.length; i++) {
                float vOffset = i * mAxisLabelPaint.getTextSize();
                Utils.drawXAxisValue(c, line[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees);
            }
        }
    }

    private void plotGraph(final List<String> xValues, List<Integer> yValues, int descriptionCode)
    {
        // Adding data points
        ArrayList<BarEntry> yData = new ArrayList<>();
        for(int i = 0; i < yValues.size(); i++){
            yData.add(new BarEntry(i,yValues.get(i)));
        }

        // Setting label  on X-Axis
        final XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if((int) value < xValues.size())
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
        leftYAxis.setAxisMinimum(0f);

        // Setting bar features
        BarDataSet barDataSet = new BarDataSet(yData,getResources().getStringArray(R.array.graph_descriptions)[descriptionCode-1]);
        barDataSet.setColors(getResources().getColor(R.color.colorAccent)); // Sets default colour of bars to grey
        barDataSet.setHighLightColor(Color.WHITE); // Sets the bar color on selection to white
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

        Log.i(TAG,"Description code = "+descriptionCode);

        // Splits the labels to two lines
        if(descriptionCode == 3 || descriptionCode == 6 || descriptionCode == 9)
            barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(),xAxis,barChart.getTransformer(YAxis.AxisDependency.LEFT)));
    }

    private List<String> getXValuesDaily()
    {
        List<String> XValues = new ArrayList<>();
        XValues.add("1-5am");
        XValues.add("5-9am");
        XValues.add("9-1pm");
        XValues.add("1-5pm");
        XValues.add("5-9pm");
        XValues.add("9-0am");
        return XValues;
    }

    private List<String> getXValuesWeekly(long startTime)
    {
        List<String> XValues = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEE"); // the day of the week abbreviated
        for(int i = 0; i < 7; i++)
        {
            calendar.setTimeInMillis(startTime);
            XValues.add(simpleDateformat.format(new Date(startTime)));
            calendar.add(Calendar.DAY_OF_WEEK,+1);
            startTime = calendar.getTimeInMillis();
        }
        return XValues;
    }

    private List<String> getXValuesMonthly(long startTime)
    {
        List<String> XValues = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM/dd"); // the day of the week abbreviated
        for(int i = 0; i < 4; i++)
        {
            calendar.setTimeInMillis(startTime);
            String xValues = simpleDateformat.format(new Date(startTime))+"-\n";
            calendar.add(Calendar.DAY_OF_WEEK,+6);
            xValues += simpleDateformat.format(new Date(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_WEEK,+1);
            startTime = calendar.getTimeInMillis();
            XValues.add(xValues);
        }
        return XValues;
    }

    public static DataReadRequest queryGraphData(long startTime,long endTime, TimeUnit timeUnit,
        DataType dataType,DataType aggregateDataType, int groupSize)
    {
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        mFragmentTotalSteps = new TotalStepsFragment();
        mFragmentTotalCalories = new TotalCaloriesFragment();
        mFragmentTotalDistance = new TotalDistanceFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mFragmentTotalSteps, "Total Steps");
        adapter.addFragment(mFragmentTotalCalories, "Total Calories");
        adapter.addFragment(mFragmentTotalDistance, "Total Distance");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");

                                    //setAutomaticRefreshTimers();
                                    readData();
                                    readHistoryData();
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.i(TAG, "Total steps: " + total);
                                //call fragment methods
                                mFragmentTotalSteps.updateText(total);
                                mWalkCoins.setText(Double.toString(total/2000.0));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                if(dataSet.getDataPoints().size()!=0) {

                                    String total = dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).toString();

                                    Log.i(TAG, "Total calories: " + total);
                                    //call fragment methods
                                    mFragmentTotalCalories.updateText(""+Math.round(Float.parseFloat(total)));
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });

//        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
//                .addOnSuccessListener(
//                        new OnSuccessListener<DataSet>() {
//                            @Override
//                            public void onSuccess(DataSet dataSet) {
//                                long total =
//                                        dataSet.isEmpty()
//                                                ? 0
//                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asInt();
//
//                                Log.i(TAG, "Total calories: " + total);
//                                //call fragment methods
//                                mFragmentTotalCalories.updateText(""+total);
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "There was a problem getting the step count.", e);
//                            }
//                        });

        //Get all summary
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the data.
     */
    private Task<DataReadResponse> readHistoryData(){
        // Begin by creating the query.
        DataReadRequest readRequest = queryFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }
    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would dump
     * all the data. In this sample, logging also prints to the device screen, so we can see what the
     * query returns, but your app should not log fitness information as a privacy consideration. A
     * better option would be to dump the data you receive to a local data directory to avoid exposing
     * it to other applications.
     */
    public static void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(
                    TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    /** Returns a {@link DataReadRequest} for all step count changes in the past week. */
    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]

        return readRequest;
    }
    // [START parse_dataset]
    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (com.google.android.gms.fitness.data.DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
        }Log.i(TAG, "DONE");
    }
    // [END parse_dataset]

}