package com.androiders.walknearn;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.androiders.walknearn.fragment.PromotedCouponsFragment;
import com.androiders.walknearn.ui.WrapContentHeightViewPager;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.util.ArrayList;
import java.util.List;

public class CouponTypeActivity extends AppCompatActivity {

    private WrapContentHeightViewPager mViewPager;
    private PageIndicatorView pageIndicatorView;
    private PromotedCouponsFragment mPromotedCouponFragment;
    private PromotedCouponsFragment mPromotedCouponFragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_type);

        setupToolbar();

        initializeViews();

    }

    private void setupToolbar() {

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {

        findViewById(R.id.textViewFoodDrinks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CouponTypeActivity.this, CouponActivity.class));
            }
        });

        findViewById(R.id.textViewSports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CouponTypeActivity.this, CouponActivity.class));
            }
        });

        findViewById(R.id.textViewCinema).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CouponTypeActivity.this, CouponActivity.class));
            }
        });

        findViewById(R.id.textViewComputers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CouponTypeActivity.this, CouponActivity.class));
            }
        });

        mViewPager = findViewById(R.id.viewPager);
        setupViewPager(mViewPager);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(2); // specify total count of indicators
        pageIndicatorView.setAnimationType(AnimationType.WORM);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        mPromotedCouponFragment = new PromotedCouponsFragment();
        mPromotedCouponFragment1 = new PromotedCouponsFragment();
        CouponTypeActivity.ViewPagerAdapter adapter = new CouponTypeActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mPromotedCouponFragment, "");
        adapter.addFragment(mPromotedCouponFragment1, "1");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
