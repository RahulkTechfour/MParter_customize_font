package com.luminous.mpartner.lucky7;


import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentCheckYourGiftBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.LsdDealerGift;
import com.luminous.mpartner.network.entities.SaveDealerSacnCodeEntity;
import com.luminous.mpartner.utilities.CommonUtility;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckYourGiftFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckYourGiftFragment extends BaseFragment implements ZBarScannerView.ResultHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String secretCode = "", barCode = "";
    public static final int REQUEST_CAMERA_PERMISSION = 225;
    private FragmentCheckYourGiftBinding binding;
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CheckYourGiftFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckYourGiftFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckYourGiftFragment newInstance(String param1, String param2) {
        CheckYourGiftFragment fragment = new CheckYourGiftFragment();
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
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_your_gift, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        binding.ivScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openScanner();

            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (barCode.length() == 0) {
                    Toast.makeText(getActivity(), "Please scan bar code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (secretCode.length() > 0) {
                    saveDealerScanCode();
                } else {
                    Toast.makeText(getActivity(), "Please enter Alphanumeric code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCodeDialog();
            }
        });
        binding.txtBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowBarCodeDialog();
            }
        });

        binding.tvSecretCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCodeDialog();
            }
        });

        binding.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secretCode = "";
                barCode = "";
                binding.ivScanQr.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_barcode));
                binding.ivScanQr.setVisibility(View.VISIBLE);
                binding.ivGift.setVisibility(View.GONE);
                binding.activityScannerScannerview.setVisibility(View.GONE);
                binding.tvGift.setText("");

                binding.btnCode.setVisibility(View.VISIBLE);
                binding.btnSubmit.setVisibility(View.VISIBLE);

                binding.txtNoBarcode.setVisibility(View.VISIBLE);
                binding.txtBarcode.setVisibility(View.VISIBLE);
                binding.txtBarcode.setText("ENTER BARCODE MANUALLY");
                binding.ivScanQr.setClickable(true);
                binding.tvSecretCode.setText("");
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private void ShowBarCodeDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_bar_code);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final EditText editText = dialog.findViewById(R.id.edit_bar_code);
        Button submitButton = dialog.findViewById(R.id.btn_ok);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() == 12) {
                    barCode = editText.getText().toString().trim();
                    binding.txtBarcode.setText("Barcode -" + barCode);
                    binding.txtNoBarcode.setVisibility(View.GONE);
                    binding.ivScanQr.setVisibility(View.INVISIBLE);
                    binding.ivGift.setVisibility(View.GONE);
                    binding.activityScannerScannerview.setVisibility(View.GONE);

                    dialog.dismiss();
                } else {
                    editText.setError("Please enter 12 barcode digit");
                }
            }
        });

        dialog.show();
    }

    public void showCodeDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.gift_code_dialog_layout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final EditText editText = dialog.findViewById(R.id.edit_code);
        Button submitButton = dialog.findViewById(R.id.btn_verify);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() == 6) {
                    secretCode = editText.getText().toString().trim();
                    dialog.dismiss();

                    binding.tvSecretCode.setText("Alphanumeric Code - " + secretCode);
                    if (!TextUtils.isEmpty(barCode)) {
                        binding.ivScanQr.setVisibility(View.INVISIBLE);
                        binding.ivGift.setVisibility(View.GONE);
                        binding.activityScannerScannerview.setVisibility(View.GONE);
                    }
                } else {
                    editText.setError("Please enter 6 digit alphanumeric code");
                }
            }
        });

        dialog.show();

    }

    public void saveDealerScanCode() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }
        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.saveDealerScanCodeUrl(barCode, secretCode), context, CheckYourGiftFragment.class.getSimpleName() + ".class");

        apiInterface.saveDealerScanCode(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SaveDealerSacnCodeEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(SaveDealerSacnCodeEntity saveDealerSacnCodeEntity) {
                        dismissProgressDialog();

                        if (saveDealerSacnCodeEntity.getStatus().equalsIgnoreCase("200")) {
                            setGiftData(saveDealerSacnCodeEntity.getLsdDealerGift().get(0), saveDealerSacnCodeEntity.getMessage());
                        } else
                            Toast.makeText(getActivity(), saveDealerSacnCodeEntity.getMessage(), Toast.LENGTH_LONG).show();

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

    public boolean isCameraAvailable() {
        PackageManager pm = getActivity().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    public void openScanner() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.ivScanQr.setVisibility(View.GONE);
            binding.ivGift.setVisibility(View.GONE);
            binding.activityScannerScannerview.setVisibility(View.VISIBLE);
            if (isCameraAvailable()) {
                binding.activityScannerScannerview.stopCamera();
                binding.activityScannerScannerview.setResultHandler(this);
                binding.activityScannerScannerview.startCamera(false);
            } else {
                Toast.makeText(getActivity(), "Rear Facing Camera Unavailable",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void handleResult(Result rawResult) {

        barCode = rawResult.getContents();

        binding.ivScanQr.setVisibility(View.INVISIBLE);
        binding.ivGift.setVisibility(View.GONE);
        binding.activityScannerScannerview.setVisibility(View.GONE);
        binding.txtBarcode.setText("Barcode -" + barCode);
        binding.txtNoBarcode.setVisibility(View.GONE);

        Log.e("RESPONSE>>>>>>>>>>>>>>", "QRCode===" + barCode);
    }

    public void setGiftData(LsdDealerGift lsdDealerGift, String message) {
        if (lsdDealerGift.getGiftImage() != null && lsdDealerGift.getGiftDescription() != null && !lsdDealerGift.getGiftImage().equalsIgnoreCase("0")) {
            binding.ivScanQr.setVisibility(View.GONE);
            binding.ivGift.setVisibility(View.VISIBLE);
            binding.tvGift.setText(lsdDealerGift.getGiftDescription());
            binding.btnCode.setVisibility(View.GONE);
            binding.btnSubmit.setVisibility(View.GONE);
            Glide.with(getActivity()).load(lsdDealerGift.getGiftImage()).into(binding.ivGift);
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            binding.tvReset.performClick();
        }
    }
}
