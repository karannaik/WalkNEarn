package com.androiders.walknearn.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiders.walknearn.DisplayNameChangeActivity;
import com.androiders.walknearn.FeedbackActivity;
import com.androiders.walknearn.LoginnActivity;
import com.androiders.walknearn.Main2Activity;
import com.androiders.walknearn.PasswordChangeActivity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.SettingsActivity;
import com.androiders.walknearn.adapter.ListButtonAdapter;
import com.androiders.walknearn.model.User;
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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by KARAN on 15-05-2018.
 */

public class SettingsFragment extends Fragment {
    private View view;

    ImageView ChngProfilePic;
    UserLocalStore userLocalStore;
    ListView SettingsList;
    CallbackManager mCallbackManager;
    final int MEDIA_ACCESS_REQUESTCODE =1;
    final int CONTACT_INVITES_REQUESTCODE = 2;
    private Toolbar toolbar;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_settings, container, false);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        InitializeViews();
        userLocalStore = new UserLocalStore(getActivity());

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

        ListButtonAdapter settingsListAdapter = new ListButtonAdapter(getActivity(),R.layout.listview_text_type,settingsActionList);
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
                    startActivity(new Intent(getActivity(),PasswordChangeActivity.class));
                else if(position == 2) // Change name
                    startActivity(new Intent(getActivity(),DisplayNameChangeActivity.class));
                else if(position == 3){ // Switch view
                    long viewId = view.getId();
                    if(viewId == R.id.listViewSwitch){
                        if(l == 1)
                            Toast.makeText(getActivity(),"Switch on",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),"Switch off",Toast.LENGTH_LONG).show();
                    }
                }
                else if(position == 4) // Contacts invites
                    InviteFromContacts();
                else if(position == 5) // FB invites
                    InviteOnFacebook();
                else if(position == 6) // Feedback
                    startActivity(new Intent(getActivity(),FeedbackActivity.class));
                else if(position == 7) // T & C
                    Toast.makeText(getActivity(),"T & C checking",Toast.LENGTH_LONG).show();
                else if(position == 8) // Privacy policy
                    Toast.makeText(getActivity(),"Privacy policy checking",Toast.LENGTH_LONG).show();
                else if(position == 9) // Logout
                {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLoggedIn(false);
                    startActivity(new Intent(getActivity(), LoginnActivity.class));
                }
            }
        });

        return view;
    }



    void InitializeViews(){
        ChngProfilePic = view.findViewById(R.id.ChngProfilePic);
        SettingsList = view.findViewById(R.id.settingsList);

        final User user = new UserLocalStore(getActivity()).getLoggedInUser();
        if (user.getPhotoUrl()!=null && !user.getPhotoUrl().isEmpty()) {
            //load profile pic

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Bitmap bmp = null;
                            try {
                                URL url = new URL(user.getPhotoUrl());
                                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                ((de.hdodenhof.circleimageview.CircleImageView)view.findViewById(R.id.ProfilePic)).setImageBitmap(bmp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    void ChangeProfilePic(){
        MediaAccess();
    }

    void MediaAccess(){
        final AlertDialog.Builder MediaAccessAlert = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        MediaAccessAlert.setTitle("Media Access")
                .setMessage("Allow WALKNEARN to access media of your device")
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if(i == KeyEvent.KEYCODE_BACK)
                            startActivity(new Intent(getActivity(),SettingsActivity.class));
                        return true;
                    }
                })
                .setPositiveButton("Allow",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,MEDIA_ACCESS_REQUESTCODE);
                        Toast.makeText(getActivity(),"Changing profile pic",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder MediaAlert = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                        MediaAlert.setTitle("ALERT")
                                .setMessage("Denying WALKNEARN to access media prevents from updating the profile picture")
                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                        if(i == KeyEvent.KEYCODE_BACK)
                                            startActivity(new Intent(getActivity(),SettingsActivity.class));
                                        return true;
                                    }
                                })
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(getActivity(),SettingsActivity.class));
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
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == getActivity().RESULT_OK){
            switch (requestCode){
                case  MEDIA_ACCESS_REQUESTCODE:{
                    Uri targetUri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
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
                Toast.makeText(getActivity(),"Cancel",Toast.LENGTH_LONG).show();
                Log.d("Tag_Cancel","On cancel");
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
                Log.d("Tag_Error",error.toString());
            }
        });

        Toast.makeText(getActivity(),"Getting friends list ",Toast.LENGTH_SHORT).show();
        LoginManager.getInstance().logOut();
    }

}
