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

public class UpdateForgotPasswordActivity extends AppCompatActivity {

    EditText mEmailText,mTempPassword,mNewPassword;
    Button mChangePassword;
    UserLocalStore userLocalStore;
    Utility util = new Utility(this);
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_frgt_pswrd);
        InitializeViews();
        setupToolbar();
        userLocalStore = new UserLocalStore(this);
        mNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                UpdatePassword();
                return true;
            }
        });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword();
            }
        });

    }

    void InitializeViews(){
        mEmailText = findViewById(R.id.email_updateforgotpswd);
        mTempPassword = findViewById(R.id.temp_password);
        mNewPassword = findViewById(R.id.update_password);
        mChangePassword = findViewById(R.id.updatefrgtpwsd_button);
    }

    private void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    void UpdatePassword(){
        mEmailText.setError(null);
        mTempPassword.setError(null);
        mNewPassword.setError(null);

        View focusView = null;
        boolean cancel = false;

        final String email = mEmailText.getText().toString();
        final String tempPassword = mTempPassword.getText().toString();
        final String newPassword = mNewPassword.getText().toString();

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
        else {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("PswrdUpdateCredentials", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists)
                            UpdatePasswordUtil(email,newPassword);
                        else{
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
            CheckCredentials credentials = new CheckCredentials(email,tempPassword,responseListener);
            RequestQueue queue = Volley.newRequestQueue(UpdateForgotPasswordActivity.this);
            queue.add(credentials);
        }
    }

    private void UpdatePasswordUtil(final String email, final String password){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("passwordUpdate", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        String name = jsonResponse.getString("userName");
                        String stepCnt = jsonResponse.getString("userStepCount");
                        User newUser = new User();
                        newUser.setUsername(name);
                        newUser.setStepCount(Integer.parseInt(stepCnt));
                        newUser.setEmail(email);
                        newUser.setPassword(password);
                        userLocalStore.storeUserData(newUser);
                        userLocalStore.setUserLggedIn(true);
                        util.showProgressDialog("Changing Password", UpdateForgotPasswordActivity.this);
                        startActivity(new Intent(UpdateForgotPasswordActivity.this, MainActivity.class));
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateForgotPasswordActivity.this);
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
        RequestQueue queue = Volley.newRequestQueue(UpdateForgotPasswordActivity.this);
        queue.add(passwordRequest);
    }

}
