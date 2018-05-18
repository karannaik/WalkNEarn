package com.androiders.walknearn.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiders.walknearn.CouponDetailsActivity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.model.CouponItemDetails;
import com.androiders.walknearn.model.CouponModel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

// Custom list adapter for Coupons
public class CouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    int resource;
    ArrayList<HashMap<String, String>> coupons = null;

    public CouponListAdapter(@NonNull Context context, int resource, ArrayList<HashMap<String, String>> coupons) {
        this.context = context;
        this.resource = resource;
        this.coupons = coupons;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                resource, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

//        ((ViewHolder) holder).imageViewCoupon.setImageResource(couponItemDetails.get());
        ((ViewHolder) holder).textViewCouponName.setText(coupons.get(position).get("name"));
        ((ViewHolder) holder).textViewRequired.setText(coupons.get(position).get("price"));
        ((ViewHolder) holder).textViewDiscount.setText(coupons.get(position).get("walkcoins"));
        ((ViewHolder) holder).textViewDesc.setText(coupons.get(position).get("description"));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        try {
//                            URL url = new URL(user.getPhotoUrl());
//                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                            mProfilePic.setImageBitmap(bmp);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
        ((ViewHolder) holder).buttonCouponValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,CouponDetailsActivity.class);
//                intent.putExtra("CouponId", Integer.toString(couponItemDetails.getOfferId()));
//                intent.putExtra("buttonCouponValue",couponItemDetails.getOfferValue());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCoupon;
        TextView textViewCouponName;
        TextView textViewRequired;
        TextView textViewDiscount;
        TextView textViewDesc;
        Button buttonCouponValue;

        public ViewHolder(View view) {
            super(view);

            imageViewCoupon = view.findViewById(R.id.imageViewCoupon);
            textViewCouponName = view.findViewById(R.id.textViewName);
            textViewRequired = view.findViewById(R.id.textViewRequired);
            buttonCouponValue = view.findViewById(R.id.buttonRedeem);
            textViewDiscount = view.findViewById(R.id.textViewDiscount);
            textViewDesc = view.findViewById(R.id.desc);
        }
    }
}
