package com.androiders.walknearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androiders.walknearn.dbhelper.SharedPrefs;
import com.androiders.walknearn.model.UserLocalStore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

public class FitnessTrackerSelectionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_tracker_selection);
        if (new SharedPrefs(this).getIsFitnessTrackerGiven()) {
            startMainActivity();
        }
        setupToolbar();

        findViewById(R.id.linearGoogleFit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeGoogleFit();
            }
        });
    }

    private void setupToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void initializeGoogleFit() {

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
                        //      .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                        //     .addDataType(DataType.TYPE_DISTANCE_DELTA)
                        .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {

            startMainActivity();
        }
    }

    private void startMainActivity() {

        new SharedPrefs(this).setIsFitnessTrackerGiven(true);
        Intent i = new Intent(FitnessTrackerSelectionActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {

                Toast.makeText(this, "Subscribed to Google Fit", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }
        } else {

            Toast.makeText(this, "Google fit authentication failed", Toast.LENGTH_SHORT).show();
        }
    }
}
