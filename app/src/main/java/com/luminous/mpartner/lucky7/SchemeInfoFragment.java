package com.luminous.mpartner.lucky7;


import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentSchemeInfoBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.utilities.AppConstants;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SchemeInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchemeInfoFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentSchemeInfoBinding binding;
    private SharedPrefsManager sharedPrefsManager;
    private TextView mTxtSchemeName;
    private ImageView mImgScheme;

    public SchemeInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchemeInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchemeInfoFragment newInstance(String param1, String param2) {
        SchemeInfoFragment fragment = new SchemeInfoFragment();
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scheme_info, container, false);
        View view = binding.getRoot();
        mImgScheme = view.findViewById(R.id.image_scheme);
        mTxtSchemeName = view.findViewById(R.id.txt_scheme_name);
        sharedPrefsManager = new SharedPrefsManager(getContext());

        getTermsAndConditions();
        return view;
    }



    public void setUI(/*TermsAndConditions termsAndConditions*/) {
//
//        if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
//            binding.txtDistributor.setVisibility(View.VISIBLE);
//            binding.txtDealer.setVisibility(View.GONE);
//            binding.txtDistributor.setText(termsAndConditions.getSchemeinfo());
//        } else {
//            binding.txtDistributor.setVisibility(View.GONE);
//            binding.txtDealer.setVisibility(View.VISIBLE);
//            binding.txtDealer.setText(termsAndConditions.getSchemeinfo());
//        }
//
//        Glide.with(getActivity()).load(termsAndConditions.getImage()).into(binding.image);
    }

    public void getTermsAndConditions() {
//
        try {
            if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
                mTxtSchemeName.setText("");
                mImgScheme.setImageResource(R.drawable.scheme_dist);


            } else {
                mTxtSchemeName.setText("");
                mImgScheme.setImageResource(R.drawable.scheme_dealer);
            }
        }catch(Exception ex){
            ex.getMessage();
        }

    }
}
