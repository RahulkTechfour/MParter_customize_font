package com.luminous.mpartner.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentContactUsBinding;
import com.luminous.mpartner.events.ContactUsUriEvent;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ContactEntity;
import com.luminous.mpartner.network.entities.ContactUsDetail;
import com.luminous.mpartner.network.entities.ContactUsEntity;
import com.luminous.mpartner.network.entities.SaveContactUsSuggestionResponse;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.FilePath;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;


import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.luminous.mpartner.connect.WRSStatusNewActivity.loadFileAsBytesArray;


public class  ContactUsFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String name;
    private String email;
    private String message;
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ContactUsDetail contactUsDetailData;
    private ContactUsEntity contactUsDetailEntity;
    private String attachment = ServerConfig.BY_DEFALT;
    private OnFragmentInteractionListener mListener;

    private FragmentContactUsBinding binding;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactUsFragment newInstance(String param1, String param2) {
        ContactUsFragment fragment = new ContactUsFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        binding.fragmentLabel.tvLabel.setText(getString(R.string.contact_us));

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etEmail.length() == 0 || !CommonUtility.isValidEmail(binding.etEmail.getText())) {
                    Toast.makeText(getActivity(), "Please enter valid email id", Toast.LENGTH_SHORT).show();
                    return;
                } else if (binding.etName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (binding.etMessage.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter message", Toast.LENGTH_SHORT).show();
                    return;
                } else
                    email = binding.etEmail.getText().toString();
                if (binding.etName.length() > 0 && binding.etMessage.length() > 0) {
//                    Toast.makeText(getActivity(), "submitted", Toast.LENGTH_SHORT).show();
                    name = binding.etName.getText().toString();
                    message = binding.etMessage.getText().toString();
                    if (CommonUtility.isNetworkAvailable(context)) {
                        saveContactUsSuggestion();
                    }
                }
            }
        });

        binding.txtAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery();
            }
        });
        if (CommonUtility.isNetworkAvailable(context)) {
            getContactUsDetails();
        }


        try {
            MyApplication.getApplication()
                    .bus()
                    .toObservable()
                    .subscribe(object -> {
                        if (object instanceof ContactUsUriEvent) {
                            ContactUsUriEvent event = (ContactUsUriEvent) object;

                            try {
                                String selectedFilePath = FilePath.getPath(ContactUsFragment.this.getActivity(), event.getUri());
                                //  uploadFile(FilePath.getPath(getActivity(), event.getUri()));
                                if (selectedFilePath != null) {
                                    Bitmap mBitMap = FilePath.resizeBitMapImage1(FilePath.getPath(getActivity(), event.getUri()), 400, 400);
                                    attachment = FilePath.encodeFromString(mBitMap);

                                    binding.txtAttachment.setText(selectedFilePath);
                                }
                            }catch(Exception ex){
                                ex.getMessage();
                            }
                           // attachment =   encodeFileToBase64Binary(FilePath.getPath(getActivity(), event.getUri()));
                        }
                    });
        }catch(Exception ex){
            ex.getMessage();
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int PICK_FILE_REQUEST_Gallery = 2;

    private void gallery() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent, PICK_FILE_REQUEST_Gallery);
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

    private void setContactUsPageUI() {
        if (contactUsDetailEntity != null && !TextUtils.isEmpty(contactUsDetailEntity.getContactusTitle())) {
            binding.titleTv.setText(contactUsDetailEntity.getContactusTitle());
        }
        if (contactUsDetailEntity != null && !TextUtils.isEmpty(contactUsDetailEntity.getAddress())) {
            binding.addressTv.setText(contactUsDetailEntity.getAddress());
        }
        if (contactUsDetailEntity != null && !TextUtils.isEmpty(contactUsDetailEntity.getPhoneno())) {
            binding.phoneNoTv.setText(contactUsDetailEntity.getPhoneno());
        }
        if (contactUsDetailEntity != null && !TextUtils.isEmpty(contactUsDetailEntity.getSalesSupportPhoneno())) {
            binding.saleSupportNoTv.setText(contactUsDetailEntity.getSalesSupportPhoneno());
        }
        if (contactUsDetailEntity != null && !TextUtils.isEmpty(contactUsDetailEntity.getEmail())) {
            binding.emailTv.setText(contactUsDetailEntity.getEmail());
        }
    }

    private void clearViews() {
        binding.etName.setText("");
        binding.etEmail.setText("");
        binding.etMessage.setText("");
        binding.txtAttachment.setText("Attach Image");
    }

    private void getContactUsDetails() {
        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getContactUsDetailsUrl(), context, ContactUsFragment.class.getSimpleName() + ".class");

        apiInterface.getContactUsDetail(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContactUsDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ContactUsDetail contactUsDetailResponse) {
                        dismissProgressDialog();
                        if (contactUsDetailResponse.getStatus().equalsIgnoreCase("200")) {
                            contactUsDetailData = contactUsDetailResponse;
                            if (contactUsDetailData != null) {
                                contactUsDetailEntity = contactUsDetailData.getContactus().get(0);
                                setContactUsPageUI();
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

    private void saveContactUsSuggestion() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        showProgressDialog();

        String appVersionName = "", appVersionCode = "";
        final List<String> versionList = CommonUtility.appVersionDetails(context);
        if (versionList != null && versionList.size() == 2) {
            appVersionName = versionList.get(0);
            appVersionCode = versionList.get(1);
        }

        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setUser_id(new SharedPrefsManager(context).getString(SharedPreferenceKeys.USER_ID));
        contactEntity.setEmail(email);
        contactEntity.setName(name);
        contactEntity.setMessage(message);
        contactEntity.setImage(attachment);
        contactEntity.setFile("image.jpg");
        contactEntity.setToken(new SharedPrefsManager(context).getString(SharedPreferenceKeys.TOKEN));
        contactEntity.setApp_version(appVersionName);
        contactEntity.setAppversion_code(appVersionCode);
        contactEntity.setDevice_id(CommonUtility.getDeviceId(context));
        contactEntity.setDevice_name(CommonUtility.getDeviceName());
        contactEntity.setOs_type("Android");
        contactEntity.setIp_address(CommonUtility.getIpAddress(context));
        contactEntity.setLanguage(CommonUtility.getAppLanguage(context));
        contactEntity.setTime_captured(System.currentTimeMillis() + "");
        contactEntity.setChannel("M");

        String url = ServerConfig.saveContactUsSuggestionUrl();

        apiInterface.saveContactUsSuggestion(url, contactEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SaveContactUsSuggestionResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(SaveContactUsSuggestionResponse saveContactUsSuggestionResponse) {
                        dismissProgressDialog();
                        if (saveContactUsSuggestionResponse.getStatus().equalsIgnoreCase("200")) {
                            clearViews();
                            Toast.makeText(getActivity(), saveContactUsSuggestionResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                        CommonUtility.printLog("SaveContactUsSuggestion", "onComplete");
                    }
                });

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
        try {
            if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
        }catch(Exception ex){
            ex.getMessage();
        }
    }


    // change
   /* public void uploadFile(final String selectedFilePath) {


        File selectedFile = null;
        try {
            selectedFile = new File(selectedFilePath);
            attachment = getStringFile(selectedFile);
            binding.txtAttachment.setText(selectedFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        *//*if (!selectedFile.isFile()) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.txtAttachment.setText("Upload Pic" + selectedFilePath);
                }
            });
        } else {
            *//**//*try {


                byte[] encodedBytes = Base64.encodeBase64(loadFileAsBytesArray(selectedFilePath));
                attachment = new String(encodedBytes, "UTF-8"); // for UTF-8 encoding
                binding.txtAttachment.setText(selectedFilePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Cannot Read/Write File!", Toast.LENGTH_LONG).show();
            }*//**//*
        }*//*

      //  File selectedFile = Compressor.getDefault(getActivity()).compressToFile(new File(selectedFilePath));
       *//* Bitmap bitmap = BitmapFactory.decodeFile(selectedFilePath);
        //if (!selectedFile.isFile()) {
            binding.txtAttachment.setText("Upload Pic" + selectedFilePath);
       // }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        byte[] byteArray = stream.toByteArray();

        try {

            attachment = URLEncoder.encode(Base64.encodeToString(byteArray, Base64.DEFAULT), "UTF-8");
            binding.txtAttachment.setText(selectedFilePath);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*//*





    }*/


}
