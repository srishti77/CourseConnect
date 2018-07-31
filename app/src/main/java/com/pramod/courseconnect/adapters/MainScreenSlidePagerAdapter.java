package com.pramod.courseconnect.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pramod.courseconnect.fragments.CalendarFragment;
import com.pramod.courseconnect.fragments.EventsListFragment;


public class MainScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;

    public MainScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CalendarFragment();
            case 1:
                return  new EventsListFragment();

            default:
                return new CalendarFragment();
        }

    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
