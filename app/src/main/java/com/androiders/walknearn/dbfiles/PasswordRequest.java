package com.androiders.walknearn.dbfiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// Class to connect to server and perform the request to update the password
public class PasswordRequest extends StringRequest {
    private static final String PASSWORD_REQUEST_URL = "https://walknearn.000webhostapp.com/PasswordUpdate.php";
    private Map<String,String> params;

    public PasswordRequest(String email, String newPassword,Response.Listener<String> listener){
        super(Method.POST,PASSWORD_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",newPassword);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
