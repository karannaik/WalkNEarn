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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.DBFiles.CheckIfExists;
import com.androiders.walknearn.DBFiles.SignUpRequest;
import com.androiders.walknearn.util.Utility;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailText, mDisplayName, mPasswordText;
    private Button mSignUpButton, mSignInButton;
    private ImageView mFacebookSignUp, mGoogleSignUp;
    private CoordinatorLayout mCoordinatorLayout;
    private CallbackManager mCallBackManager;
    String email,password,name;
    Utility util = new Utility(SignUpActivity.this);
    UserLocalStore userLocalStore;

    @SuppressLint("WrongViewCast")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);
        initializeViews();
        userLocalStore = new UserLocalStore(this);

        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                attemptSignUp();
                return true;
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

        mCallBackManager = CallbackManager.Factory.create();

        //mInfo = findViewById(R.id.info);
        mFacebookSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FBLogin();

            }
        });

        mGoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUpActivity.this,"Working google sign up", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeViews() {
        mEmailText = findViewById(R.id.email_signup);
        mDisplayName = findViewById(R.id.name_signup);
        mPasswordText = findViewById(R.id.password_signup);
        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInButton = findViewById(R.id.signup_sign_in_button);
        mFacebookSignUp = findViewById(R.id.fb_signup);
        mGoogleSignUp = findViewById(R.id.google_signup);
        mCoordinatorLayout = findViewById(R.id.SignUpCoordinatorLayout);
    }

    private void attemptSignUp(){

        mEmailText.setError(null);
        mPasswordText.setError(null);

        email = mEmailText.getText().toString();
        name = mDisplayName.getText().toString();
        password = mPasswordText.getText().toString();

        if (name.isEmpty())
            name = "temp name";
        View focusView = null;
        boolean cancel = false;

        if(!util.isEmailValid(email,mEmailText)){
            focusView = mEmailText;
            cancel = true;
        }
        if (!util.isConnectingToInternet())
            showInternetSnackBar();
        if(!util.isPasswordValid(password,mPasswordText)){
            if(!cancel){
                focusView = mPasswordText;
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
                        Log.d("userExists", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean exists = jsonResponse.getBoolean("exists");
                        if (exists) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.AlertDialogTheme);
                            builder.setMessage("Email already exists")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        } else
                            GPSAccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            CheckIfExists UserExists = new CheckIfExists(email,responseListener);
            RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
            queue.add(UserExists);
        }
    }

    private void GPSAccess(){
        final AlertDialog.Builder GPSAccessAlert = new AlertDialog.Builder(SignUpActivity.this,R.style.AlertDialogTheme);
        GPSAccessAlert.setTitle("GPS ACCESS")
            .setMessage("Allow WALKNEARN to access GPS location of your device")
            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if(i == KeyEvent.KEYCODE_BACK)
                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    return true;
                }
            })
            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("signUp", response);
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    User NewUser = new User(email,password,name);
                                    userLocalStore.storeUserData(NewUser);
                                    userLocalStore.setUserLggedIn(true);
                                    util.showProgressDialog("Signing up",SignUpActivity.this);
                                    Intent MainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(MainActivityIntent);
                                }
                                else {
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
                    SignUpRequest signUpRequest = new SignUpRequest(email,password,name,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                    queue.add(signUpRequest);
                }
            })
            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AlertDialog.Builder GPSAlert = new AlertDialog.Builder(SignUpActivity.this,R.style.AlertDialogTheme);
                    GPSAlert.setTitle("ALERT!!")
                        .setMessage("Denying WALKNEARN to access GPS location prevents from continuing further")
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                if(i == KeyEvent.KEYCODE_BACK)
                                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                return true;
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GPSAccess();
                            }
                        })
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
                }
            })
            .setIcon(R.drawable.ic_place_black_24dp)
            .show();
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

    private void FBLogin()
    {
        mCallBackManager = CallbackManager.Factory.create();

        // Set permissions to open the fb login page (By default opens if used com.facebook.....loginbutton
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));

        LoginManager.getInstance().registerCallback(mCallBackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //System.out.println("Success");
                Toast.makeText(SignUpActivity.this,"Success",Toast.LENGTH_LONG).show();
                /*GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                            System.out.println("ERROR");
                        }
                        else {
                            System.out.println("Success");
                            try {
                                String jsonresult = String.valueOf(json);
                                System.out.println("JSON Result"+jsonresult);

                                String str_email = json.getString("email");
                                String str_id = json.getString("id");
                                String str_firstname = json.getString("first_name");
                                String str_lastname = json.getString("last_name");
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
                */
            }
            @Override
            public void onCancel() {
                Toast.makeText(SignUpActivity.this,"Cancel",Toast.LENGTH_LONG).show();
                Log.d("Tag_Cancel","On cancel");
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignUpActivity.this,"Error",Toast.LENGTH_LONG).show();
                Log.d("Tag_Error",error.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }

}
