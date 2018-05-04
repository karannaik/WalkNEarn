package com.androiders.walknearn;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androiders.walknearn.dbhelper.SharedPrefs;

public class AskingPermissionsActivity extends AppCompatActivity {

    private static final String[] LOCATION_PERMS =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    private static final int INITIAL_REQUEST = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_locaion);


        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.allowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(LOCATION_PERMS, INITIAL_REQUEST);
                }
            }
        });

        if (!new SharedPrefs(this).getIsLocationGiven()) {

            int currentapiVersion = Build.VERSION.SDK_INT;
            if (currentapiVersion >= 23) {//is marshmallow

                if (canAccessLocation()) {
                    startNextActivity();
                }
            } else {

                startNextActivity();
            }

        } else {

            startNextActivity();
        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (!canAccessLocation()) {
            Toast.makeText(this, "Please enable locations to use this app", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMS, INITIAL_REQUEST);
            }
        } else {
            startNextActivity();
        }
    }

    private void startNextActivity() {
        new SharedPrefs(this).setIsLocationGiven(true);
        Intent i = new Intent(getApplicationContext(), FitnessTrackerSelectionActivity.class);
        startActivity(i);
        finish();
    }

}
