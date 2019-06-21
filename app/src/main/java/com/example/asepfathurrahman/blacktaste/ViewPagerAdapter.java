package com.example.asepfathurrahman.blacktaste;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> oneFragment = new ArrayList<>();
    private final List<String> oneTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return oneFragment.get(position);
    }

    @Override
    public int getCount() {
        return oneTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return oneTitles.get(position);
    }

    public void AddFragment (Fragment fragment, String title){

        oneFragment.add(fragment);
        oneTitles.add(title);

    }

}
