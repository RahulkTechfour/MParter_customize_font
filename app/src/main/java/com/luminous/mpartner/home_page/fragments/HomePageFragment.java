package com.luminous.mpartner.home_page.fragments;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.pdf.parser.Line;
import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.connect.UserController;
import com.luminous.mpartner.customviews.ScaleImageView;
import com.luminous.mpartner.databinding.DialogAlertNotificationBinding;
import com.luminous.mpartner.databinding.FragmentHomePageBinding;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;
import com.luminous.mpartner.dynamic_home.entities.HomeData;
import com.luminous.mpartner.dynamic_home.utils.HomeCardUtils;
import com.luminous.mpartner.events.HomePageVisibleEvent;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.Notification.NotificationReadResponseEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationResponseEntity;
import com.luminous.mpartner.network.entities.Notification.SurveyNotificationAlert;
import com.luminous.mpartner.utilities.CommonUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context context;
    private int count = 0;

    private OnFragmentInteractionListener mListener;
    private FragmentHomePageBinding binding;
    private HomeData homeData;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "HomePageFragment";
    private List<SurveyNotificationAlert> alertList;

    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_page, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        initViews();

        getHomePageData();

        MyApplication.getApplication().bus().send(new HomePageVisibleEvent(true));
        getNotificationAlert();

        return view;
    }

    private void initViews() {
//
        binding.swipeContainer.setOnRefreshListener(this);
        binding.swipeContainer.setNestedScrollingEnabled(true);
        try {
            binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } catch (Exception e) {
            // intentionally left blank
        }
        binding.swipeContainer.setRefreshing(false);

        binding.materialRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.materialRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= 21)
            binding.materialRecyclerView.setNestedScrollingEnabled(true);

        binding.materialRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (binding.materialRecyclerView.getChildAt(0) != null) {
                    binding.swipeContainer.setEnabled(binding.materialRecyclerView.getChildAt(0).getTop() == 0);
                }
            }
        });

        setHomePageUI();
    }

    private void getNotificationAlert() {


        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getNotificationAlert(), getContext(), "HomePageFragment.class");


        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        String response = null;

                        try {
                            response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);


                            NotificationResponseEntity entity = new Gson().fromJson(response, new TypeToken<NotificationResponseEntity>() {
                            }.getType());
                            if (entity != null && entity.getStatus().equals("200")) {
                                alertList = entity.getSurveyNotificationAlert();
                                if (alertList != null && alertList.size() > 0) {
                                    for (int i = 0; i < alertList.size(); i++) {
                                        if (alertList.get(i).getIsread() == false) {
                                            alertImage.add(alertList.get(i).getImagepath());
                                            alertId.add(alertList.get(i).getId());
                                        }
                                    }

                                    if (alertImage.size() > 0)
                                        inflateAlertDialog();
                                }

                            }

                        } catch (Exception e) {
                            Log.d(TAG, "onNext: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    ArrayList<String> alertImage = new ArrayList<String>();
    ArrayList<Integer> alertId = new ArrayList<Integer>();

    private void inflateAlertDialog() {

        try {
            //Custom Dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_alert_notification);

            final ScaleImageView image = (ScaleImageView) dialog.findViewById(R.id.image);
            Glide.with(context).load(alertImage.get(count)).apply(new RequestOptions()
                    .placeholder(R.drawable.alert_animation)
            ).into(image);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertRead(alertList.get(count).getId() + "");
                    dialog.dismiss();
                    count = count + 1;
                    if (count < alertImage.size())
                        inflateAlertDialog();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void alertRead(String nid) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getNotificationRead(nid), getContext(), "HomePageFragment.class");
        Log.d(TAG, "alertRead: " + url);
        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        String response = "";
                        try {
                            response = responseBody.string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        NotificationReadResponseEntity entity = new Gson().fromJson(response, new TypeToken<NotificationReadResponseEntity>() {
                        }.getType());
                        if (entity != null) {
                            if (entity.getStatus().equals("200")) {
                                Log.d(TAG, "onNext: checked alert");
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }

    private void setHomePageUI() {
        if (homeData == null) {
            homeData = new Gson().fromJson(loadJSONFromAsset(), HomeData.class);
        }
        setAdapter(homeData.getDynamicHomePage());
    }

    private void setAdapter(List<HomeCardEntity> dynamicHomePage) {

        if (!binding.materialRecyclerView.getAdapter().isEmpty()) {
            binding.materialRecyclerView.getAdapter().clear();
            binding.materialRecyclerView.getAdapter().notifyDataSetChanged();
        }

        binding.materialRecyclerView.getAdapter().addAll(HomeCardUtils.getHomePageCards(context, dynamicHomePage));
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("home_options.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
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

    @Override
    public void onRefresh() {
        if (CommonUtility.isNetworkAvailable(context)) {
            binding.swipeContainer.setRefreshing(true);
            getHomePageData();
        } else {
            binding.swipeContainer.setRefreshing(false);
        }
    }

    //Api Call
    private void getHomePageData() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }
        binding.swipeContainer.setRefreshing(false);

        String url = ServerConfig.newUrl(ServerConfig.getHomePageDataUrl(), context, HomePageFragment.class.getSimpleName() + ".class");

        apiInterface.getHomePageData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(HomeData homeDataResponse) {
                        if (homeDataResponse.getStatus().equalsIgnoreCase("200")) {
                            homeData = homeDataResponse;
                            setHomePageUI();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.swipeContainer.setRefreshing(false);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        binding.swipeContainer.setRefreshing(false);
                        CommonUtility.printLog("HomeData", "onComplete");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyApplication.getApplication().bus().send(new HomePageVisibleEvent(false));

    }
}
