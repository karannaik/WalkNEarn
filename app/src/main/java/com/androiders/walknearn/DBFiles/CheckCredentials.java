package com.androiders.walknearn.DBFiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jyoth on 4/7/2018.
 */

public class CheckCredentials extends StringRequest{
    private static final String SIGNUP_REQUEST_URL = "https://walknearn.000webhostapp.com/CheckIfExists.php";
    private Map<String,String> params;

    public CheckCredentials(String email, String password, Response.Listener<String> listener){
        super(Method.POST,SIGNUP_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}