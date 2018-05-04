package com.androiders.walknearn;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.dbfiles.CheckCredentials;
import com.androiders.walknearn.dbfiles.PasswordRequest;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordChangeActivity extends AppCompatActivity {

    EditText mOldPswrd, mNewPswrd;
    Button mChngPswrd, mCancel;
    CoordinatorLayout mCoordinatelayout;
    Utility util = new Utility(PasswordChangeActivity.this);
    UserLocalStore userLocalStore;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        InitializeViews();

        setupToolbar();

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
                finish();
            }
        });
    }


    private void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    void InitializeViews() {
        mOldPswrd = findViewById(R.id.old_password);
        mNewPswrd = findViewById(R.id.new_password);
        mChngPswrd = findViewById(R.id.change_password_button);
        mCancel = findViewById(R.id.chng_pswrd_cancel_button);
        mCoordinatelayout = findViewById(R.id.ChngPswrdCoordinatorLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    void ChangePassword() {
        mNewPswrd.setError(null);
        mOldPswrd.setError(null);

        // Store values at the time of the login attempt.
        final String newPassword = mNewPswrd.getText().toString();
        String oldPassword = mOldPswrd.getText().toString();

        boolean cancel = false;

        if (!util.isPasswordValid(newPassword, mNewPswrd))
            cancel = true;
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if (util.isFieldEmpty(oldPassword,mOldPswrd)) {
            if(!cancel)
                cancel = true;
        }
        if(newPassword.equals(oldPassword)){
            AlertDialog.Builder passwordMatch = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            passwordMatch.setMessage("New password cannot be the same as old one")
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mNewPswrd.requestFocus();
                    }
                })
                .setTitle("Warning")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();
        }
        else {
            if (!cancel) {

                final User user = userLocalStore.getLoggedInUser();
                final String email = user.getEmail();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("PasswordCredentials", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean exists = jsonResponse.getBoolean("exists");
                            if (exists)
                                ChangePasswordUtil(email, newPassword);
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordChangeActivity.this, R.style.AlertDialogTheme);
                                builder.setMessage("Incorrect Credentials")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                CheckCredentials credentials = new CheckCredentials(email, oldPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PasswordChangeActivity.this);
                queue.add(credentials);

            }
        }
    }

    private void ChangePasswordUtil(String email, final String password){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("passwordChange", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        userLocalStore.updateUserPassword(password);
                        util.showProgressDialog("Updating Password", PasswordChangeActivity.this);
                        startActivity(new Intent(PasswordChangeActivity.this, SettingsActivity.class));
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordChangeActivity.this);
                        builder.setMessage("Updating Password failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PasswordRequest passwordRequest = new PasswordRequest(email,password,responseListener);
        RequestQueue queue = Volley.newRequestQueue(PasswordChangeActivity.this);
        queue.add(passwordRequest);
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
