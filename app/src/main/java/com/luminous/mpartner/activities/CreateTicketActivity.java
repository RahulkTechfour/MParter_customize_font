package com.luminous.mpartner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.connect.ConnectFragment;
import com.luminous.mpartner.connect.FilePath;
import com.luminous.mpartner.connect.UpdateListActivity;
import com.luminous.mpartner.connect.UserController;
import com.luminous.mpartner.databinding.ActivityCreateTicketBinding;
import com.luminous.mpartner.deeplinking.DeeplinkingHandler;
import com.luminous.mpartner.home_page.fragments.HomePageFragment;
import com.luminous.mpartner.network.GetticketDatum;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.Media.MediaListResponseEntity;
import com.luminous.mpartner.network.entities.SaveAssistEntityResponse;
import com.luminous.mpartner.network.entities.SaveContactUsSuggestionResponse;
import com.luminous.mpartner.network.entities.TicketEntity;
import com.luminous.mpartner.network.entities.TicketListEntity;
import com.luminous.mpartner.network.entities.ViewTicketEntity;
import com.luminous.mpartner.network.entities.ViewticketDatum;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.DialogMenu;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CreateTicketActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final String TAG = CreateTicketActivity.class.getSimpleName();
    private String selectedFilePath;
    private String serialNumber, error;
    private ActivityCreateTicketBinding binding;
    private String attachment = "", attach_type;
    private RetrofitInterface apiInterface;
    private ProgressDialog progressDialog;
    private int ticketId;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_ticket);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);


        Bundle bundle = getIntent().getExtras();
        serialNumber = bundle.getString("serial_number");
        error = bundle.getString("desc");
        ticketId = bundle.getInt("ticket_id");

        initToolbar();
        initUI();

    }

    private void initUI() {
        if (error != null && !error.isEmpty()) {
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(error);
        } else
            binding.txtError.setVisibility(View.GONE);

        binding.edtSerialNumber.setText(serialNumber);

        binding.ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call our Dialogmenu by Anusha 06/07/2018 TASK#4009
                DialogMenu dialogMenu = new DialogMenu(CreateTicketActivity.this);
                dialogMenu.setListener(new DialogMenu.OnDialogMenuListener() {
                    @Override
                    public void onPDFPress() {
                        Intent intent = new Intent();
                        intent.setType("application/*");
                        String[] mimetypes = {"application/vnd.ms-excel", "application/pdf"};
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
                    }

                    @Override
                    public void onGalleryPress() {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_FILE_REQUEST);
                    }
                });

            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        if (ticketId != 0)
            getTicket(ticketId);
    }

    private void getTicket(int ticketId) {
        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getViewTicket(ticketId + ""), getApplicationContext(), CreateTicketActivity.class.getSimpleName());
        apiInterface.getTicketDetails(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ViewTicketEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ViewTicketEntity entity) {
                        dismissProgressDialog();
                        if (entity != null
                                && entity.getViewticketData() != null && entity.getViewticketData().size() > 0) {
                            binding.llTicDetail.setVisibility(View.VISIBLE);
                            binding.llTicDetail.removeAllViews();
                            for (final ViewticketDatum detail : entity.getViewticketData()) {
                                LayoutInflater inflater = null;
                                inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View convertView = inflater.inflate(R.layout.item_ticket_detail, null);
                                TextView tvdate = (TextView) convertView.findViewById(R.id.tvdate);
                                TextView tvcreatedBy = (TextView) convertView.findViewById(R.id.tvcreatedBy);
                                final TextView tvAttach = (TextView) convertView.findViewById(R.id.tvAttach);
                                TextView tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
                                TextView tvError = (TextView) convertView.findViewById(R.id.txt_error);

                                tvdate.setText(detail.getDate());
                                tvcreatedBy.setText(detail.getCreatedby());
                                tvDesc.setText(detail.getDescription());
                                tvError.setText(detail.getConnectplusMsg());
                                //tvAttach.setText(detail.Attachment);
                                if (detail.getAttachment() == null) {
                                    tvAttach.setEnabled(false);
                                    tvAttach.setAlpha(0.5f);
                                }

                                tvAttach.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (detail.getAttachment().contains(".pdf") || detail.getAttachment().contains(".PDF") || detail.getAttachment().contains(".xls") || detail.getAttachment().contains(".xlsx")) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detail.getAttachment()));
                                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(browserIntent);
                                        } else if (detail.getAttachment().contains(".png") || detail.getAttachment().contains(".jpg") || detail.getAttachment().contains(".jpeg")) {
                                            //by Anusha 16/07/2018 TASK#4009
                                            //   showProgressDialog();
                                            CommonUtility.downloadFile(getApplicationContext(), detail.getAttachment(), "ticket.jpg"); //change
                                            CommonUtility.downloadFile(getApplicationContext(), detail.getAttachment(), detail.getDescription() + ".jpg");
                                        } else {
                                            tvAttach.setEnabled(false);
                                            tvAttach.setAlpha(0.5f);
                                        }
                                    }
                                });

                                binding.llTicDetail.addView(convertView);
                            }
                        } else {
                            binding.llTicDetail.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), entity.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void onSubmit() {
        if (binding.etDesc.getText().toString().length() == 0) {
            binding.etDesc.setError("You need to enter a Description stating the issue");
            binding.etDesc.setFocusable(true);
            return;
        } else {
            showProgressDialog();
            String appVersionName = "", appVersionCode = "";
            final List<String> versionList = CommonUtility.appVersionDetails(getApplicationContext());
            if (versionList != null && versionList.size() == 2) {
                appVersionName = versionList.get(0);
                appVersionCode = versionList.get(1);
            }

            TicketEntity entity = new TicketEntity();
            entity.setUser_id(new SharedPrefsManager(getApplicationContext()).getString(SharedPreferenceKeys.USER_ID));
            entity.setAttachmentname("attachment.jpg");
            entity.setAttachment(attachment);
            entity.setSerialno(binding.edtSerialNumber.getText().toString());
            entity.setDescription(binding.etDesc.getText().toString());
            entity.setConnectplus_message("Not registered");
            entity.setStatus("O");
            entity.setToken(new SharedPrefsManager(getApplicationContext()).getString(SharedPreferenceKeys.TOKEN));
            entity.setApp_version(appVersionName);
            entity.setAppversion_code(appVersionCode);
            entity.setDevice_id(CommonUtility.getDeviceId(getApplicationContext()));
            entity.setDevice_name(CommonUtility.getDeviceName());
            entity.setOs_type("Android");
            entity.setIp_address(CommonUtility.getIpAddress(getApplicationContext()));
            entity.setLanguage(CommonUtility.getAppLanguage(getApplicationContext()));
            entity.setTime_captured(System.currentTimeMillis() + "");
            entity.setChannel("M");

            if (ticketId == 0) {
                String url = ServerConfig.saveAssistTicket();
                apiInterface.saveAssistTicket(url, entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                dismissProgressDialog();
                                String response = null;
                                try {
                                    response = responseBody.string();
                                    Log.d(TAG, "onNext: " + response);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                SaveContactUsSuggestionResponse entity = new Gson().fromJson(response, new TypeToken<SaveContactUsSuggestionResponse>() {
                                }.getType());
                                if (entity.getStatus().equalsIgnoreCase("200")) {
                                    clearViews();
                                    Toast.makeText(getApplicationContext(), entity.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissProgressDialog();
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                                dismissProgressDialog();
                                CommonUtility.printLog("SaveContactUsSuggestion", "onComplete");
                            }
                        });
            } else {

                entity.setTicketid(ticketId + "");
                String url = ServerConfig.updateAssistTicket();
                apiInterface.updateAssistTicket(url, entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                dismissProgressDialog();
                                String response = null;
                                try {
                                    response = responseBody.string();
                                    Log.d(TAG, "onNext: " + response);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                SaveContactUsSuggestionResponse entity = new Gson().fromJson(response, new TypeToken<SaveContactUsSuggestionResponse>() {
                                }.getType());
                                if (entity.getStatus().equalsIgnoreCase("200")) {
                                    clearViews();
                                    Toast.makeText(getApplicationContext(), entity.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissProgressDialog();
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                                dismissProgressDialog();
                                CommonUtility.printLog("SaveContactUsSuggestion", "onComplete");
                            }
                        });

            }
        }

    }

    private void clearViews() {
        binding.etDesc.setText("");
        binding.tvFileName.setText("");
        Intent deeplinkIntent = new Intent(CreateTicketActivity.this, DeeplinkingHandler.class);
        deeplinkIntent.setData(Uri.parse("Connect"));
        startActivity(deeplinkIntent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    Uri selectedFileUri = data.getData();
                    selectedFilePath = FilePath.getPath(this, selectedFileUri);
                    Log.i(TAG, "Selected File Path:" + selectedFilePath);

                    if (selectedFilePath != null && !selectedFilePath.equals("")) {
                        attach_type = selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1);
                        if (attach_type.equalsIgnoreCase("pdf") || attach_type.equalsIgnoreCase("xls") //Added Excel by Anusha 10/07/2018 TASK#4009
                                || attach_type.equalsIgnoreCase("xlsx") || attach_type.equalsIgnoreCase("jpg") ||
                                attach_type.equalsIgnoreCase("png") || attach_type.equalsIgnoreCase("jpeg")) {
                            binding.tvFileName.setText(selectedFilePath);
                            uploadFile(selectedFilePath);
                        } else {
                            Toast.makeText(this, "Please attach either a PDF, Excel or an Image", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public void uploadFile(final String selectedFilePath) {
        File selectedFile = new File(selectedFilePath);
        // String[] parts = selectedFilePath.split("/");
        //   final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
        } else {
            try {
                byte[] encodedBytes = Base64.encodeBase64(loadFileAsBytesArray(selectedFilePath));
                attachment = new String(encodedBytes, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        //Make Length Long for Toast by Anusha 01/08/2018 TASK#4307
                        Toast.makeText(CreateTicketActivity.this, "Cannot Read/Write File!", Toast.LENGTH_LONG).show();
                    }
                };
            }
        }
    }

    /**
     * This method loads a file from file system and returns the byte array of the content.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {

        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;

    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }


    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
