package com.luminous.mpartner.gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.luminous.mpartner.network.entities.GalleryDatum;
import com.luminous.mpartner.network.entities.GalleryMenuUpper;

import java.util.List;

public class GalleryViewPagerAdapter extends FragmentStatePagerAdapter {

    int PAGE_COUNT;
    private List<GalleryMenuUpper> galleryDatumList;


    public GalleryViewPagerAdapter(FragmentManager fm, List<GalleryMenuUpper> galleryDatumList) {
        super(fm);
        this.galleryDatumList = galleryDatumList;
        PAGE_COUNT = galleryDatumList.size();
    }

    @Override
    public Fragment getItem(int position) {
        GalleryMenuUpper galleryMenuUpper = galleryDatumList.get(position);
        return GalleryFragment.newInstance(galleryMenuUpper.getGalleryMenuUpperId() + "", galleryMenuUpper.getGalleryMenuUpperName());
    }

    public CharSequence getPageTitle(int position) {
        return galleryDatumList.get(position).getGalleryMenuUpperName();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
