package com.luminous.mpartner.pricelist;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentPriceListTabBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.PriceListHeaderEntity;
import com.luminous.mpartner.utilities.CommonUtility;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PriceListTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PriceListTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PriceListTabFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentPriceListTabBinding binding;
    private int position = 0;
    private String titles[];
    private int categoryId[];
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public PriceListTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PriceListTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PriceListTabFragment newInstance(String param1, String param2) {
        PriceListTabFragment fragment = new PriceListTabFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_price_list_tab, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        if (CommonUtility.isNetworkAvailable(context)) {
            getPriceListHeaderData();
        }
        return view;
    }

    private void getPriceListHeaderData() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getPriceListHeaderURL(), context, PriceFragment.class.getSimpleName() + ".class");
        showProgressDialog();

        //TODO Garvit
//        url = "http://166.62.100.102/Api/MpartnerApi/ParentCategory?user_id=9999998&token=9714e77c48ecc0a5c6cb5d50c9ae68d7&app_version=3.9&device_id=3c83daae90bfe9d5&device_name=Oppo&os_type=Android&os_version_name=Noughat&os_version_code=5.1.1&ip_address=192.98.89&language=EN&screen_name=A.class&network_type=2g&network_operator=Airtel&time_captured=123213123123123&channel=M";

        apiInterface.getPriceListHeader(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PriceListHeaderEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(PriceListHeaderEntity priceListHeaderEntity) {
                        dismissProgressDialog();
                        if (priceListHeaderEntity.getStatus().equalsIgnoreCase("200")) {

                            if (priceListHeaderEntity.getParentCategory() != null) {
                                titles = new String[priceListHeaderEntity.getParentCategory().size()];
                                categoryId = new int[priceListHeaderEntity.getParentCategory().size()];
                                for (int i = 0; i < priceListHeaderEntity.getParentCategory().size(); i++) {
                                    titles[i] = priceListHeaderEntity.getParentCategory().get(i).getParentcategoryname();
                                    categoryId[i] = priceListHeaderEntity.getParentCategory().get(i).getId();
                                }
                                initViews();
                                setupTab(position);
                            }
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

    private void initViews() {
        binding.viewPager.setAdapter(new PriceListViewPagerAdapter(getFragmentManager(), titles, categoryId));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.tabLayout.addOnTabSelectedListener(this);
        binding.viewPager.setCurrentItem(position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    private void setupTab(int pos) {
        int tabSize = binding.tabLayout.getTabCount();
        for (int i = 0; i < tabSize; i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);

            RelativeLayout relativeLayout1 = (RelativeLayout)
                    LayoutInflater.from(context).inflate(R.layout.custom_gallery_tab, null, false);
            RelativeLayout relativeLayout = relativeLayout1.findViewById(R.id.relativeLayout);
            TextView tv = relativeLayout1.findViewById(R.id.textView);

            tv.setText(titles[i]);

            if (pos == i) {
                tv.setTextColor(getResources().getColor(R.color.white));
                relativeLayout.setBackground(context.getResources().getDrawable(R.color.tab_blue));

            } else {
                tv.setTextColor(getResources().getColor(R.color.tab_blue));
                relativeLayout.setBackground(context.getResources().getDrawable(R.color.light_gray));
            }

            tab.setCustomView(relativeLayout);
        }
    }

    private void updateTab(int pos) {
        int tabSize = binding.tabLayout.getTabCount();
        for (int i = 0; i < tabSize; i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            RelativeLayout relativeLayout1 = ((RelativeLayout) tab.getCustomView());
            if (relativeLayout1 != null) {
                TextView tv = relativeLayout1.findViewById(R.id.textView);
                RelativeLayout relativeLayout = relativeLayout1.findViewById(R.id.relativeLayout);

                if (pos == i) {
                    tv.setTextColor(getResources().getColor(R.color.white));
                    relativeLayout.setBackground(context.getResources().getDrawable(R.color.tab_blue));

                } else {
                    tv.setTextColor(getResources().getColor(R.color.tab_blue));
                    relativeLayout.setBackground(context.getResources().getDrawable(R.color.light_gray));
                }
            }
        }
        position = pos;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateTab(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
