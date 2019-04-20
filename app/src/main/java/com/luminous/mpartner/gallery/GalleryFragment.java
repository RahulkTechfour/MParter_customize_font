package com.luminous.mpartner.gallery;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentGalleryBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.GalleryDatum;
import com.luminous.mpartner.network.entities.GalleryMainData;
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
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String gallery_menu_upper_id;
    private String gallery_menu_upper_name;

    private OnFragmentInteractionListener mListener;
    private FragmentGalleryBinding binding;
    private GalleryRecyclerViewAdapter adapter;
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<GalleryDatum> galleryDatumList;
    private int galleryCategoryId;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
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
            gallery_menu_upper_id = getArguments().getString(ARG_PARAM1);
            gallery_menu_upper_name = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        if (CommonUtility.isNetworkAvailable(context)) {
            getGalleryMainData();
        }

        return view;
    }

    private void setVideoListData() {


        adapter = new GalleryRecyclerViewAdapter(context, galleryDatumList);
        binding.recyclerView.setAdapter(adapter);
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
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private void getGalleryMainData() {
        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(String.format(ServerConfig.getGalleryMainDataUrl(), gallery_menu_upper_id), context, GalleryFragment.class.getSimpleName() + ".class");

        apiInterface.getGalleryMainData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GalleryMainData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(GalleryMainData galleryMainData) {
                        dismissProgressDialog();
                        if (galleryMainData.getStatus().equalsIgnoreCase("200")) {

                            if (galleryMainData.getGalleryData() != null && galleryMainData.getGalleryData().size() > 0) {

                                galleryDatumList = galleryMainData.getGalleryData();
                                setVideoListData();
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
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

}
