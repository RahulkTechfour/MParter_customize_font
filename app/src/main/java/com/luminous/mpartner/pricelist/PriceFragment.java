package com.luminous.mpartner.pricelist;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentPriceBinding;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;
import com.luminous.mpartner.dynamic_home.entities.PriceData;
import com.luminous.mpartner.dynamic_home.utils.HomeCardUtils;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.utilities.CommonUtility;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PriceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PriceFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private int categoryId;

    private OnFragmentInteractionListener mListener;
    private FragmentPriceBinding binding;
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public PriceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PriceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PriceFragment newInstance(String param1, int param2) {
        PriceFragment fragment = new PriceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            categoryId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_price, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        initViews();

        if (CommonUtility.isNetworkAvailable(context)) {
            getPriceListData();
        }
        return view;
    }

    private void initViews() {
//
        binding.materialRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.materialRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= 21)
            binding.materialRecyclerView.setNestedScrollingEnabled(true);
    }

    private void setPricePageUI(PriceData priceData) {
        setAdapter(priceData.getDynamicHomePage());
    }

    private void setAdapter(List<HomeCardEntity> dynamicHomePage) {

        if (!binding.materialRecyclerView.getAdapter().isEmpty()) {
            binding.materialRecyclerView.getAdapter().clear();
            binding.materialRecyclerView.getAdapter().notifyDataSetChanged();
        }

        binding.materialRecyclerView.getAdapter().addAll(HomeCardUtils.getHomePageCards(context, dynamicHomePage));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PriceFragment.OnFragmentInteractionListener) {
            mListener = (PriceFragment.OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }

        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


    //Api Call
    private void getPriceListData() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        String url = ServerConfig.newUrl(String.format(ServerConfig.getPriceListPageDataUrl(), categoryId), context, PriceFragment.class.getSimpleName() + ".class");
        showProgressDialog();

        /*TODO : remove*/
//        url = "http://166.62.100.102/Api/MpartnerApi/pricelist_page_cards?token=9714e77c48ecc0a5c6cb5d50c9ae68d7&user_id=9999998&parentid=1&app_version=3.9&appversion_code=28&device_id=3c83daae90bfe9d5&device_name=iphone5&os_type=iOS&os_version_name=3.2.0&os_version_code=12&ip_address=166.62.100.102&language=EN&screen_name=dashboard&network_type=2G&network_operator=Airtel&time_captured=1550484272&channel=M";
        apiInterface.getPriceListData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PriceData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(PriceData priceDataResponse) {
                        dismissProgressDialog();
                        if (priceDataResponse.getStatus().equalsIgnoreCase("200")) {
                            setPricePageUI(priceDataResponse);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("HomeData", "onComplete");
                    }
                });
    }
}
