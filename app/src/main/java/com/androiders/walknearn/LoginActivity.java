package com.androiders.walknearn;

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
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.dbfiles.LoginRequest;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.util.Utility;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;


// A login screen that offers login via email/password.
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    // UI references.
    private EditText mEmailText, mPasswordText;
    private Button mSignIn, mSignUp;
    private TextView mForgotPassword;
    ImageView mGoogleLogin;
    private CoordinatorLayout mCoordinatorLayout;
    String email, password;
    private Utility mUtil;
    UserLocalStore userLocalStore;
    private int RC_SIGN_IN = 11;

    @Override
    protected void onStart() {
        super.onStart();

        // Get the account details only if the user is logged in
        // setUserLoggedIn is set to false on clicking Logout button in settings page
        if(userLocalStore.isUserLoggedIn()) {
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                startPermissionsActivity(account);
                finish();
            }
        }
    }

    private void startPermissionsActivity(GoogleSignInAccount account) {
        //storeUserData(account); // Call to update user details on local database
        Intent intent = new Intent(this, AskingPermissionsActivity.class);
        intent.putExtra("email", account.getEmail());
        startActivity(intent);
    }

    // Method updates the local database with the details from the google account
    private void storeUserData(GoogleSignInAccount account) {
        User user = new User();
        user.setUsername(account.getDisplayName()+"");
        user.setEmail(account.getEmail());
        if(account.getPhotoUrl()!=null) {
            user.setPhotoUrl(account.getPhotoUrl().toString());
        }
        userLocalStore.storeUserData(user);
        userLocalStore.setUserLoggedIn(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews(); // Initializing the views in the activity

        // Retrieving the user details from local database
        userLocalStore = new UserLocalStore(this);

        // Login on pressing enter key rather than clicking "Login" button
        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != EditorInfo.IME_ACTION_DONE)
                    return false;
                if (!mUtil.isConnectingToInternet())
                    showInternetSnackBar();
                else
                    attemptLogin();
                return true;
            }
        });

        // Login on clicking the "Login" button
        mSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUtil.isConnectingToInternet())
                    showInternetSnackBar();
                else
                    attemptLogin();
            }
        });

        // Specifying what is to be done on clicking "Forgot Password"
        mForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        // Specifying what is to be done on clicking "Sign Up" button
        mSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(SignUpIntent);
            }
        });

        // Specifying what is to be done on clicking " Sign Up Google" button
        mGoogleLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    // Method initializes the views in the activity
    void initializeViews() {
        mEmailText = findViewById(R.id.email_login);
        mPasswordText = findViewById(R.id.password_login);
        mForgotPassword = findViewById(R.id.forgot_password);
        mSignIn = findViewById(R.id.sign_in_button);
        mSignUp = findViewById(R.id.login_sign_up_button);
        mGoogleLogin = findViewById(R.id.google_login);
        mCoordinatorLayout = findViewById(R.id.LoginCoordinatorLayout);
        mUtil = new Utility(LoginActivity.this);
    }

    // Method allows the user to sign up with google account
    private void signInWithGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            startPermissionsActivity(account);
            Toast.makeText(this, "Welcome "+account.getGivenName(), Toast.LENGTH_LONG).show();
        }
        catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // Attempts to sign in or register the account specified by the login form.
    // If there are form errors (invalid email, missing fields, etc.), the
    // errors are presented and no actual login attempt is made.
    private void attemptLogin() {
        // Reset errors.
        mEmailText.setError(null);
        mPasswordText.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailText.getText().toString().toLowerCase();
        password = mPasswordText.getText().toString();

        boolean cancel = false;

        // Checks if fields are empty
        if (mUtil.isFieldEmpty(email, mEmailText))
            cancel = true;
        if (mUtil.isFieldEmpty(password, mPasswordText)) {
            if (!cancel)
                cancel = true;
        }
        if (!cancel) {
            // Attempting to login
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("success", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) // True specifies that the attempt to login was successful
                        {
                            // Retrieving the data from database and updating the local database
                            String name = jsonResponse.getString("userName");
                            String walkCoins = jsonResponse.getString("userWalkCoins");
                            User user = new User();
                            user.setUsername(name);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setWalkCoins(Double.parseDouble(walkCoins));

                            userLocalStore.storeUserData(user);
                            userLocalStore.setUserLoggedIn(true);
                            mUtil.showProgressDialog("Logging in", LoginActivity.this);
                            startActivity(new Intent(LoginActivity.this, AskingPermissionsActivity.class));
                        }
                        else // Handing the situation where login was unsuccessful
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this,R.style.AlertDialogTheme);
                            builder.setMessage("Invalid Email or Password")
                                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mEmailText.requestFocus();
                                    }
                                })
                                .create()
                                .show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            // Handing the login attempt in server side
            LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }
    }

    // Method checks if the internet is available
    private void showInternetSnackBar() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, getString(R.string.internet_unavailable), Snackbar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        snackbar.show();
    }
}