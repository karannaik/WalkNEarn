package com.androiders.walknearn;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

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
                ChangeProfilePic();
            }
        });

        ChngPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this,PasswordChangeActivity.class));
            }
        });

        ChngDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this,DisplayNameChangeActivity.class));
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

    void ChangeProfilePic(){
        MediaAccess();
    }

    void MediaAccess(){
        final AlertDialog.Builder MediaAccessAlert = new AlertDialog.Builder(SettingsActivity.this,R.style.AlertDialogTheme);
        MediaAccessAlert.setTitle("Media Access")
            .setMessage("Allow WALKNEARN to access media of your device")
            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if(i == KeyEvent.KEYCODE_BACK)
                        startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
                    return true;
                }
            })
            .setPositiveButton("Allow",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,0);
                    Toast.makeText(SettingsActivity.this,"Changing profile pic",Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AlertDialog.Builder MediaAlert = new AlertDialog.Builder(SettingsActivity.this,R.style.AlertDialogTheme);
                    MediaAlert.setTitle("ALERT")
                        .setMessage("Denying WALKNEARN to access media prevents from updating the profile picture")
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                if(i == KeyEvent.KEYCODE_BACK)
                                    startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
                                return true;
                            }
                        })
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MediaAccess();
                            }
                        })
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .show();
                }
            })
            .setIcon(R.drawable.ic_perm_media_black_24dp)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try{
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                ChngProfilePic.setImageBitmap(bitmap);
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

}
