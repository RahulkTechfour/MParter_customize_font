package com.luminous.mpartner.sales;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentSalesMainBinding;
import com.luminous.mpartner.dealer.CreateDealerFirmAddressDeatailsFragment;
import com.luminous.mpartner.dealer.ViewDealerListFragment;
import com.luminous.mpartner.utilities.CommonUtility;


public class SalesMainFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FragmentSalesMainBinding binding;

    public SalesMainFragment() {
    }

    public static SalesMainFragment newInstance(String param1, String param2) {
        SalesMainFragment fragment = new SalesMainFragment();
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
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sales_main, container, false);
        binding.btnCreateOrder.setOnClickListener(this);
        binding.btnViewOrder.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCreateOrder:
                loadFragment(CreateSalesOrderFragment.newInstance("",""), "CreateSalesOrderFragment");
                break;
            case R.id.btnViewOrder:
                loadFragment(SalesViewOrderFragment.newInstance(/*from*/CommonUtility.getPastSeventhDate(),/*to*/CommonUtility.getTodayDate()), "SalesViewOrderFragment");
                break;
        }

    }

    private void loadFragment(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, tag).commit();
    }

}
