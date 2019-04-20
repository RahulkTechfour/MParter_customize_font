package com.luminous.mpartner.reports;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ActivityReportBinding;
import com.luminous.mpartner.reports.fragments.CreditDebitFragment;
import com.luminous.mpartner.reports.fragments.CustomerLedgerFragment;
import com.luminous.mpartner.reports.fragments.PrimaryBillingReportFragment;
import com.luminous.mpartner.reports.fragments.PrimaryReportFragment;
import com.luminous.mpartner.reports.fragments.ProductReconciliationFragment;
import com.luminous.mpartner.reports.fragments.SecondaryDispatchReportFragment;
import com.luminous.mpartner.reports.fragments.SecondarySalesFragment;
import com.luminous.mpartner.reports.fragments.WarrantyReport.WarrantyRegistrationFragment;
import com.luminous.mpartner.utilities.CommonUtility;

public class ReportFragment extends Fragment {

    ActivityReportBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ReportFragment(){

    }
    public static ReportFragment newInstance(String param1, String param2) {
        com.luminous.mpartner.reports.ReportFragment fragment = new com.luminous.mpartner.reports.ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_report, container, false);
        View view = binding.getRoot();

        String[] reportList = {"Primary Billing Report", "Current Available Stocks", "Secondary Dispatch Report",
                "Product Reconciliation", "Customer Ledger Report", "Credit Debit Report", "Warranty Report"/*, "Primary Report"*/};
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, reportList);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerReports.setAdapter(spinnerAdapter);

        PrimaryReportFragment fragment = PrimaryReportFragment.newInstance("", "");
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();


        binding.spinnerReports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                loadReport(reportList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void loadReport(String s) {

        /*if (s.equals("Primary Report")) {
            PrimaryReportFragment fragment = PrimaryReportFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else */
        if (s.equals("Current Available Stocks")) {
            SecondarySalesFragment fragment = SecondarySalesFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if (s.equals("Secondary Dispatch Report")) {
            SecondaryDispatchReportFragment fragment = SecondaryDispatchReportFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if (s.equals("Product Reconciliation")) {
            ProductReconciliationFragment fragment = ProductReconciliationFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if (s.equals("Customer Ledger Report")) {
            CustomerLedgerFragment fragment = CustomerLedgerFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if (s.equals("Credit Debit Report")) {
            CreditDebitFragment fragment = CreditDebitFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if (s.equals("Primary Billing Report")) {
            PrimaryBillingReportFragment fragment = PrimaryBillingReportFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if(s.equals("Warranty Report")){
            WarrantyRegistrationFragment fragment = WarrantyRegistrationFragment.newInstance("","");
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        }

    }

}
