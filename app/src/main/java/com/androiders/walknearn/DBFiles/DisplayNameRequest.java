package com.androiders.walknearn.DBFiles;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DisplayNameRequest extends StringRequest{
    private static final String DISPLAYNAME_REQUEST_URL = "https://walknearn.000webhostapp.com/DisplayNameUpdate.php";
    private Map<String,String> params;

    public DisplayNameRequest(String email, String name, Response.Listener<String> listener){
        super(Method.POST,DISPLAYNAME_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("name",name);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
