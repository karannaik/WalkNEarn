package com.androiders.walknearn.dbfiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordRequest extends StringRequest{
    private static final String FORGOT_PSWD_REQUEST_URL = "https://walknearn.000webhostapp.com/ForgotPassword.php";
    private Map<String,String> params;

    public ForgotPasswordRequest(String email_to, String sub, String msg, Response.Listener<String> listener){
        super(Method.POST,FORGOT_PSWD_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email_to",email_to);
        params.put("sub",sub);
        params.put("msg",msg);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
