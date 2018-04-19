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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androiders.walknearn.model.UserLocalStore;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.appinvite.AppInviteInvitation;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    ImageView ChngProfilePic;
    UserLocalStore userLocalStore;
    ListView SettingsList;
    CallbackManager mCallbackManager;
    final int MEDIA_ACCESS_REQUESTCODE =1;
    final int CONTACT_INVITES_REQUESTCODE = 2;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();

        FacebookSdk.sdkInitialize(getApplicationContext());

        InitializeViews();
        userLocalStore = new UserLocalStore(this);

        ArrayList<String> settingsActionList = new ArrayList<>();
        settingsActionList.add("Profile picture");
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

                if(position == 0) {
                    long viewId = view.getId();
                    if (viewId == R.id.ChngProfilePic)
                        ChangeProfilePic();
                }
                else if (position == 1) // Change password
                    startActivity(new Intent(SettingsActivity.this,PasswordChangeActivity.class));
                else if(position == 2) // Change name
                    startActivity(new Intent(SettingsActivity.this,DisplayNameChangeActivity.class));
                else if(position == 3){ // Switch view
                    long viewId = view.getId();
                    if(viewId == R.id.listViewSwitch){
                        if(l == 1)
                            Toast.makeText(SettingsActivity.this,"Switch on",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SettingsActivity.this,"Switch off",Toast.LENGTH_LONG).show();
                    }
                }
                else if(position == 4) // Contacts invites
                    InviteFromContacts();
                else if(position == 5) // FB invites
                    InviteOnFacebook();
                else if(position == 6) // Feedback
                    startActivity(new Intent(SettingsActivity.this,FeedbackActivity.class));
                else if(position == 7) // T & C
                    Toast.makeText(SettingsActivity.this,"T & C checking",Toast.LENGTH_LONG).show();
                else if(position == 8) // Privacy policy
                    Toast.makeText(SettingsActivity.this,"Privacy policy checking",Toast.LENGTH_LONG).show();
                else if(position == 9) // Logout
                {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLggedIn(false);
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                    startActivityForResult(intent,MEDIA_ACCESS_REQUESTCODE);
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

    void InviteFromContacts(){
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, CONTACT_INVITES_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case  MEDIA_ACCESS_REQUESTCODE:{
                    Uri targetUri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                        ChngProfilePic.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case CONTACT_INVITES_REQUESTCODE: {
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    for (String id : ids) {
                        Log.d("Invite", "onActivityResult: sent invitation " + id);
                    }
                }
                break;
                default:
                    mCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }

    void InviteOnFacebook(){
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile","user_friends"));

        LoginManager.getInstance().registerCallback(mCallbackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Replace invitable_friends[gives the list of all friends] with friends[gives the list of friends using the app]
                new GraphRequest(loginResult.getAccessToken(),"/me/friends",null, HttpMethod.GET,new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Helps getting the list of friends (Only after app reviewed by Facebook developers)
                         try {
                             JSONArray rawName = response.getJSONObject().getJSONArray("data");
                             ArrayList<String> friends = new ArrayList<>();
                             for(int i=0;i < rawName.length();i++){
                                 friends.add(rawName.getJSONObject(i).getString("name"));
                                 Log.d("JSONArray",rawName.getJSONObject(i).getString("name"));
                             }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
            }
            @Override
            public void onCancel() {
                Toast.makeText(SettingsActivity.this,"Cancel",Toast.LENGTH_LONG).show();
                Log.d("Tag_Cancel","On cancel");
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_LONG).show();
                Log.d("Tag_Error",error.toString());
            }
        });

        Toast.makeText(this,"Getting friends list ",Toast.LENGTH_SHORT).show();
        LoginManager.getInstance().logOut();
    }
}
