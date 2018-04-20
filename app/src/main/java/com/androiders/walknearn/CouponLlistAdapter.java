package com.androiders.walknearn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CouponLlistAdapter extends ArrayAdapter<CouponItemDetails> {

    Context context;
    int resource;
    ArrayList<CouponItemDetails> coupons = null;

    public CouponLlistAdapter(@NonNull Context context, int resource, ArrayList<CouponItemDetails> coupons) {
        super(context, resource, coupons);
        this.context = context;
        this.resource = resource;
        this.coupons = coupons;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        CouponItemDetails coupon = coupons.get(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource,parent,false);
        }
        ImageView CouponImage = convertView.findViewById(R.id.logo);
        TextView CouponName = convertView.findViewById(R.id.logo_name);
        Button CouponValue = convertView.findViewById(R.id.logo_value);
        CouponImage.setImageResource(coupon.OfferId);
        CouponName.setText(coupon.OfferName);
        CouponValue.setText(coupon.OfferValue);
        CouponValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view,position,0);
            }
        });
        return convertView;
    }

    public String getCouponValue(int position){
        CouponItemDetails coupon = coupons.get(position);
        return coupon.OfferValue;
    }

    public String getCouponId(int position){
        CouponItemDetails coupon = coupons.get(position);
        return Integer.toString(coupon.OfferId);
    }

}
