package com.luminous.mpartner.connect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SchemeStatusAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public SchemeStatusAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SchemeStatusFragment tab1 = new SchemeStatusFragment();
                return tab1;
            /*case 1:
                ReportsFragment tab2 = noti ReportsFragment();
                return tab2;*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}