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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

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

        InitializeViews();
        userLocalStore = new UserLocalStore(this);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                ChangeName();
                return true;
            }
        });

        mChngName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeName();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayNameChangeActivity.this,SettingsActivity.class));
            }
        });

    }

    void  InitializeViews(){
        mNewName = findViewById(R.id.displayName);
        mPassword = findViewById(R.id.password);
        mChngName = findViewById(R.id.change_name_button);
        mCancel = findViewById(R.id.chng_name_cancel_button);
        mCoordinateLayout = findViewById(R.id.ChngNameCoordinatorLayout);
    }

    void ChangeName(){

        mNewName.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        final String name = mNewName.getText().toString();
        String password = mPassword.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (util.isFieldEmpty(name,mNewName)) {
            focusView = mNewName;
            cancel = true;
        }
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if(util.isFieldEmpty(password,mPassword)){
            if(!cancel){
                focusView = mPassword;
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
                        Log.d("DisplayName", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) {
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                userLocalStore.updateDisplayName(name);
                                util.showProgressDialog("Updating display name", DisplayNameChangeActivity.this);
                                startActivity(new Intent(DisplayNameChangeActivity.this, SettingsActivity.class));
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayNameChangeActivity.this);
                                builder.setMessage("Updating display name failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DisplayNameChangeActivity.this);
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

            DisplayNameRequest displayNameRequest = new DisplayNameRequest(email,password,name,responseListener);
            RequestQueue queue = Volley.newRequestQueue(DisplayNameChangeActivity.this);
            queue.add(displayNameRequest);
        }
    }

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
