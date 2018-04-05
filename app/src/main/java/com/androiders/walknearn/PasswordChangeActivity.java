package com.androiders.walknearn;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordChangeActivity extends AppCompatActivity {

    EditText mOldPswrd, mNewPswrd;
    Button mChngPswrd, mCancel;
    CoordinatorLayout mCoordinatelayout;
    Utility util = new Utility(PasswordChangeActivity.this);
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        InitializeViews();
        userLocalStore = new UserLocalStore(this);
        mOldPswrd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                ChangePassword();
                return true;
            }
        });

        mChngPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordChangeActivity.this, SettingsActivity.class));
            }
        });
    }

    void InitializeViews() {
        mOldPswrd = findViewById(R.id.old_password);
        mNewPswrd = findViewById(R.id.new_password);
        mChngPswrd = findViewById(R.id.change_password_button);
        mCancel = findViewById(R.id.chng_pswrd_cancel_button);
        mCoordinatelayout = findViewById(R.id.ChngPswrdCoordinatorLayout);
    }

    void ChangePassword() {
        mNewPswrd.setError(null);
        mOldPswrd.setError(null);

        // Store values at the time of the login attempt.
        final String newPassword = mNewPswrd.getText().toString();
        String oldPassword = mOldPswrd.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (!util.isPasswordValid(newPassword, mNewPswrd)) {
            focusView = mNewPswrd;
            cancel = true;
        }
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if (util.isFieldEmpty(oldPassword,mOldPswrd)) {
            if(!cancel){
                focusView = mOldPswrd;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {

            final User user = userLocalStore.getLoggedInUser();
            String email = user.Email;

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("convertstr", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) {
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                userLocalStore.updateUserPassword(newPassword);
                                util.showProgressDialog("Updating Password", PasswordChangeActivity.this);
                                startActivity(new Intent(PasswordChangeActivity.this, SettingsActivity.class));
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordChangeActivity.this);
                                builder.setMessage("Updating Password failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PasswordChangeActivity.this);
                            builder.setMessage("Incorrect Password")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            PasswordRequest passwordRequest = new PasswordRequest(email,oldPassword,newPassword,responseListener);
            RequestQueue queue = Volley.newRequestQueue(PasswordChangeActivity.this);
            queue.add(passwordRequest);


            /*Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("success",response);
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            String name = jsonResponse.getString("userName");
                            String stepCnt = jsonResponse.getString("userStepCount");
                            User user = new User(name,email,password,Integer.parseInt(stepCnt));
                            userLocalStore.storeUserData(user);
                            userLocalStore.setUserLggedIn(true);
                            util.showProgressDialog("Logging in",LoginActivity.this);
                            Intent MainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(MainActivityIntent);
                        }
                        else{AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Invalid Email or Password")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            LoginRequest loginRequest = new LoginRequest(email,password,responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }*/
        }
    }

    private void showInternetSnackBar() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatelayout,getString(R.string.internet_unavailable), Snackbar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        snackbar.show();
    }

}
