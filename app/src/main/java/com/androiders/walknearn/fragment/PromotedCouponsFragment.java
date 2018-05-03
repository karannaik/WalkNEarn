package com.androiders.walknearn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiders.walknearn.R;
import com.androiders.walknearn.model.CouponItemDetails;

/**
 * Created by KARAN on 29-04-2018.
 */

public class PromotedCouponsFragment extends Fragment {

    private View view;
    private TextView mTextViewCalories;
    private ImageView imageViewCoupon;
    private TextView textViewCouponName;
    private TextView textViewRequired;
    private Button buttonCouponValue;

    public PromotedCouponsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_promoted_coupons, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {

        imageViewCoupon = view.findViewById(R.id.imageViewCoupon);
        textViewCouponName = view.findViewById(R.id.textViewDesc);
        textViewRequired = view.findViewById(R.id.textViewRequired);
        buttonCouponValue = view.findViewById(R.id.logo_value);

        CouponItemDetails carlsjr = new CouponItemDetails("Carl's Jr",R.drawable.carls_jr,"500+");
        CouponItemDetails coffeebean = new CouponItemDetails("Coffee Bean",R.drawable.coffee_bean,"600+");

        imageViewCoupon.setImageResource(carlsjr.getOfferId());
        textViewCouponName.setText(carlsjr.getOfferName());
        textViewRequired.setText(carlsjr.getOfferValue());


    }

}
