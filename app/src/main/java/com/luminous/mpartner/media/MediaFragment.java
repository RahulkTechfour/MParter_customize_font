package com.luminous.mpartner.media;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentMediaBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.Media.MediaDatum;
import com.luminous.mpartner.network.entities.Media.MediaListResponseEntity;
import com.luminous.mpartner.utilities.CommonUtility;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MediaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String TAG = "MediaFragment";
    FragmentMediaBinding  binding;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();



    public MediaFragment() {
    }

    public static MediaFragment newInstance(String param1, String param2) {
        MediaFragment fragment = new MediaFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_media, container, false);
        getMediaList();
        return binding.getRoot();
    }

    private void getMediaList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getMedialList(), getContext(), "MediaFragment.class");

        apiInterface.getMediaList(url)
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
                            Log.d(TAG, "onNext: "+response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MediaListResponseEntity entity = new Gson().fromJson(response, new TypeToken<MediaListResponseEntity>(){}.getType());

                        if (entity != null){
                            List<MediaDatum> list = entity.getMediaData();
                            MediaVedioAdapter adapter = new MediaVedioAdapter(getContext(), list);
                            binding.recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else{
                            Log.d(TAG, "Response null");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onError: message - "+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }


}
