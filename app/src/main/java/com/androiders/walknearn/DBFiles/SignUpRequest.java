package com.androiders.walknearn.DBFiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignUpRequest extends StringRequest {

    private static final String SIGNUP_REQUEST_URL = "https://walknearn.000webhostapp.com/SignUp.php";
    private Map<String,String> params;

    public SignUpRequest(String email, String password, String name, Response.Listener<String> listener){
        super(Method.POST,SIGNUP_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
        params.put("name",name);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
