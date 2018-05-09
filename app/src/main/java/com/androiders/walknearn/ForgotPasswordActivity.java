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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.dbfiles.CheckIfExists;
import com.androiders.walknearn.dbfiles.ForgotPasswordRequest;
import com.androiders.walknearn.dbfiles.PasswordRequest;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

// Forgot Password screen which allows the user to update the password when forgot
public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mEmailText;
    Button mContinueBtn,mCancelBtn;
    Utility util = new Utility(ForgotPasswordActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        InitializeViews(); // Initializing the views in the activity

        setupToolbar(); // Generalizing the action bars

        // Specifying what is to be done on clicking "Continue" button
        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptFrgtPswd();
            }
        });

        // Specifying what is to be done on clicking done key on keyboard rather than clicking "Continue" button
        mEmailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i != EditorInfo.IME_ACTION_DONE)
                    return false;
                attemptFrgtPswd();
                return true;
            }
        });

        // Specifying what is to be done on clicking "Cancel" button
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Method initializes the views in the activity
    void InitializeViews(){
        mEmailText = findViewById(R.id.email_forgotpswd);
        mContinueBtn = findViewById(R.id.frgtpwsd_button);
        mCancelBtn = findViewById(R.id.frgtpwsdcncl_button);
    }

    // Method generalizes the action bars
    private void setupToolbar() {

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

    // Method handles the forgot password case
    void attemptFrgtPswd(){
        final String email = mEmailText.getText().toString();

        mEmailText.setError(null);

        // Checking for the empty fields
        if(util.isFieldEmpty(email,mEmailText))
            mEmailText.requestFocus();
        else // Checking if the user credentials are correct
        {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("FrgtPswduserExists", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) // True specifies that the user credentials are correct
                            attemptFrgtPswdUtil(email);
                        else // Handling the case where user credentials are incorrect
                        {
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

    // Utility function to handle the forgot password case
    void attemptFrgtPswdUtil(final String email){

        // Generating a randome password and sending it in mail
        final String randomPswd = GenerateRandomPswd();

        // Temporary static data (Should be replaced by the user details)
        String to = "jyothsna.kilaru04@gmail.com";
        String sub = "WalkNEarn Recovery Password";
        String msg = "Try changing the password using "+randomPswd+" as old password";

        // Sending a mail to the user
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("FrgtPswd", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) // True specifies mail was sent to the user successfully
                    {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    Log.d("FrgtPswdUpdate", response);
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success) // True specifies that the password was updates with random password successfully, thereby asking the use to update password
                                        startActivity(new Intent(ForgotPasswordActivity.this,UpdateForgotPasswordActivity.class));
                                    else // Handling the situation where the password updating was unsuccessful
                                    {
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
                        // Handling the updating of password to random password generated on server side
                        PasswordRequest passwordRequest = new PasswordRequest(email,randomPswd,responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                        queue.add(passwordRequest);
                    }
                    else // Handling the situation where the sending of mail was unsuccessful
                    {
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
        // Handling the sending of mail to the user with the random password generated
        ForgotPasswordRequest frgtPswd = new ForgotPasswordRequest(to,sub,msg,responseListener);
        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
        queue.add(frgtPswd);
    }

    // Method generates a random sequence of capital letters and digits os size 8
    // Used this string as a random password
    String GenerateRandomPswd(){
        String CharPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder ResStr = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++)
            ResStr.append(CharPool.charAt(random.nextInt(CharPool.length())));
        return ResStr.toString();
    }

}
