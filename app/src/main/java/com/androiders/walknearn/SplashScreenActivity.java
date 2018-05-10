package com.androiders.walknearn;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androiders.walknearn.model.UserLocalStore;

public class SplashScreenActivity extends AppCompatActivity {

    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        userLocalStore = new UserLocalStore(this);
        int SPLASH_TIME_OUT = 1500;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                if(userLocalStore.isUserLoggedIn()){
                    Intent i = new Intent(SplashScreenActivity.this, AskingPermissionsActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(SplashScreenActivity.this, LoginnActivity.class);
                    startActivity(i);
                    finish();
                }
                // This method will be executed once the timer is over
                // Start your app main activity

                /*if (new SharedPrefs(SplashScreenActivity.this).getSyncTime() == null) {

                    Intent i = new Intent(SplashScreenActivity.this, LoginnActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }*/

            }
        }, SPLASH_TIME_OUT);
    }
}
