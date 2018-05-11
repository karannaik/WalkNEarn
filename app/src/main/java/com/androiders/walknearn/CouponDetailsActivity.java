package com.androiders.walknearn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androiders.walknearn.adapter.CouponItemDetailsAdapter;

import java.util.ArrayList;

// Coupon Details screen which shows the details of the coupon offers
public class CouponDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);

        setupToolbar(); // Call for a method to generalize action bars

        // Retrieving the extras obtained  from the parent(previous) activity
        Bundle extras = getIntent().getExtras();
        String couponId = extras.getString("CouponId");
        String couponValue = extras.getString("buttonCouponValue");
        couponValue = couponValue.substring(0,couponValue.length()-1);

        // Setting the image(logo) for the particular offer
        ImageView image = findViewById(R.id.selected_logo);
        image.setImageResource(Integer.parseInt(couponId));

        // Adding available coupon offers to list
        ListView listView = findViewById(R.id.selected_offers);
        int couponValueFromStr = Integer.parseInt(couponValue);
        ArrayList<String> offersAvailable = new ArrayList<>();
        offersAvailable.add(couponValue + ",10");
        offersAvailable.add(Integer.toString(couponValueFromStr*2) + ",20");
        offersAvailable.add(Integer.toString((couponValueFromStr*4)-100) + ",40");
        offersAvailable.add(Integer.toString(couponValueFromStr*8) + ",60");
        offersAvailable.add(Integer.toString(couponValueFromStr*10) + ",75");

        CouponItemDetailsAdapter availOffers = new CouponItemDetailsAdapter(this,R.layout.coupon_item_details,offersAvailable);
        listView.setAdapter(availOffers);

        // Specifies what is to be done on clicking the offer
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CouponDetailsActivity.this,"Insufficient walkcoins" +
                        "",Toast.LENGTH_LONG).show();

            }
        });
    }

    // Method generalizes the action bars
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Method specifies what is to be done on selecting a coupon item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
