package com.androiders.walknearn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;
import com.androiders.walknearn.ui.CustomFixedViewPager;

import java.io.IOException;
import java.net.URL;

public class Main2Activity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private CustomFixedViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    final int[] ICONS = new int[]{
            R.mipmap.ic_home_white_36dp,
            R.mipmap.ic_settings_white_36dp,
            R.mipmap.ic_account_white_36dp,
            R.mipmap.ic_wallet_white_36dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        setViewPagerAndTabLayout();
        loadProfilePic();
    }

    private void loadProfilePic() {

        final User user = new UserLocalStore(this).getLoggedInUser();

        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            //load profile pic
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            try {
                                URL url = new URL(user.getPhotoUrl());
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                ((de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.ProfilePic)).setImageBitmap(bmp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

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
                setIconColorsSelectables(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.getTabAt(0).setIcon(ICONS[0]);
        mTabLayout.getTabAt(1).setIcon(ICONS[1]);
        mTabLayout.getTabAt(2).setIcon(ICONS[2]);
        mTabLayout.getTabAt(3).setIcon(ICONS[3]);
        setIconColorsSelectables(0);
        mViewPager.setPagingEnabled(false);
    }

    private void setIconColorsSelectables(int position) {
        switch (position) {
            case 0:
                mTabLayout.getTabAt(0).getIcon().setAlpha(255);
                mTabLayout.getTabAt(1).getIcon().setAlpha(50);
                mTabLayout.getTabAt(2).getIcon().setAlpha(50);
                mTabLayout.getTabAt(3).getIcon().setAlpha(50);
                break;
            case 1:
                mTabLayout.getTabAt(0).getIcon().setAlpha(50);
                mTabLayout.getTabAt(1).getIcon().setAlpha(255);
                mTabLayout.getTabAt(2).getIcon().setAlpha(50);
                mTabLayout.getTabAt(3).getIcon().setAlpha(50);
                break;
            case 2:
                mTabLayout.getTabAt(0).getIcon().setAlpha(50);
                mTabLayout.getTabAt(1).getIcon().setAlpha(50);
                mTabLayout.getTabAt(2).getIcon().setAlpha(255);
                mTabLayout.getTabAt(3).getIcon().setAlpha(50);
                break;
            case 3:
                mTabLayout.getTabAt(0).getIcon().setAlpha(50);
                mTabLayout.getTabAt(1).getIcon().setAlpha(50);
                mTabLayout.getTabAt(2).getIcon().setAlpha(50);
                mTabLayout.getTabAt(3).getIcon().setAlpha(255);
                break;
        }
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
            return 4;
        }
    }
}
