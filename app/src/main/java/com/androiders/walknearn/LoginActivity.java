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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.DBFiles.LoginRequest;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailText, mPasswordText;
    private Button mSignIn,mSignUp;
    private TextView mForgotPassword;
    ImageView mGoogleLogin;
    private CoordinatorLayout mCoordinatorLayout;
    String email,password;
    Utility util = new Utility(LoginActivity.this);
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        userLocalStore = new UserLocalStore(this);

        // Login on pressing enter key rather than clicking Login button
        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent!=null && keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                attemptLogin();
                return true;
            }
        });

        mSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

        mSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(SignUpIntent);
            }
        });

        mGoogleLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"Working Google login",Toast.LENGTH_LONG).show();
            }
        });

    }

    // Exits the app on pressing back button
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void initializeViews(){
        mEmailText = findViewById(R.id.email_login);
        mPasswordText = findViewById(R.id.password_login);
        mForgotPassword = findViewById(R.id.forgot_password);
        mSignIn = findViewById(R.id.sign_in_button);
        mSignUp = findViewById(R.id.login_sign_up_button);
        mGoogleLogin = findViewById(R.id.google_login);
        mCoordinatorLayout = findViewById(R.id.LoginCoordinatorLayout);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(){
        // Reset errors.
        mEmailText.setError(null);
        mPasswordText.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailText.getText().toString().toLowerCase();
        password = mPasswordText.getText().toString();

        boolean cancel = false;

        if(util.isFieldEmpty(email,mEmailText))
            cancel = true;
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if(util.isFieldEmpty(password,mPasswordText)){
            if(!cancel)
                cancel = true;
        }
        if(!cancel) {

            Response.Listener<String> responseListener = new Response.Listener<String>() {
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
        }
    }

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

