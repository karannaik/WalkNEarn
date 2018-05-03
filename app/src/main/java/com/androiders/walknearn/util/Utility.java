package com.androiders.walknearn.util;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import com.androiders.walknearn.R;

/**
 * Created by K@R@N on 22-03-2016.
 */
public class Utility{

    private Context mContext;

    public Utility(Context context) {
        mContext = context;
    }

    public boolean isConnectingToInternet() {
        if (mContext != null) {

            ConnectivityManager connectivity = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
        }
        return false;
    }


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailValid(String email, EditText emailText){
        if(! isFieldEmpty(email,emailText)) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.setError(mContext.getResources().getString(R.string.error_invalid_email));
                return false;
            } else {
                emailText.setError(null);
                return true;
            }
        }
        return false;
    }

    public boolean isPasswordValid(String password,EditText passwordText){
        if (! isFieldEmpty(password,passwordText)){
            if(password.length() < 4){
                passwordText.setError(mContext.getResources().getString(R.string.error_invalid_password));
                return false;
            }
            else if(password.length() > 20){
                passwordText.setError(mContext.getResources().getString(R.string.error_lengthy_password));
                return false;
            }
            else{
                passwordText.setError(null);
                return true;
            }
        }
        return false;
    }

    public boolean isFieldEmpty(String fieldValue,EditText mField){
        if (fieldValue.isEmpty()) {
            mField.setError(mContext.getResources().getString(R.string.error_field_required));
            return true;
        }
        mField.setError(null);
        return false;
    }

    public void showProgressDialog(String title,Context context){
        ProgressDialog progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setTitle(title);
        progress.setMessage("Please wait....");
        progress.show();
    }

}
