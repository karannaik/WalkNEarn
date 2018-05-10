package com.androiders.walknearn.fitbitfiles;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.androiders.walknearn.fitbitfiles.fragments.ActivitiesFragment;
import com.androiders.walknearn.fitbitfiles.fragments.DeviceFragment;
import com.androiders.walknearn.fitbitfiles.fragments.InfoFragment;
import com.androiders.walknearn.fitbitfiles.fragments.ProfileFragment;
import com.androiders.walknearn.fitbitfiles.fragments.WeightLogFragment;
import com.fitbit.authentication.AuthenticationManager;
import com.fitbit.authentication.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboggess on 10/17/16.
 */

public class UserDataPagerAdapter extends FragmentPagerAdapter {

    private final List<InfoFragment> fragments = new ArrayList<>();

    public UserDataPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        if (containsScope(Scope.profile)) {
            fragments.add(new ProfileFragment());
        }
        if (containsScope(Scope.settings)) {
            fragments.add(new DeviceFragment());
        }
        if (containsScope(Scope.activity)) {
            fragments.add(new ActivitiesFragment());
        }
        if (containsScope(Scope.weight)) {
            fragments.add(new WeightLogFragment());
        }
    }

    private boolean containsScope(Scope scope) {
        return AuthenticationManager.getCurrentAccessToken().getScopes().contains(scope);
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= fragments.size()) {
            return null;
        }

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public int getTitleResourceId(int index) {
        return fragments.get(index).getTitleResourceId();
    }
}
