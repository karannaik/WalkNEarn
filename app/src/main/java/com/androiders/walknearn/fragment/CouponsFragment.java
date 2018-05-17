package com.androiders.walknearn.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiders.walknearn.CouponActivity;
import com.androiders.walknearn.CouponTypeActivity;
import com.androiders.walknearn.Main2Activity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.adapter.CouponListAdapter;
import com.androiders.walknearn.model.CouponItemDetails;
import com.androiders.walknearn.ui.WrapContentHeightViewPager;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KARAN on 15-05-2018.
 */

public class CouponsFragment extends Fragment {
    private View view;
    private PageIndicatorView pageIndicatorView;

    public CouponsFragment() {
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
        view = inflater.inflate(R.layout.activity_coupon_type, container, false);

        initializeViews(); // Method call initializes the views in the activity

        return view;
    }

    // Method initializes the views in the activity
    private void initializeViews() {

        view.findViewById(R.id.textViewFoodDrinks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CouponActivity.class));
            }
        });

        view.findViewById(R.id.textViewSports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CouponActivity.class));
            }
        });

        view.findViewById(R.id.textViewCinema).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CouponActivity.class));
            }
        });

        view.findViewById(R.id.textViewComputers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CouponActivity.class));
            }
        });

        WrapContentHeightViewPager mViewPager = view.findViewById(R.id.viewPager);
        setupViewPager(mViewPager);

        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);
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
        PromotedCouponsFragment mPromotedCouponFragment = new PromotedCouponsFragment();
        PromotedCouponsFragment mPromotedCouponFragment1 = new PromotedCouponsFragment();
        CouponsFragment.ViewPagerAdapter adapter = new CouponsFragment.ViewPagerAdapter(getFragmentManager());
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
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
