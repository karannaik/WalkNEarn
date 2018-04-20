package com.androiders.walknearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CouponActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        final ListView coupons_list = findViewById(R.id.coupon_list);
        ArrayList<CouponItemDetails> coupons = new ArrayList<>();

        CouponItemDetails starbucks = new CouponItemDetails("Starbucks",R.drawable.starbucks,"500+");
        coupons.add(starbucks);
        CouponItemDetails subway = new CouponItemDetails("Subway",R.drawable.subway,"1000+");
        coupons.add(subway);
        CouponItemDetails bookstore = new CouponItemDetails("CSULB Bookstore",R.drawable.bookstore,"900+");
        coupons.add(bookstore);
        CouponItemDetails carlsjr = new CouponItemDetails("Carl's Jr",R.drawable.carls_jr,"500+");
        coupons.add(carlsjr);
        CouponItemDetails coffeebean = new CouponItemDetails("Coffee Bean",R.drawable.coffee_bean,"600+");
        coupons.add(coffeebean);
        CouponItemDetails elpolloloco = new CouponItemDetails("El Pollo Loco",R.drawable.el_pollo_loco,"1500+");
        coupons.add(elpolloloco);
        CouponItemDetails outpostgrill = new CouponItemDetails("Outpost Grill",R.drawable.outpost_grill,"800+");
        coupons.add(outpostgrill);
        CouponItemDetails pandaexpress = new CouponItemDetails("Panda Express",R.drawable.panda_express,"500+");
        coupons.add(pandaexpress);

        final CouponLlistAdapter couponLlistAdapter = new CouponLlistAdapter(this,R.layout.coupon_item,coupons);
        coupons_list.setAdapter(couponLlistAdapter);

        coupons_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CouponActivity.this,CouponDetailsActivity.class);
                intent.putExtra("CouponId",couponLlistAdapter.getCouponId(i));
                intent.putExtra("CouponValue",couponLlistAdapter.getCouponValue(i));
                startActivity(intent);
            }
        });

    }
}
