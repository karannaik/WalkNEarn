package com.androiders.walknearn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.androiders.walknearn.adapter.CouponListAdapter;
import com.androiders.walknearn.model.CouponItemDetails;
import com.androiders.walknearn.model.CouponModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// A coupons screen which shows all the available coupons
public class CouponActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> value = new ArrayList<>();
    private CouponListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        setupToolbar(); // Call to generalize action bars

        setupRecyclerView(); // Method call to set up the view to display coupons

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        Log.d("asdasd new::", "myRef: " + myRef);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue(); // Custom adapter to view the coupons
                mAdapter = new CouponListAdapter(CouponActivity.this, R.layout.coupon_item, value);
                mRecyclerView.setAdapter(mAdapter);
                Log.d("asdasd new::", "Value is: " + value.get(0));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("asdasd", "Failed to read value.", error.toException());
            }
        });

    }

    // Method to set up the view to display coupons
    private void setupRecyclerView()
    {
//        //Temporary static code
//        CouponItemDetails starbucks = new CouponItemDetails("starbucks",R.drawable.starbucks,"500+");
//        mArrayList.add(starbucks);
//        CouponItemDetails subway = new CouponItemDetails("subway",R.drawable.subway,"1000+");
//        mArrayList.add(subway);
//        CouponItemDetails bookstore = new CouponItemDetails("CSULB bookstore",R.drawable.bookstore,"900+");
//        mArrayList.add(bookstore);
//        CouponItemDetails carlsjr = new CouponItemDetails("Carl's Jr",R.drawable.carls_jr,"500+");
//        mArrayList.add(carlsjr);
//        CouponItemDetails coffeebean = new CouponItemDetails("Coffee Bean",R.drawable.coffee_bean,"600+");
//        mArrayList.add(coffeebean);
//        CouponItemDetails elpolloloco = new CouponItemDetails("El Pollo Loco",R.drawable.el_pollo_loco,"1500+");
//        mArrayList.add(elpolloloco);
//        CouponItemDetails outpostgrill = new CouponItemDetails("Outpost Grill",R.drawable.outpost_grill,"800+");
//        mArrayList.add(outpostgrill);
//        CouponItemDetails pandaexpress = new CouponItemDetails("Panda Express",R.drawable.panda_express,"500+");
//        mArrayList.add(pandaexpress);

        mRecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Custom adapter to view the coupons
        mAdapter = new CouponListAdapter(this, R.layout.coupon_item, value);
        mRecyclerView.setAdapter(mAdapter);
    }

    // Method to generalize the action bars
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Method specifies what is to be done on selecting a coupon from the coupon list
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
