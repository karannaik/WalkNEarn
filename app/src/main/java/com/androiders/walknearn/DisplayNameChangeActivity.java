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
import com.androiders.walknearn.dbfiles.DisplayNameRequest;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

// Display name screen which allows the user to update the display name
public class DisplayNameChangeActivity extends AppCompatActivity {

    EditText mNewName,mPassword;
    Button mChngName,mCancel;
    CoordinatorLayout mCoordinateLayout;
    Utility util = new Utility(DisplayNameChangeActivity.this);
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_name_change);

        InitializeViews(); // Method call to initialize all views in the activity
        setupToolbar(); // Call to generalize the action bars

        // Stores the user data on phone
        userLocalStore = new UserLocalStore(this);

        // Specifies what is to be done on clicking done on keyboard rather than clicking "Change Name" button"
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i != EditorInfo.IME_ACTION_DONE)
                    return false;
                ChangeName();
                return true;
            }
        });

        // Specifies what is to be done on clicking "Change Name" button
        mChngName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeName();
            }
        });

        // Specifies what is to be done in clicking "Cancel" button
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Method initializes the views present in the activitys
    void  InitializeViews(){
        mNewName = findViewById(R.id.displayName);
        mPassword = findViewById(R.id.password);
        mChngName = findViewById(R.id.change_name_button);
        mCancel = findViewById(R.id.chng_name_cancel_button);
        mCoordinateLayout = findViewById(R.id.ChngNameCoordinatorLayout);
    }

    // Method generalizes the action bars
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Method specifies what is to be done on clicking the item from a list
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // Method helps in changing the display name
    void ChangeName() {

        mNewName.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        final String newName = mNewName.getText().toString();
        final String password = mPassword.getText().toString();

        boolean cancel = false;

        // Checking if fields are empty
        if (util.isFieldEmpty(newName, mNewName))
            cancel = true;
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if (util.isFieldEmpty(password, mPassword)) {
            if (!cancel)
                cancel = true;
        }

        // Getting the details of the user who has already logged in
        final User user = userLocalStore.getLoggedInUser();
        final String email = user.getEmail();
        String oldName = user.getUsername();
        // Checking if the new name is the same as the old name
        // If so alert box appears asking to change the name
        if (oldName.equals(newName)) {
            AlertDialog.Builder nameMatch = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            nameMatch.setMessage("New name cannot be the same as old one")
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mNewName.requestFocus();
                    }
                })
                .setTitle("Warning")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();
        }
        // Specifies how to update the name both on server and on local database
        else
        {
            if (!cancel) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("NameCredentials", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean exists = jsonResponse.getBoolean("exists");
                            if (exists) // True if user exists with the provided details
                                ChangeNameUtil(email, password); // Calling the utility function to update the name
                            else // Handling the case where user credentials are incorrect
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayNameChangeActivity.this, R.style.AlertDialogTheme);
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
                // Making a server side request to check if the user credentials are correct
                CheckCredentials credentials = new CheckCredentials(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(DisplayNameChangeActivity.this);
                queue.add(credentials);
            }
        }
    }

    // Utility function to support the updating of display name
    private void ChangeNameUtil(String email, final String name){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("NameChange", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) // True if the update was done successfully
                    {
                        // Updating the name in local database
                        userLocalStore.updateDisplayName(name);
                        util.showProgressDialog("Updating display name", DisplayNameChangeActivity.this);
                        finish();
                    }
                    else // Handling the unsuccessful attempt to update the name
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayNameChangeActivity.this,R.style.AlertDialogTheme);
                        builder.setMessage("Updating display name failed")
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
        // Making a server side request to update the display name
        DisplayNameRequest displayNameRequest = new DisplayNameRequest(email,name,responseListener);
        RequestQueue queue = Volley.newRequestQueue(DisplayNameChangeActivity.this);
        queue.add(displayNameRequest);
    }

    // Method check if the internet is available or not
    private void showInternetSnackBar() {
        Snackbar snackbar = Snackbar
                .make(mCoordinateLayout,getString(R.string.internet_unavailable), Snackbar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        snackbar.show();
    }

}
