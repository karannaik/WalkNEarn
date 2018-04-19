package com.androiders.walknearn;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.DBFiles.CheckIfExists;
import com.androiders.walknearn.DBFiles.ForgotPasswordRequest;
import com.androiders.walknearn.DBFiles.PasswordRequest;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mEmailText;
    Button mContinueBtn,mCancelBtn;
    Utility util = new Utility(ForgotPasswordActivity.this);
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        InitializeViews();

        setupToolbar();
        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptFrgtPswd();
            }
        });

        mEmailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                attemptFrgtPswd();
                return true;
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
            }
        });

    }

    void InitializeViews(){
        mEmailText = findViewById(R.id.email_forgotpswd);
        mContinueBtn = findViewById(R.id.frgtpwsd_button);
        mCancelBtn = findViewById(R.id.frgtpwsdcncl_button);
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

    void attemptFrgtPswd(){
        final String email = mEmailText.getText().toString();

        mEmailText.setError(null);
        if(util.isFieldEmpty(email,mEmailText))
            mEmailText.requestFocus();
        else {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("FrgtPswduserExists", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) {
                            attemptFrgtPswdUtil(email);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AlertDialogTheme);
                            builder.setMessage("Invalid email address")
                                    .setPositiveButton("Create new", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                        }
                                    })
                                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mEmailText.requestFocus();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            CheckIfExists UserExists = new CheckIfExists(email, responseListener);
            RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
            queue.add(UserExists);
        }
    }

    void attemptFrgtPswdUtil(final String email){
        final String randomPswd = GenerateRandomPswd();
        Toast.makeText(ForgotPasswordActivity.this,randomPswd,Toast.LENGTH_LONG).show();
        String to = "jyothsna.kilaru04@gmail.com";
        String sub = "WalkNEarn Recovery Password";
        String msg = "Try changing the password using "+randomPswd+" as old password";
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("FrgtPswd", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    Log.d("FrgtPswdUpdate", response);
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        startActivity(new Intent(ForgotPasswordActivity.this,UpdateFrgtPswrdActivity.class));
                                    }
                                    else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AlertDialogTheme);
                                        builder.setMessage("Unexpected error")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                    }
                                }
                                catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        PasswordRequest passwordRequest = new PasswordRequest(email,randomPswd,responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                        queue.add(passwordRequest);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AlertDialogTheme);
                        builder.setMessage("Operation Unsuccessful")
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
        ForgotPasswordRequest frgtPswd = new ForgotPasswordRequest(to,sub,msg,responseListener);
        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
        queue.add(frgtPswd);
    }

    String GenerateRandomPswd(){
        String CharPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder ResStr = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++)
            ResStr.append(CharPool.charAt(random.nextInt(CharPool.length())));
        return ResStr.toString();
    }

}
