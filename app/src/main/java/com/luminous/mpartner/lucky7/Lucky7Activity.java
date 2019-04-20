package com.luminous.mpartner.lucky7;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ActivityLucky7Binding;
import com.luminous.mpartner.fragments.BaseFragment;


public class Lucky7Activity extends BaseFragment {


    private Lucky7PagerAdapter adapter;
    private static final int STORAGE_PERMISSION_CODE = 101;
    //    ArrayList<ClaimReport> claimReports;
//    ArrayList<ActivatedCoupon> activatedCouponArrayList;
//    ArrayList<OpenReimbursementCoupon> reimbursementReports;
//    ArrayList<RedeemedCoupon> redeemedReports;
    private String fileName;
    private ActivityLucky7Binding binding;
    private String[] titles;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Lucky7Activity() {
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
    public static Lucky7Activity newInstance(String param1, String param2) {
        Lucky7Activity fragment = new Lucky7Activity();
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


        titles = new String[]{getString(R.string.activate_cards), getString(R.string.scheme_info)};

        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);

        return binding.getRoot();
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new Lucky7PagerAdapter(getChildFragmentManager(), titles);
        viewPager.setAdapter(adapter);
    }

    public void LSD_TermsConditionInfo(Object object) {
//        UserController userController = (UserController) object;
//        Fragment fragment = adapter.getItem(1);
//        ((SchemeInfoFragment) fragment).setUI(userController.termsAndCondition);

    }


    public void LSD_GetClaimReport(Object object) {
//        claimReports = ((UserController) object).claimReports;
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//        } else {
//            AppUtility.convertJsonToCSV(new Gson().toJson(claimReports), this, "claim_reports.csv");
//        }
//        Fragment fragment = adapter.getItem(tabLayout.getSelectedTabPosition());
//        if (fragment instanceof ActivateCardsFragment) {
//            ((ActivateCardsFragment) fragment).setClaimReports(reimbursementReports);
//            ((ActivateCardsFragment) fragment).showSubmitClaimDialog();
//        }
//
//        Log.e("RESPONSE>>>>>>>>>>>>>>", "SUCCESS" + claimReports.size());
    }


    public void LSD_SaveClaimData(Object object) {
//        claimReports = ((UserController) object).claimReports;
//
//        Log.e("RESPONSE>>>>>>>>>>>>>>", "SUBMITTED CLAIM");
    }

    public void LSD_GetActivatedCouponReport(Object object) {
//        activatedCouponArrayList = ((UserController) object).activatedCouponReports;
//        AppUtility.convertJsonToCSV(new Gson().toJson(activatedCouponArrayList), this, "activated_coupons_reports.csv");
//
//        Log.e("RESPONSE>>>>>>>>>>>>>>", "activatedCouponArrayList==" + activatedCouponArrayList.size());
    }

    public void LSD_GetDistOpenReimbursmentReport(Object object) {
//        reimbursementReports = ((UserController) object).reimbursementReports;
//
//        Fragment fragment = adapter.getItem(tabLayout.getSelectedTabPosition());
//        if (fragment instanceof ActivateCardsFragment) {
//            ((ActivateCardsFragment) fragment).setClaimReports(reimbursementReports);
//
//            if (((ActivateCardsFragment) fragment).isDownload) {
//                AppUtility.convertJsonToCSV(new Gson().toJson(reimbursementReports), this, "reimbursementReports.csv");
//            } else {
//                ((ActivateCardsFragment) fragment).showSubmitClaimDialog();
//            }
//        }
//
//        Log.e("RESPONSE>>>>>>>>>>>>>>", "LSD_GetDistOpenReimbursmentReport==" + reimbursementReports.size());
    }

    public void LSD_GetRedeemedReport(Object object) {
//        redeemedReports = ((UserController) object).redeemedReports;
//        AppUtility.convertJsonToCSV(new Gson().toJson(redeemedReports), this, "redeemedReports.csv");
//
//        Log.e("RESPONSE>>>>>>>>>>>>>>", "LSD_GetRedeemedReport==" + redeemedReports.size());
    }


}