package com.androiders.walknearn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androiders.walknearn.dbhelper.SharedPrefs;
import com.androiders.walknearn.util.Utility;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button mFacebookLoginButton;
    private EditText mEdtEmailId;
    private EditText mEdtPassword;
    private TextInputLayout mTxtInputLayoutEmail;
    private TextInputLayout mTxtInputLayoutPassword;
    private LinearLayout mLinearEmailId;
    private LinearLayout mLinearPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private CallbackManager mCallBackManager;
    TextView mInfo;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        initializeViews();
        mFacebookLoginButton = findViewById(R.id.facebook_login_button);
        Toast.makeText(LoginActivity.this, "Welcome in WalkNEarn app", Toast.LENGTH_SHORT).show();
        mInfo = (TextView) findViewById(R.id.info);

        mCallBackManager = CallbackManager.Factory.create();

        mFacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().registerCallback(mCallBackManager,
                        new FacebookCallback<LoginResult>() {
                            public void onSuccess(LoginResult loginResult) {

                                Toast.makeText(LoginActivity.this, "Successful facebook login", Toast.LENGTH_SHORT).show();
                                /*mInfo.setText(
                                        "User ID: "
                                                + loginResult.getAccessToken().getUserId()
                                                + "\n" +
                                                "Auth Token: "
                                                + loginResult.getAccessToken().getToken()
                                );*/
                            }


                            @Override
                            public void onCancel() {

                               // mInfo.setText("Login attempt canceled.");
                            }

                            @Override
                            public void onError(FacebookException e) {


//                                mInfo.setText("Login attempt failed.");

                            }
                        });
            }
        });


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

        if (mLinearPassword!=null && mLinearPassword.getVisibility() == View.VISIBLE) {
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }


}
