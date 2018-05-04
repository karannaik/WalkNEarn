package com.androiders.walknearn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androiders.walknearn.fragment.TotalCaloriesFragment;
import com.androiders.walknearn.fragment.TotalDistanceFragment;
import com.androiders.walknearn.fragment.TotalStepsFragment;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    private static final int GRAPH_DAILY = 1;
    private static final int GRAPH_WEEKLY = 2;
    private static final int GRAPH_MONTHLY = 3;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        initializeGraph(GRAPH_DAILY);

        subscribe();

        setSpinnerForGraph();

    }

    private void setSpinnerForGraph() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Daily");
        categories.add("Weekly");
        categories.add("Monthly");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        initializeGraph(position+1);
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

        graph = (GraphView) findViewById(R.id.graph);
        mWalkCoins = findViewById(R.id.WalkCOins);

        findViewById(R.id.btnFabCoupons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CouponTypeActivity.class));
            }
        });

        findViewById(R.id.ProfilePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
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

    private void initializeGraph(int type) {

        graph.removeAllSeries();
        graph.clearSecondScale();
        LineGraphSeries<DataPoint> series = null;
        switch (type){
            case GRAPH_DAILY:

                series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 6)
                });
                graph.addSeries(series);
                break;
            case GRAPH_WEEKLY:
            {
                /*Calendar calendar = Calendar.getInstance();
                Date date = new Date(); // Current data
                calendar.setTime(date);
                long endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.WEEK_OF_YEAR,-1);
                long startTime = calendar.getTimeInMillis();
                java.text.DateFormat dateFormat = getDateInstance();
                Log.i(TAG,"RANGE START : "+dateFormat.format(startTime));
                Log.i(TAG,"RANGE END : "+dateFormat.format(endTime));
                DataReadRequest readRequest = new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA,DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .bucketByTime(1,TimeUnit.DAYS)
                        .setTimeRange(startTime,endTime,TimeUnit.MILLISECONDS)
                        .build();
                Task<DataReadResponse> responseTask = Fitness.getHistoryClient(this,GoogleSignIn.getLastSignedInAccount(this))
                    .readData(readRequest)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>()
                    {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {
                            Log.i(TAG,"Response size : "+dataReadResponse.getBuckets().size());
                            if (dataReadResponse.getBuckets().size() > 0)
                            {
                                Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResponse.getBuckets().size());
                                for (Bucket bucket : dataReadResponse.getBuckets()) {
                                    List<DataSet> dataSets = bucket.getDataSets();
                                    for (DataSet dataSet : dataSets) {
                                        dumpDataSet(dataSet);
                                    }
                                }
                            }
                            else if (dataReadResponse.getDataSets().size() > 0) {
                                Log.i(TAG, "Number of returned DataSets is: " + dataReadResponse.getDataSets().size());
                                for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                    dumpDataSet(dataSet);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "There was a problem reading the data.", e);
                        }
                    });*/
                //List<DataSet> dataSets = responseTask.getResult().getDataSets();
                //Log.i(TAG,"DataSet size : "+dataSets.size());
                series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 5),
                        new DataPoint(5, 1),
                        new DataPoint(6, 1),
                        new DataPoint(7, 4),
                        new DataPoint(8, 7),
                        new DataPoint(9, 3)
                });
                graph.addSeries(series);
            }
            break;
            case GRAPH_MONTHLY:

                series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0),
                        new DataPoint(0.5, 0),
                        new DataPoint(1, 1),
                        new DataPoint(1.5, 1),
                        new DataPoint(2, 2),
                        new DataPoint(2.5, 2),
                        new DataPoint(3, 3),
                        new DataPoint(3.5, 3),
                        new DataPoint(4, 4),
                        new DataPoint(4.5, 4),
                        new DataPoint(5, 5),
                        new DataPoint(5.5, 5),
                        new DataPoint(6, 1),
                        new DataPoint(7, 4),
                        new DataPoint(8, 7),
                        new DataPoint(9, 3),
                        new DataPoint(10, 2),
                        new DataPoint(11, 4),
                        new DataPoint(12, 3),
                        new DataPoint(13, 2),
                        new DataPoint(14, 2),
                        new DataPoint(15, 1),
                        new DataPoint(16, 5),
                        new DataPoint(17, 4),
                        new DataPoint(18, 7),
                        new DataPoint(19, 3)
                });
                graph.addSeries(series);
                break;
        }

        series.setDrawBackground(true);
        series.setBackgroundColor(getResources().getColor(R.color.transparent_accent));
        series.setTitle("Steps");
        series.setDrawDataPoints(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
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