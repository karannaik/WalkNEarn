package com.androiders.walknearn;

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
import com.androiders.walknearn.dbfiles.FeedbackRequest;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

// Feedback screen which allows the user to send feedback to the admin
public class FeedbackActivity extends AppCompatActivity {

    EditText mFeedbackText;
    Button mFeedbackBtn;
    UserLocalStore userLocalStore;
    Utility util = new Utility(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setupToolbar(); // Calling method to generalize the action bars
        initializeViews(); // Method call initializes all the views in the activity
        userLocalStore = new UserLocalStore(this); // Getting the user details from local database

        // Specifies what to do when the done button on keyboard is pressed rather than
        mFeedbackText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i != EditorInfo.IME_ACTION_DONE)
                    return false;
                sendFeedback();
                return true;
            }
        });

        // Specifies what is to be done on clicking the "Send Feedback" button
        mFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });
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

    // Method initializes the views in the activity
    void initializeViews(){
        mFeedbackText = findViewById(R.id.feedback);
        mFeedbackBtn = findViewById(R.id.feedback_button);
    }

    // Method specifies how the feedback is to be sent
    void sendFeedback(){
        User user = userLocalStore.getLoggedInUser();

        final String msg = mFeedbackText.getText().toString();
        final String email = user.getEmail();

        // Checks if the feedback box is empty
        if(util.isFieldEmpty(msg,mFeedbackText))
            mFeedbackText.requestFocus();
        else{ // Handling the feedback sent
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("feedback",response);
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success) // True means that the feedback has been sent successfully
                        {
                            util.showProgressDialog("Sending feedback",FeedbackActivity.this);
                            finish();
                        }
                        else // Handling the unsuccessful attempt to send a feedback
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this,R.style.AlertDialogTheme);
                            builder.setMessage("Unexpected error")
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
            // Handling the feedback sent on server side (using php queries)
            FeedbackRequest feedbackRequest = new FeedbackRequest(email,msg,responseListener);
            RequestQueue queue = Volley.newRequestQueue(FeedbackActivity.this);
            queue.add(feedbackRequest);
        }
    }
}