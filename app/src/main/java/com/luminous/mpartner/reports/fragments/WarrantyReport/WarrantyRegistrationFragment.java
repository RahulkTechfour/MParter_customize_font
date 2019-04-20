package com.luminous.mpartner.reports.fragments.WarrantyReport;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentWarrantyRegistrationBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WarrantyRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WarrantyRegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public WarrantyRegistrationFragment() {
        // Required empty public constructor
    }


    public static WarrantyRegistrationFragment newInstance(String param1, String param2) {
        WarrantyRegistrationFragment fragment = new WarrantyRegistrationFragment();
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
    Fragment fragment = null;
    String tag = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentWarrantyRegistrationBinding binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_warranty_registration, container, false);

        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (checkedId == R.id.rbDealer){
                    fragment = DealerWRFragment.newInstance("","");
                    tag = "DealerWRFragment";
                } else if (checkedId == R.id.rbDist){
                    fragment = DistributorWRFragment.newInstance("","");
                    tag = "DistributorWRFragment";
                }
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment, tag).commit();
            }
        });


        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, DistributorWRFragment.newInstance("",""), "DistributorWRFragment").commit();

        return binding.getRoot();
    }

}
