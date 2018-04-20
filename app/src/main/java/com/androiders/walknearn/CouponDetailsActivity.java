package com.androiders.walknearn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CouponDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);

        Bundle extras = getIntent().getExtras();
        String couponId = extras.getString("CouponId");
        String couponvalue = extras.getString("CouponValue");
        couponvalue = couponvalue.substring(0,couponvalue.length()-1);

        ImageView image = findViewById(R.id.selected_logo);
        image.setImageResource(Integer.parseInt(couponId));

        ListView listView = findViewById(R.id.selected_offers);
        int couponValueFromStr = Integer.parseInt(couponvalue);
        ArrayList<String> offersAvailable = new ArrayList<>();
        offersAvailable.add(couponvalue + ",10");
        offersAvailable.add(Integer.toString(couponValueFromStr*2) + ",20");
        offersAvailable.add(Integer.toString((couponValueFromStr*4)-100) + ",40");
        offersAvailable.add(Integer.toString(couponValueFromStr*8) + ",60");
        offersAvailable.add(Integer.toString(couponValueFromStr*10) + ",75");

        CouponItemDetailsAdapter availOffers = new CouponItemDetailsAdapter(this,R.layout.coupon_item_details,offersAvailable);
        listView.setAdapter(availOffers);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CouponDetailsActivity.this,"Redeem coupon in progress",Toast.LENGTH_LONG).show();
            }
        });

    }
}
