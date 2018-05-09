package com.androiders.walknearn;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.dbfiles.CheckIfExists;
import com.androiders.walknearn.dbfiles.SignUpRequest;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

// Sign up activity screen allows the user to create a new account
public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailText, mDisplayName, mPasswordText;
    private Button mSignUpButton, mSignInButton;
    private CoordinatorLayout mCoordinatorLayout;
    String email,password,name;
    Utility util = new Utility(SignUpActivity.this);
    UserLocalStore userLocalStore;

    @SuppressLint("WrongViewCast")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeViews(); // Initializing the views in the activity

        // Retrieving the data from local database
        userLocalStore = new UserLocalStore(this);

        // Signing up on pressing enter key rather than clicking "Sign Up" button
        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i != EditorInfo.IME_ACTION_DONE)
                    return false;
                attemptSignUp();
                return true;
            }
        });

        // Signing up the user on clicking "Sign Up" button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        // Handling the case on clicking "Sign In" button
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

    }

    // Method initializes the views in the activity
    private void initializeViews() {
        mEmailText = findViewById(R.id.email_signup);
        mDisplayName = findViewById(R.id.name_signup);
        mPasswordText = findViewById(R.id.password_signup);
        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInButton = findViewById(R.id.signup_sign_in_button);
        mCoordinatorLayout = findViewById(R.id.SignUpCoordinatorLayout);
    }

    // Method helps in signing up the user
    private void attemptSignUp(){

        mEmailText.setError(null);
        mPasswordText.setError(null);

        email = mEmailText.getText().toString().toLowerCase();
        name = mDisplayName.getText().toString();
        password = mPasswordText.getText().toString();

        boolean cancel = false;

        // Checking for valid emails and that no mandatory field is empty
        if(!util.isEmailValid(email,mEmailText))
            cancel = true;
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if (! name.isEmpty()){
            if(name.length() > 20){
                mDisplayName.setError(getString(R.string.error_lengthy_name));
                if(!cancel)
                    cancel = true;
            }
        }
        else
            name = GetNameFromEmail(email);
        if(!util.isPasswordValid(password,mPasswordText)){
            if(!cancel)
                cancel = true;
        }

        if(!cancel) {
            // Trying to check if the email id already exists
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("userExists", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) // True specifies that the user with given mail exists
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.AlertDialogTheme);
                            builder.setMessage("Email already exists")
                                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mEmailText.requestFocus();
                                    }
                                })
                                .create()
                                .show();
                        }
                        else // Specifies what is to be done if email id doesn't exist
                            attemptSignUpUtil();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            // Checking if email id exists on sever side
            CheckIfExists UserExists = new CheckIfExists(email,responseListener);
            RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
            queue.add(UserExists);
        }
    }

    // Utility function which helps in signing up the user
    private void attemptSignUpUtil()
    {
        // Adds the user to database
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("signUp", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) // True specifies that the sign up was successful
                    {
                        // Adding (updating) data on local database
                        User newUser = new User();
                        newUser.setPassword(password);
                        newUser.setUsername(name);
                        newUser.setEmail(email);
                        userLocalStore.storeUserData(newUser);
                        userLocalStore.setUserLoggedIn(true);
                        util.showProgressDialog("Signing up",SignUpActivity.this);
                        Intent intent = new Intent(SignUpActivity.this, AskingPermissionsActivity.class);
                        startActivity(intent);
                    }
                    else // Handling the unsuccessful attempt to sign up
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.AlertDialogTheme);
                        builder.setMessage("Sign up failed")
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
        // Signing up the user on server side
        SignUpRequest signUpRequest = new SignUpRequest(email,password,name,responseListener);
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        queue.add(signUpRequest);
    }

    // Method returns part of the mail id, if the name field is empty
    private String GetNameFromEmail(String email){
        String ResStr = "";
        int i = 0;
        while(Character.isLetter(email.charAt(i)) && i < 20){
            ResStr += email.charAt(i);
            i++;
        }
        return ResStr;
    }

    // Checks if the internet is available or not
    private void showInternetSnackBar() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout,getString(R.string.internet_unavailable), Snackbar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        snackbar.show();
    }

}
