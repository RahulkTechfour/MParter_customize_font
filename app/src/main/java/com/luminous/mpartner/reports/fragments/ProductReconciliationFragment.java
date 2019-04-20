package com.luminous.mpartner.reports.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentProductReconciliationBinding;
import com.luminous.mpartner.databinding.FragmentSecondaryDispatchReportBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ItemListEntity;
import com.luminous.mpartner.network.entities.ProductReconciliationReportEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;


public class ProductReconciliationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int STORAGE_PERMISSION_CODE_3 = 1;

    private String mParam1;
    private String mParam2;
    private static final String TAG = "ProductReconciliation";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RetrofitInterface apiInterface;
    private FragmentProductReconciliationBinding binding;
    SharedPrefsManager manager;
    private String primaryReportString;


    public ProductReconciliationFragment() {
    }


    public static ProductReconciliationFragment newInstance(String param1, String param2) {
        ProductReconciliationFragment fragment = new ProductReconciliationFragment();
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

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        manager = new SharedPrefsManager(getContext());

    }

    private void getProductReconciliationReport(String serialNumber, String disCode) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        binding.llProgressBar.setVisibility(View.VISIBLE);

        /* serialNumber = "k2h333l1006336";*/
        //disCode = "9999998";
        String url1 = ServerConfig.getProdcutReconciliationUrl(disCode, serialNumber);

        Log.d(TAG, "url : " + url1);
        apiInterface.getProductReconciliationReport(url1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {
                            List<ProductReconciliationReportEntity> list = new Gson().fromJson(responseBody.string(), new TypeToken<List<ProductReconciliationReportEntity>>() {
                            }.getType());

                            binding.llPRR.setVisibility(View.VISIBLE);

                            if (list.size() >= 1) {

                                ProductReconciliationReportEntity entity = list.get(0);

                                if (entity != null) {

                                    showLLHideNodata();
                                    binding.tvModelName.setText("Product name : " + entity.getModelName());
                                    binding.tvPbd.setText("Billing Date : " + entity.getPrimaryBilledDate());
                                    binding.tvPc.setText("Primart Customer : " + entity.getPrimaryCustomer());
                                    binding.tvProductType.setText("Product Type : " + entity.getProductType());

                                    binding.tvSbd.setText("Billing Date : " + entity.getSecBilledDate());
                                    binding.tvSDealerName.setText("Dealer Name : " + entity.getDealerName());

                                    binding.tvTCNumber.setText("Customer Number : " + entity.getCustomerNo());
                                    binding.tvCity.setText("City : " + entity.getCity());
                                    binding.tvTCustomerName.setText("Customer Name : " + entity.getCustomerName());
                                    binding.tvTDate.setText("Date : " + entity.getTertiaryDate());

                                }
                            } else {
                                hideLLShowNoData();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "onError: " + e.getMessage());
                        hideLLShowNoData();
                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void hideLLShowNoData() {
        binding.tvNoDataFound.setVisibility(View.VISIBLE);
        binding.llPRR.setVisibility(View.GONE);

    }

    private void showLLHideNodata() {
        binding.tvNoDataFound.setVisibility(View.GONE);
        binding.llPRR.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_reconciliation, container, false);
        View view = binding.getRoot();


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.edtSerialNumber.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Enter Serial number", Toast.LENGTH_SHORT).show();
                } else {
                    getProductReconciliationReport(binding.edtSerialNumber.getText().toString(), manager.getString(SharedPreferenceKeys.USER_ID));
                }

            }
        });

        binding.edtSerialNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (binding.edtSerialNumber.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Enter Serial number", Toast.LENGTH_SHORT).show();
                    } else {
                        getProductReconciliationReport(binding.edtSerialNumber.getText().toString(), manager.getString(SharedPreferenceKeys.USER_ID));
                    }
                    return true;
                }
                return false;
            }
        });

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.edtSerialNumber.setText("");
                binding.llPRR.setVisibility(View.GONE);

            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
