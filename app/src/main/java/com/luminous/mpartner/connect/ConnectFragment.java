package com.luminous.mpartner.connect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.SlabsAchievedRecyclerViewAdapter;
import com.luminous.mpartner.databinding.FragmentConnectBinding;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;
import com.luminous.mpartner.dynamic_home.entities.HomeData;
import com.luminous.mpartner.dynamic_home.utils.HomeCardUtils;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.SchemePointSlabEntity;
import com.luminous.mpartner.network.entities.SerWRReportConnect2CardRewardPonitsEntity;
import com.luminous.mpartner.network.entities.StrSerWRReportConnectRejectionPerEntity;
import com.luminous.mpartner.network.entities.WRStatusReportCardEntity;
import com.luminous.mpartner.reports.fragments.PrimaryReportFragment;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
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
 * {@link ConnectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFragment extends BaseFragment implements OnRecyclerViewItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentConnectBinding binding;
    private SlabsAchievedRecyclerViewAdapter slabsAchievedRecyclerViewAdapter;
    private ConnectSlabsAchievedRecyclerViewAdapter slabsAchievedRecyclerViewAdapter1;
    private ConnectRecyclerViewAdapter connectRecyclerViewAdapter;
    private Context context;
    private int progress = 0;
    private Handler handler = new Handler();
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SharedPrefsManager sharedPrefsManager;
    private String userId = "", userType = "";
    private static final String TAG = "ConnectFragment";

    public ConnectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectFragment newInstance(String param1, String param2) {
        ConnectFragment fragment = new ConnectFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        //updateRewardUI();
        //initRecyclerView();
        //getSchemePointSlab();
        setProgressBarUI(0, 0);

        initViews();

        // show disclaimer dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Disclaimer");
        builder.setMessage("Points in Connect+ report are subject to change. Points will be deducted if Customer details are found inaccurate in the verification.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();

        //

        sharedPrefsManager = new SharedPrefsManager(context);
        userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        userType = sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE);

        getConnectPlusCardData();
        SerWRReportConnect2_Card_Reward_Ponits();
        SStrSerWRReportConnect_Rejection_per();
        getWRStatusReportCard();
//        SerWRReportConnect_Card();

        binding.llRewardSlab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.llRewardSlab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                binding.llRewardSlab.getHeight(); //height is ready
            }
        });

        binding.btnGetAssistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initViews() {

        binding.ivScan.setOnClickListener(v -> {

            Intent intent = new Intent(context, WRSStatusNewActivity.class);
            startActivity(intent);

        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initRecyclerView(ArrayList<WRStatusReportCardEntity> arrayList) {

        binding.materialRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.materialRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= 21)
            binding.materialRecyclerView.setNestedScrollingEnabled(true);


//        connectRecyclerViewAdapter = new ConnectRecyclerViewAdapter(getContext(), null, this);

        if (arrayList != null && arrayList.size() > 0) {
            slabsAchievedRecyclerViewAdapter = new SlabsAchievedRecyclerViewAdapter(getContext(), arrayList);
            binding.recyclerView.setAdapter(slabsAchievedRecyclerViewAdapter);
            binding.reportLayout.setVisibility(View.VISIBLE);
        } else {
            binding.reportLayout.setVisibility(View.GONE);
        }
//        binding.imageRecyclerView.setAdapter(connectRecyclerViewAdapter);
    }

    private void getWRStatusReportCard() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        String url = ServerConfig.getWRStatusReportCard(userId, userType);
//       url = "http://mpartner.luminousintranet.com:81//nonsapservices/api/nonsapservice/SerWRReportConnect_Card?dist_code=1000000891&Scheme=connect%20plus%201&user_type=dlr&token=pass@1234";

        apiInterface.getWRStatusReportCard(url)
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
                            ArrayList<WRStatusReportCardEntity> list = new Gson().fromJson(response,
                                    new TypeToken<ArrayList<WRStatusReportCardEntity>>() {
                                    }.getType());

                            if (list != null && list.size() > 0) {
                                initRecyclerView(list);

                            } else {
                                Log.d("Connect+", "onNext: null");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
//                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });

    }

    private void getSchemePointSlab() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        String url = /*ServerConfig.getWRStatusReportCard("1000000891","dlr")*/ "";
        url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/nonsapservice/" +
                "SerWRReportConnect2_Card_Reward_Ponits?dist_code=1000000891&Scheme=connect%20plus%202&user_type=Test&token=pass@1234\n";


        apiInterface.getSchemePointSlab(url)
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
                            Log.d(TAG, "onNext: " + response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ArrayList<SchemePointSlabEntity> list = new Gson().fromJson(response,
                                new TypeToken<ArrayList<SchemePointSlabEntity>>() {
                                }.getType());

                        if (list != null) {
                            Log.d("Connect+", "onNext: not null");
                        } else {
                            Log.d("Connect+", "onNext: null");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }


    private void getConnectPlusCardData() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }



        String url = ServerConfig.newUrl(ServerConfig.getConnectCards(), context, PrimaryReportFragment.class.getSimpleName());
        apiInterface.getConnectCardData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(HomeData homeData) {
                        dismissProgressDialog();
                        if (homeData.getStatus().equalsIgnoreCase("200")) {

                            setAdapter(homeData.getConnectplus_data());
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
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }

    private void setAdapter(List<HomeCardEntity> dynamicHomePage) {
        if (!binding.materialRecyclerView.getAdapter().isEmpty()) {
            binding.materialRecyclerView.getAdapter().clear();
            binding.materialRecyclerView.getAdapter().notifyDataSetChanged();
        }

        binding.materialRecyclerView.getAdapter().addAll(HomeCardUtils.getHomePageCards(context, dynamicHomePage));
    }

    private void setImageViewPagerAdapter() {
//        binding.viewpager.setAdapter(new ConnectImagePagerAdapter(getContext(), null));
//        binding.viewpager.setCurrentItem(0);
//        binding.indicator.setViewPager(binding.viewpager);
    }

    private void setProgressBarUI(int primary, int secondary) {

        Resources res = getResources();
        Drawable drawableYellow = res.getDrawable(R.drawable.circular_yellow);
        binding.primaryProgressBar.setProgress(0);
        binding.primaryProgressBar.setSecondaryProgress(100); // Secondary Progress
        binding.primaryProgressBar.setMax(100); // Maximum Progress
        binding.primaryProgressBar.setProgressDrawable(drawableYellow);

        Drawable drawableOrange = res.getDrawable(R.drawable.circular_orange);
        binding.secondaryProgressBar.setProgress(0);
        binding.secondaryProgressBar.setSecondaryProgress(100); // Secondary Progress
        binding.secondaryProgressBar.setMax(100); // Maximum Progress
        binding.secondaryProgressBar.setProgressDrawable(drawableOrange);


//        new Thread(() -> {
//            // TODO Auto-generated method stub
//            while (progress < 45) {
//                progress += 1;
//
//                handler.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        binding.primaryProgressBar.setProgress(progress);
//                        binding.primaryProgressTv.setText(progress + "%");
//
//                        binding.secondaryProgressBar.setProgress(progress);
//                        binding.secondaryProgressTv.setText(progress + "%");
//                    }
//                });
//                try {
//                    // Sleep for 200 milliseconds.
//                    // Just to display the progress slowly
//                    Thread.sleep(16); //thread will take approx 3 seconds to finish
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

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
    public void onItemCLicked(View view, int position) {

    }

    @Override
    public void onItemCLicked(View view, int position, String source) {

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

    List<SerWRReportConnect2CardRewardPonitsEntity> cardRewardPointsList;

    private void SerWRReportConnect2_Card_Reward_Ponits() {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.SerWRReportConnect2_Card_Reward_Ponits(userId, userType);

        apiInterface.SerWRReportConnect2_Card_Reward_Ponits(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();
                        Log.d(TAG, "onNext");

                        try {
                            String data = response.string();
                            Log.d(TAG, "onNext: " + data);

                            cardRewardPointsList = new Gson().fromJson(data, new TypeToken<List<SerWRReportConnect2CardRewardPonitsEntity>>() {
                            }.getType());

                            Log.d(TAG, "onNext: " + cardRewardPointsList.size());

                            if (cardRewardPointsList != null && cardRewardPointsList.size() > 0 && !cardRewardPointsList.get(0).getCurrentSlab().isEmpty()) {
                                List<SerWRReportConnect2CardRewardPonitsEntity> list = new ArrayList<>();
                                for (int i = 0; i < cardRewardPointsList.size(); i++) {
                                    if (cardRewardPointsList.get(i).getPointNextSlab().equalsIgnoreCase("Slab"))
                                        list.add(cardRewardPointsList.get(i));
                                }

                                slabsAchievedRecyclerViewAdapter1 = new ConnectSlabsAchievedRecyclerViewAdapter(getContext(), list);
                                binding.slabsRecyclerView.setAdapter(slabsAchievedRecyclerViewAdapter1);
                                slabsAchievedRecyclerViewAdapter1.notifyDataSetChanged();

                                updateRewardUI();
                                if (list.size() > 0)
                                    binding.slabeAchievedLayout.setVisibility(View.VISIBLE);
                                else
                                    binding.slabeAchievedLayout.setVisibility(View.GONE);
                            } else {
                                binding.slabeAchievedLayout.setVisibility(View.GONE);
                            }
//                            inflateTrackUI(cardRewardPointsList);

                        } catch (IOException e) {
                            Log.d(TAG, "onNext: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Log.d(TAG, "onError: " + e.getMessage());
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }


    private void SStrSerWRReportConnect_Rejection_per() {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.SStrSerWRReportConnect_Rejection_per(userId, userType);

        apiInterface.SStrSerWRReportConnect_Rejection_per(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();

                        try {
                            String data = response.string();
                            JSONArray jsonArray = new JSONArray(data);
                            StrSerWRReportConnectRejectionPerEntity entity = new Gson().fromJson(jsonArray.getString(0), StrSerWRReportConnectRejectionPerEntity.class);
                            if (entity != null) {
                                binding.llRejectionRateComparison.setVisibility(View.VISIBLE);
                                binding.primaryProgressBar.setProgress(Integer.parseInt(entity.getTotal()));
                                binding.secondaryProgressBar.setProgress(Integer.parseInt(entity.getMy()));
                                binding.primaryProgressTv.setText(entity.getTotal() + "%");
                                binding.secondaryProgressTv.setText(entity.getMy() + "%");
                            } else
                                binding.llRejectionRateComparison.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        binding.llRejectionRateComparison.setVisibility(View.GONE);
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }


    private void updateRewardUI() {

        int manPosition = -1;

        for (int i = 0; i < cardRewardPointsList.size(); i++) {
            if (cardRewardPointsList.get(i).getPointNextSlab().equalsIgnoreCase("")) {
                manPosition = i;
                break;
            }
        }
        ConnectRewardPointAdapter adapter = new ConnectRewardPointAdapter(getContext(), cardRewardPointsList, manPosition);
        binding.pointRecycler.setAdapter(adapter);

//        SerWRReportConnect2CardRewardPonitsEntity currentSlab = null;
//        SerWRReportConnect2CardRewardPonitsEntity finalSlab = null;
//        SerWRReportConnect2CardRewardPonitsEntity previousToCurrent = null;
//
//        ImageButton[] iBs = {binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5, binding.btn6};
//
//        finalSlab = cardRewardPointsList.get(cardRewardPointsList.size() - 1);
//        int count = 0;
//
//        for (SerWRReportConnect2CardRewardPonitsEntity entity : cardRewardPointsList) {
//
//            if (entity.getCurrentSlab().equals("current")) {
//                currentSlab = entity;
//                Log.d(TAG, "points Earned " + currentSlab.getPointsEarned());
//                break;
//            } else
//                count++;
//        }
//
//
//        int ptcValue = 0;
//        int ntcValue = 0;
//        if (count != 0) {
//            previousToCurrent = cardRewardPointsList.get(count - 1);
//            ptcValue = Integer.parseInt(previousToCurrent.getPointsEarned());
//        }
//
//        int curValue = Integer.parseInt(currentSlab.getPointsEarned());
//
//        if (count + 1 <= cardRewardPointsList.size() - 1)
//            ntcValue = Integer.parseInt(cardRewardPointsList.get(count + 1).getPointsEarned());
//        else
//            ntcValue = Integer.parseInt(finalSlab.getPointsEarned());
//
//        int range = ntcValue - ptcValue;
//        int distanceFromPreviousValue = curValue - ptcValue;
//
//        int percentageCovered = (distanceFromPreviousValue * 100) / range;
//
//        Log.d(TAG, "count: " + count);
//        Log.d(TAG, "ptcValue: " + ptcValue);
//        Log.d(TAG, "ntcValue: " + ntcValue);
//        Log.d(TAG, "range: " + range);
//        Log.d(TAG, "percentageCovered: " + percentageCovered);
//
//        ImageButton current = new ImageButton(getContext());
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(binding.btn1.getWidth() + 25, binding.btn1.getWidth() + 25);
//
//        int x = binding.btn1.getWidth() + (int) iBs[count - 1 - 2].getX() + (percentageCovered * binding.view100dp.getWidth()) / 100;
//        current.setX(x);
//        current.setImageDrawable(getResources().getDrawable(R.drawable.walking_man));
//        current.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        int mTop = (int) (binding.btn1.getWidth() / 2);
//        params.setMargins(0, mTop - 10, 0, 0);
//        current.setLayoutParams(params);
//        current.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//        for (int i = 0; i < count - 2; i++) {
//            iBs[i].setImageDrawable(getResources().getDrawable(R.drawable.unlock_icon));
//        }
//
//        binding.llRewardSlab.addView(current);

    }

//    private void SerWRReportConnect_Card() {
//
//        if (!CommonUtility.isNetworkAvailable(context))
//            return;
//
////        showProgressDialog();
//        String url = ServerConfig.newUrl(ServerConfig.SerWRReportConnect_Card(userId, userType), context, ConnectFragment.class.getSimpleName());
//
//        apiInterface.SerWRReportConnect_Card(url)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        compositeDisposable.add(d);
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody response) {
//                        dismissProgressDialog();
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        dismissProgressDialog();
//                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
//                    }
//                });
//
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
