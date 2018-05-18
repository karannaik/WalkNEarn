package com.androiders.walknearn;

import android.content.Intent;
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

// Update forgot password screen allows the user to update the password (when user forgot the password)
public class UpdateForgotPasswordActivity extends AppCompatActivity {

    EditText mEmailText,mTempPassword,mNewPassword;
    Button mChangePassword;
    UserLocalStore userLocalStore;
    Utility util = new Utility(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_frgt_pswrd);
        InitializeViews(); // Initializing the views in the activity
        setupToolbar(); // Generalizing the action bars

        // Retrieving the data from local database
        userLocalStore = new UserLocalStore(this);

        // Specifies what os to be done on clicking done button on keyboard rather than the "Change Password" button
        mNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i != EditorInfo.IME_ACTION_DONE)
                    return false;
                UpdatePassword();
                return true;
            }
        });

        // Specifies what is to be done on clicking "Change Password" button
        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword();
            }
        });

    }

    // Method initializes the views in the activity
    void InitializeViews(){
        mEmailText = findViewById(R.id.email_updateforgotpswd);
        mTempPassword = findViewById(R.id.temp_password);
        mNewPassword = findViewById(R.id.update_password);
        mChangePassword = findViewById(R.id.updatefrgtpwsd_button);
    }

    // Method generalizes the action bars
    private void setupToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Specifies what is to be done on selecting an item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // Method helps in updating the password
    private void UpdatePassword(){
        mEmailText.setError(null);
        mTempPassword.setError(null);
        mNewPassword.setError(null);

        View focusView = null;
        boolean cancel = false;

        final String email = mEmailText.getText().toString();
        final String tempPassword = mTempPassword.getText().toString();
        final String newPassword = mNewPassword.getText().toString();

        // Checks for validations and if the fields are not empty
        if(util.isFieldEmpty(email,mEmailText)){
            focusView = mEmailText;
            cancel = true;
        }
        if(util.isFieldEmpty(tempPassword,mTempPassword)){
            if(!cancel){
                focusView = mTempPassword;
                cancel = true;
            }
        }
        if(!util.isPasswordValid(newPassword,mNewPassword)){
            if(!cancel){
                focusView = mNewPassword;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Checking if the user credentials are correct
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("PswrdUpdateCredentials", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) // True specifies that the user credentials are correct
                            UpdatePasswordUtil(email,newPassword);
                        else // Specifies how to handle the case when the user credentials were incorrect
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateForgotPasswordActivity.this, R.style.AlertDialogTheme);
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
            CheckCredentials credentials = new CheckCredentials(email,tempPassword,responseListener);
            RequestQueue queue = Volley.newRequestQueue(UpdateForgotPasswordActivity.this);
            queue.add(credentials);
        }
    }

    // Utility function which helps in updating the password
    private void UpdatePasswordUtil(final String email, final String password){
        // Trying to update the password
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("passwordUpdate", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) // True specifies that the password was updated successfully
                    {
                        String name = jsonResponse.getString("userName");
                        String stepCnt = jsonResponse.getString("userStepCount");
                        // Updating user data on local database
                        User newUser = new User();
                        newUser.setUsername(name);
                        newUser.setWalkCoins(Integer.parseInt(stepCnt));
                        newUser.setEmail(email);
                        newUser.setPassword(password);
                        userLocalStore.storeUserData(newUser);
                        userLocalStore.setUserLoggedIn(true);
                        util.showProgressDialog("Changing Password", UpdateForgotPasswordActivity.this);
                        startActivity(new Intent(UpdateForgotPasswordActivity.this, MainActivity.class));
                    }
                    else // Handling the situation where the attempt to update password failed
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateForgotPasswordActivity.this,R.style.AlertDialogTheme);
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
        // Updating password on server side
        PasswordRequest passwordRequest = new PasswordRequest(email,password,responseListener);
        RequestQueue queue = Volley.newRequestQueue(UpdateForgotPasswordActivity.this);
        queue.add(passwordRequest);
    }

}
