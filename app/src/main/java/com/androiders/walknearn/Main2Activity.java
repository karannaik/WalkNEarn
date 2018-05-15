package com.androiders.walknearn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;

import com.androiders.walknearn.fitbitfiles.fragments.ActivitiesFragment;
import com.androiders.walknearn.fitbitfiles.fragments.DeviceFragment;
import com.androiders.walknearn.fitbitfiles.fragments.ProfileFragment;
import com.androiders.walknearn.fragment.HomeFragment;
import com.androiders.walknearn.ui.CustomFixedViewPager;

public class Main2Activity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private CustomFixedViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    final int[] ICONS = new int[]{
            R.mipmap.ic_home_white_36dp,
            R.mipmap.ic_settings_white_36dp,
            R.mipmap.ic_account_white_36dp,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        setViewPagerAndTabLayout();
//        setupToolbar();
    }

    private void setViewPagerAndTabLayout() {

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomFixedViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mTabLayout.getTabAt(0).getIcon().setAlpha(255);
                        mTabLayout.getTabAt(1).getIcon().setAlpha(88);
                        mTabLayout.getTabAt(2).getIcon().setAlpha(88);
                        break;
                    case 1:
                        mTabLayout.getTabAt(0).getIcon().setAlpha(88);
                        mTabLayout.getTabAt(1).getIcon().setAlpha(255);
                        mTabLayout.getTabAt(2).getIcon().setAlpha(88);
                        break;
                    case 2:
                        mTabLayout.getTabAt(0).getIcon().setAlpha(88);
                        mTabLayout.getTabAt(1).getIcon().setAlpha(88);
                        mTabLayout.getTabAt(2).getIcon().setAlpha(255);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.getTabAt(0).setIcon(ICONS[0]);
        mTabLayout.getTabAt(1).setIcon(ICONS[1]);
        mTabLayout.getTabAt(2).setIcon(ICONS[2]);
    }

    protected void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new HomeFragment();

                case 1:
                    return new DeviceFragment();

                case 2:
                    return new ActivitiesFragment();

            }
            return new ProfileFragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
