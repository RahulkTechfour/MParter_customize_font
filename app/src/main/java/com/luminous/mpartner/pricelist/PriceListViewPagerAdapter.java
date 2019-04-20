package com.luminous.mpartner.pricelist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PriceListViewPagerAdapter extends FragmentStatePagerAdapter {

    int PAGE_COUNT;
    private String titles[];
    private int[] categoryId;

    public PriceListViewPagerAdapter(FragmentManager fm, String[] titles2, int[] categoryId) {
        super(fm);
        titles = titles2;
        this.categoryId = categoryId;
        PAGE_COUNT = titles2.length;
    }

    @Override
    public Fragment getItem(int position) {
        return PriceFragment.newInstance(titles[position], categoryId[position]);
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
