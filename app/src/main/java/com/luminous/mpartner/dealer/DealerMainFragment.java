package com.luminous.mpartner.dealer;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentDealerMainBinding;
import com.luminous.mpartner.utilities.CommonUtility;


public class DealerMainFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentDealerMainBinding binding;


    public DealerMainFragment() {
        // Required empty public constructor
    }


    public static DealerMainFragment newInstance(String param1, String param2) {
        DealerMainFragment fragment = new DealerMainFragment();
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dealer_main, container, false);
        binding.btnCreateDealer.setOnClickListener(this);
        binding.btnViewDealer.setOnClickListener(this);
        return binding.getRoot();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCreateDealer:
                loadFragment(CreateDealerFirmAddressDeatailsFragment.newInstance("",""), "CreateDealerFirmAddressDeatailsFragment");
                break;
            case R.id.btnViewDealer:
                loadFragment(ViewDealerListFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate()), "ViewDealerListFragment" );
                break;
        }

    }

    private void loadFragment(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment,tag).commit();
    }


}
