package com.androiders.walknearn.fragment;

/**
 * Created by KARAN on 25-05-2016.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androiders.walknearn.CouponTypeActivity;
import com.androiders.walknearn.MainActivity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.SettingsActivity;
import com.androiders.walknearn.dbhelper.SharedPrefs;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.widgets.flowingdrawer.MenuFragment;

import java.io.IOException;
import java.net.URL;


public class LeftFloatingDrawerMenuFragment extends MenuFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_global_menu_header, container,
                false);
        initializeViews(view);
//        setupHeader(view);
        return  setupReveal(view) ;
    }

    private void initializeViews(final View view) {

        view.findViewById(R.id.textViewCoupons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CouponTypeActivity.class));

            }
        });

        view.findViewById(R.id.ProfilePic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SettingsActivity.class));
            }
        });

        final User user = new UserLocalStore(getActivity()).getLoggedInUser();

        TextView user_name = view.findViewById(R.id.textViewEmail);
        user_name.setText(user.getUsername());

        // Updates the photo if the photo slot is empty and an image url is present (given)
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            //load profile pic
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            try {
                                URL url = new URL(user.getPhotoUrl());
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
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

    public void updateStepcount(String stepCount){
        //update walkcoins somewhere here

    }
}