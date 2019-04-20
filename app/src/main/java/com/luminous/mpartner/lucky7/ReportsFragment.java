package com.luminous.mpartner.lucky7;


import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentReportsBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.DealerReportEntity;
import com.luminous.mpartner.network.entities.LsdDealerReport;
import com.luminous.mpartner.utilities.CommonUtility;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ReportsRecyclerViewAdapter adapter;
    private List<LsdDealerReport> dealerReports;
    private Context context;
    private FragmentReportsBinding binding;
    private RetrofitInterface apiInterface;
    private String secretCode = "";
    private String barcode = "";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void setDealerReports(List<LsdDealerReport> dealerReports) {
        this.dealerReports = dealerReports;

        if (dealerReports != null) {
            adapter = new ReportsRecyclerViewAdapter(context, dealerReports);
            binding.recyclerView.setAdapter(adapter);

            binding.tvTotalScannedCards.setText("Total Card Scanned  - " + dealerReports.size());
        }

    }

    public void getDealerReport() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }
        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getDealerReportUrl(secretCode, barcode), context, CheckYourGiftFragment.class.getSimpleName() + ".class");

        apiInterface.getDealerReportData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DealerReportEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(DealerReportEntity dealerReportEntity) {
                        dismissProgressDialog();

                        if (dealerReportEntity.getStatus().equalsIgnoreCase("200")) {
                            setDealerReports(dealerReportEntity.getLsdDealerReport());
                        }else
                            Toast.makeText(getActivity(), dealerReportEntity.getMessage(), Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reports, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);

        getDealerReport();

        binding.tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDealerReport();
            }
        });

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
