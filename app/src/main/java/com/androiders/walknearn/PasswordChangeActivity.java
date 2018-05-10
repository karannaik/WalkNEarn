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
import android.view.inputmethod.EditorInfo;
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

// Password change screen allows the user to update the password
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

        InitializeViews(); // Initializing the views in the activity

        setupToolbar(); // Generalizing the action bars

        // Retrieving the data from the local database
        userLocalStore = new UserLocalStore(this);

        // Specifies how to handle the keyword done button rather than clicking "Change Password" button
        mOldPswrd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != EditorInfo.IME_ACTION_DONE)
                    return false;
                ChangePassword();
                return true;
            }
        });

        // Specifies how to handle the case when "Change Password" button is pressed
        mChngPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });

        // Specifies what is to be done when "Cancel" button is pressed
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Method generalizes the action bars
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Method initializes the views in the activity
    void InitializeViews() {
        mOldPswrd = findViewById(R.id.old_password);
        mNewPswrd = findViewById(R.id.new_password);
        mChngPswrd = findViewById(R.id.change_password_button);
        mCancel = findViewById(R.id.chng_pswrd_cancel_button);
        mCoordinatelayout = findViewById(R.id.ChngPswrdCoordinatorLayout);
    }

    // Specifies what is to be done on selecting an item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // Method helps in updating the password
    void ChangePassword() {
        mNewPswrd.setError(null);
        mOldPswrd.setError(null);

        // Store values at the time of the login attempt.
        final String newPassword = mNewPswrd.getText().toString();
        String oldPassword = mOldPswrd.getText().toString();

        boolean cancel = false;

        // Checking for the password validations and if any field is empty
        if (!util.isPasswordValid(newPassword, mNewPswrd))
            cancel = true;
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if (util.isFieldEmpty(oldPassword,mOldPswrd)) {
            if(!cancel)
                cancel = true;
        }

        // Checking if the new password is the same as the old one
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
        else // Continuing with the changing of password
        {
            if (!cancel) {

                final User user = userLocalStore.getLoggedInUser();
                final String email = user.getEmail();
                // Checking for the credentials of the user
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("PasswordCredentials", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean exists = jsonResponse.getBoolean("exists");
                            if (exists) // True specifies that the user credentials are correct
                                ChangePasswordUtil(email, newPassword);
                            else // Handling the situation where user credentials are incorrect
                            {
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
                // Checking for user credentials on server side
                CheckCredentials credentials = new CheckCredentials(email, oldPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PasswordChangeActivity.this);
                queue.add(credentials);

            }
        }
    }

    // Utility function to help in updating password
    private void ChangePasswordUtil(String email, final String password){
        // Updating the password
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("passwordChange", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) // True specifies that the update was successful
                    {
                        // Updating the password in local database
                        userLocalStore.updateUserPassword(password);
                        util.showProgressDialog("Updating Password", PasswordChangeActivity.this);
                        finish();
                    }
                    else // Handling the situation where the password updating was unsuccessful
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordChangeActivity.this,R.style.AlertDialogTheme);
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
        // Updating the password on server side
        PasswordRequest passwordRequest = new PasswordRequest(email,password,responseListener);
        RequestQueue queue = Volley.newRequestQueue(PasswordChangeActivity.this);
        queue.add(passwordRequest);
    }

    // Method checks if the internet connection is available
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
