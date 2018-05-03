package com.androiders.walknearn.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

public class CouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    int resource;
    ArrayList<CouponItemDetails> coupons = null;

    public CouponListAdapter(@NonNull Context context, int resource, ArrayList<CouponItemDetails> coupons) {
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

        final CouponItemDetails couponItemDetails = coupons.get(position);

        ((ViewHolder) holder).imageViewCoupon.setImageResource(couponItemDetails.getOfferId());
        ((ViewHolder) holder).textViewCouponName.setText(couponItemDetails.getOfferName());
        ((ViewHolder) holder).textViewRequired.setText(couponItemDetails.getOfferValue());
        ((ViewHolder) holder).buttonCouponValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,CouponDetailsActivity.class);
                intent.putExtra("CouponId", Integer.toString(couponItemDetails.getOfferId()));
                intent.putExtra("buttonCouponValue",couponItemDetails.getOfferValue());
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
        Button buttonCouponValue;

        public ViewHolder(View view) {
            super(view);

            imageViewCoupon = view.findViewById(R.id.imageViewCoupon);
            textViewCouponName = view.findViewById(R.id.textViewDesc);
            textViewRequired = view.findViewById(R.id.textViewRequired);
            buttonCouponValue = view.findViewById(R.id.buttonRedeem);
        }
    }
}
