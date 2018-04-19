package com.androiders.walknearn.dbfiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FeedbackRequest extends StringRequest {
    private static final String FEEDBACK_REQUEST_URL = "https://walknearn.000webhostapp.com/Feedback.php";
    private Map<String,String> params;

    public FeedbackRequest(String email, String msg, Response.Listener<String> listener){
        super(Method.POST,FEEDBACK_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("msg",msg);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
