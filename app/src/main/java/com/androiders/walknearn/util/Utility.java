package com.androiders.walknearn.util;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androiders.walknearn.R;
import com.androiders.walknearn.SignUpActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    public boolean isValidEmail(String email, TextView emailText){
        if (email.isEmpty()) {
            emailText.setError(mContext.getResources().getString(R.string.error_field_required));
            return false;
        }
        else if(! android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(mContext.getResources().getString(R.string.error_invalid_email));
            return false;
        }
        else {
            emailText.setError(null);
            return true;
        }
    }

    public boolean isPasswordValid(String password,TextView passwordText){
        if (password.isEmpty()) {
            passwordText.setError(mContext.getResources().getString(R.string.error_field_required));
            return false;
        }
        else if(password.length() < 4){
            passwordText.setError(mContext.getResources().getString(R.string.error_invalid_password));
            return false;
        }
        else{
            passwordText.setError(null);
            return true;
        }
    }

    public void showProgressDialog(String title,Context context){
        ProgressDialog progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setTitle(title);
        progress.setMessage("Please wait....");
        progress.show();
    }

}
