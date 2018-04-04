package com.androiders.walknearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {


    ImageView ChngProfilePic;
    Button ChngPassword,ChngDisplayName,Logout;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        InitializeViews();

        userLocalStore = new UserLocalStore(this);

        ChngProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"Changing profile pic",Toast.LENGTH_LONG).show();
            }
        });

        ChngPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"Changing Password",Toast.LENGTH_LONG).show();
            }
        });

        ChngDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"Changing Display name",Toast.LENGTH_LONG).show();
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLocalStore.clearUserData();
                userLocalStore.setUserLggedIn(false);
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });

    }

    void InitializeViews(){
        ChngProfilePic = findViewById(R.id.ChngProfilePic);
        ChngPassword = findViewById(R.id.ChngPswrd);
        ChngDisplayName = findViewById(R.id.ChngDsplyName);
        Logout = findViewById(R.id.Logout);
    }

}
