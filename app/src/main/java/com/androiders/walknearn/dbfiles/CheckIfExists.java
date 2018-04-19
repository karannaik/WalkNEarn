package com.androiders.walknearn.dbfiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckIfExists extends StringRequest{
    private static final String CHECK_IF_EXISTS_REQUEST_URL = "https://walknearn.000webhostapp.com/CheckIfExists.php";
    private Map<String,String> params;

    public CheckIfExists(String email, Response.Listener<String> listener){
        super(Method.POST,CHECK_IF_EXISTS_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
