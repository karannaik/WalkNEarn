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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {


    ImageView ChngProfilePic;
    UserLocalStore userLocalStore;
    ListView SettingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        InitializeViews();

        ArrayList<String> settingsActionList = new ArrayList<>();
        settingsActionList.add(getString(R.string.action_change_password));
        settingsActionList.add(getString(R.string.action_change_name));
        settingsActionList.add(getString(R.string.notifications));
        settingsActionList.add(getString(R.string.contacts_invite));
        settingsActionList.add(getString(R.string.fb_invite));
        settingsActionList.add(getString(R.string.send_feedback));
        settingsActionList.add(getString(R.string.t_and_c));
        settingsActionList.add(getString(R.string.privacy_policy));
        settingsActionList.add(getString(R.string.logout));

        ListButtonAdapter settingsListAdapter = new ListButtonAdapter(this,R.layout.listview_text_type,settingsActionList);
        SettingsList.setAdapter(settingsListAdapter);

        SettingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                long viewId = view.getId();
                if(viewId == R.id.ChngProfilePic)
                    ChangeProfilePic();
                if(viewId == R.id.listViewSwitch){
                    if(l == 1)
                        Toast.makeText(SettingsActivity.this,"Switch on",Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(SettingsActivity.this,"Switch off",Toast.LENGTH_LONG).show();
                }
                if (position == 1) // Change password
                {
                    startActivity(new Intent(SettingsActivity.this,PasswordChangeActivity.class));
                }
                else if(position == 2) // Change name
                {
                    startActivity(new Intent(SettingsActivity.this,DisplayNameChangeActivity.class));
                }
                else if(position == 4) // Contacts invites
                {
                    InviteFromContacts();
                      //Toast.makeText(SettingsActivity.this,"Contacts invite checking",Toast.LENGTH_LONG).show();
                }
                else if(position == 5) // switch
                {
                    Toast.makeText(SettingsActivity.this,"FB invite checking",Toast.LENGTH_LONG).show();
                }
                else if(position == 6) // switch
                {
                    Toast.makeText(SettingsActivity.this,"Send feedback checking",Toast.LENGTH_LONG).show();
                }
                else if(position == 7) // switch
                {
                    Toast.makeText(SettingsActivity.this,"T & C checking",Toast.LENGTH_LONG).show();
                }
                else if(position == 8) // switch
                {
                    Toast.makeText(SettingsActivity.this,"Switch checking",Toast.LENGTH_LONG).show();
                }
                else if(position == 9) //Logout
                {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLggedIn(false);
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                }
            }
        });

    }

    void InviteFromContacts(){
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, 100);
    }

    void InitializeViews(){
        ChngProfilePic = findViewById(R.id.ChngProfilePic);
        SettingsList = findViewById(R.id.settingsList);
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
            if(requestCode == 100){
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("Invite", "onActivityResult: sent invitation " + id);
                }
            }
            else {
                Uri targetUri = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    ChngProfilePic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
