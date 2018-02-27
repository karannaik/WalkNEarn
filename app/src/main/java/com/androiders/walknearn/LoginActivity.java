package com.androiders.walknearn;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androiders.walknearn.dbhelper.SharedPrefs;
import com.androiders.walknearn.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";

    private EditText mEdtEmailId;
    private EditText mEdtPassword;
    private TextInputLayout mTxtInputLayoutEmail;
    private TextInputLayout mTxtInputLayoutPassword;
    private LinearLayout mLinearEmailId;
    private LinearLayout mLinearPassword;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mCoordinatorLayout;

    private static final String [] LOCATION_PERMS =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
    private static final int INITIAL_REQUEST=1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (new SharedPrefs(this).getSyncTime() == null) {

            int currentapiVersion = Build.VERSION.SDK_INT;
            if (currentapiVersion >= 23){//is marshmallow

                if (!canAccessLocation()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(LOCATION_PERMS, INITIAL_REQUEST);
                    }
                } else {
                    initializeViews();
                }
            } else{

                initializeViews();
            }

        } else {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void showInternetSnackBar() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, "Internet not available", Snackbar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });

        snackbar.show();
    }

    private void initializeViews() {

        mEdtEmailId = (EditText) findViewById(R.id.edt_email);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mTxtInputLayoutEmail = (TextInputLayout) findViewById(R.id.txtInputLayoutEmail);
        mTxtInputLayoutPassword = (TextInputLayout) findViewById(R.id.txtInputLayoutPassword);
        mLinearEmailId = (LinearLayout) findViewById(R.id.linear_emailId);
        mLinearPassword = (LinearLayout) findViewById(R.id.linear_password);
        ImageView mImgBack = (ImageView) findViewById(R.id.img_back);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        findViewById(R.id.cardViewNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateEmailId(mEdtEmailId.getText().toString())) {

                    if (new Utility(LoginActivity.this).isConnectingToInternet()) {

                        mLinearEmailId.setVisibility(View.GONE);
                        mLinearPassword.setVisibility(View.VISIBLE);
                    } else {
                        showInternetSnackBar();
                    }
                }

                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        findViewById(R.id.cardViewLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mEdtPassword.getText().toString().isEmpty()) {

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    View view = LoginActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    new SharedPrefs(LoginActivity.this).setSyncTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));

                } else {

                    mTxtInputLayoutPassword.setError("Please enter password");
                }

            }
        });

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mEdtEmailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mTxtInputLayoutEmail.setError(null);
            }
        });

        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mTxtInputLayoutPassword.setError(null);
            }
        });

    }

    private boolean validateEmailId(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTxtInputLayoutEmail.setError("enter a valid email address");
            return false;
        } else {
            mTxtInputLayoutEmail.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (mLinearPassword.getVisibility() == View.VISIBLE) {
            mLinearEmailId.setVisibility(View.VISIBLE);
            mLinearPassword.setVisibility(View.GONE);
            View view = LoginActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            super.onBackPressed();
        }
    }


    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (!canAccessLocation()) {
            Toast.makeText(this, "Please enable locations to use this app", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMS, INITIAL_REQUEST);
            }
        } else {
            initializeViews();
        }
    }

}
