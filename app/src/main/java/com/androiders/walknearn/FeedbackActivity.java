package com.androiders.walknearn;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androiders.walknearn.DBFiles.FeedbackRequest;
import com.androiders.walknearn.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends AppCompatActivity {

    EditText mFeedbackText;
    Button mFeedbackBtn;
    UserLocalStore userLocalStore;
    Utility util = new Utility(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initializeViews();
        userLocalStore = new UserLocalStore(this);

        mFeedbackText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                sendFeedback();
                return true;
            }
        });

        mFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });
    }

    void initializeViews(){
        mFeedbackText = findViewById(R.id.feedback);
        mFeedbackBtn = findViewById(R.id.feedback_button);
    }

    void sendFeedback(){
        User user = userLocalStore.getLoggedInUser();

        final String msg = mFeedbackText.getText().toString();
        final String email = user.Email;

        if(util.isFieldEmpty(msg,mFeedbackText))
            mFeedbackText.requestFocus();
        else{
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("feedback",response);
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            util.showProgressDialog("Sending feedback",FeedbackActivity.this);
                            Toast.makeText(FeedbackActivity.this,"Sent feedback successfully",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(FeedbackActivity.this, SettingsActivity.class));
                        }
                        else{AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
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
            FeedbackRequest feedbackRequest = new FeedbackRequest(email,msg,responseListener);
            RequestQueue queue = Volley.newRequestQueue(FeedbackActivity.this);
            queue.add(feedbackRequest);
        }
    }
}