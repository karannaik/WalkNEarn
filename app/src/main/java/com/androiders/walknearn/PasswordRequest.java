package com.androiders.walknearn;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PasswordRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "https://walknearn.000webhostapp.com/PasswordUpdate.php";
    private Map<String,String> params;

    public PasswordRequest(String email, String oldPassword, String newPassword,Response.Listener<String> listener){
        super(Method.POST,LOGIN_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("oldpassword",oldPassword);
        params.put("newpassword",newPassword);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}