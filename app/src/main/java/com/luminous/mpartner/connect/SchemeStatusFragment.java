package com.luminous.mpartner.connect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.SchemesDropdownAdapter;

import java.util.ArrayList;

public class SchemeStatusFragment extends Fragment {

    Boolean visible = false;
    View rootView;
    private Spinner spSchemes;
    private ArrayList<Scheme> mSchemes;
    public String mSelectedScheme = "";

    private static SchemeStatusFragment instance;

    public SchemeStatusFragment() {
        // Required empty public constructor
    }

    public static SchemeStatusFragment getInstance() {
        // instance = noti InverlastFragment();
        return instance;
    }

    public static SchemeStatusFragment newInstance(String title) {
        SchemeStatusFragment fragment = new SchemeStatusFragment();

        Bundle args = new Bundle();
        args.putCharSequence("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean menuVisible) {
        if (menuVisible)
            visible = true;
        else
            visible = false;

        super.setUserVisibleHint(menuVisible);
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_scheme_status, container, false);
        instance = this;
        prepareView();
        return rootView;
    }

    public void prepareView(){
        spSchemes = (Spinner)rootView.findViewById(R.id.fragment_reports_sp_schemes);
        getSchemes();
    }

    public void getSchemes() {
        UserController userController = new UserController(getActivity(), "updateSchemes");
        userController.getSchemes();
    }

    public void updateSchemes(Object object) {
        mSchemes = ((UserController) object).mSchemeList;
        if (mSchemes != null) {
            spSchemes.setAdapter(new SchemesDropdownAdapter(getActivity(), mSchemes));
            spSchemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedScheme = position > 0 ? mSchemes.get(position - 1).schemeName : "";
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
