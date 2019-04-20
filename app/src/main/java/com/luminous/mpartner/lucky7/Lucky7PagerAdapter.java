package com.luminous.mpartner.lucky7;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Lucky7PagerAdapter extends FragmentStatePagerAdapter {

    int PAGE_COUNT;
    private String[] titles;


    public Lucky7PagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
        PAGE_COUNT = titles.length;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ActivateCardsFragment.newInstance("", "");
            case 1:
                return SchemeInfoFragment.newInstance("", "");
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
