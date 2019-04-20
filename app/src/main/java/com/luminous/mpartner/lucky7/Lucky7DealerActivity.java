package com.luminous.mpartner.lucky7;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ActivityLucky7Binding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.home_page.activities.HomePageActivity;

public class Lucky7DealerActivity extends BaseFragment {

    Lucky7DealerPagerAdapter adapter;
    private ActivityLucky7Binding binding;
    private String[] titles;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Lucky7DealerActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Lucky7DealerActivity newInstance(String param1, String param2) {
        Lucky7DealerActivity fragment = new Lucky7DealerActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_lucky_7, container, false);

        titles = new String[]{getString(R.string.check_your_gift), getString(R.string.reports), getString(R.string.scheme_info)};

        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);

        return binding.getRoot();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Lucky7DealerPagerAdapter(getChildFragmentManager(), titles);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setAdapter(adapter);
    }

    public void LSD_TermsConditionInfo(Object object) {
//        UserController userController = (UserController) object;
//        Fragment fragment = adapter.getItem(2);
//        ((SchemeInfoFragment) fragment).setUI(userController.termsAndCondition);

    }

    public void LSD_SaveDealerScanCode(Object object) {
//        UserController userController = (UserController) object;
//        Fragment fragment = adapter.getItem(0);
//
//        ((CheckYourGiftFragment) fragment).setGiftData(userController);
//
//        Log.e("RESPONSE>>>>>>>>>>>>>>", "LSD_SaveDealerScanCode");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CheckYourGiftFragment.REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Fragment fragment = adapter.getItem(binding.tabs.getSelectedTabPosition());
                    if (fragment instanceof CheckYourGiftFragment) {
                        CheckYourGiftFragment checkYourGiftFragment = (CheckYourGiftFragment) adapter.getItem(binding.tabs.getSelectedTabPosition());
                        checkYourGiftFragment.openScanner();
                    }
                } else {
                    Toast.makeText(getContext(), "Camera Permission Required", Toast.LENGTH_SHORT).show();
                }

            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
