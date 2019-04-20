package com.luminous.mpartner.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luminous.mpartner.BaseActivity;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ActivityProfileBinding;
import com.luminous.mpartner.home_page.fragments.HomePageFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ProfileEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ProfileActivity extends BaseActivity {

    //Firebase Analyticsiv_my_info
//    private FirebaseAnalytics mFirebaseAnalytics;

    private ProfileEntity mProfileDetail;

    private final int REQUEST_CAMERA = 2001;
    private final int REQUEST_GALLERY = 2002;
    private final int READ_STORAGE = 201;
    private final int REQUEST_CAMERA_USE = 200;
    private String TAG;
    private Uri imageUri;
    private ActivityProfileBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //Check for version
    String currentVersion, latestVersion;
    Dialog dialog;

    Typeface tfRegular;
    private SharedPrefsManager sharedPrefsManager;
    private RetrofitInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        sharedPrefsManager = new SharedPrefsManager(this);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        mProfileDetail = (ProfileEntity) sharedPrefsManager.getObject(SharedPreferenceKeys.USER_DATA, ProfileEntity.class);

        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Screenviews
        Bundle params = new Bundle();
        params.putString("screen_name", "com.luminous.mpartner.ui.activities.MyProfileActivity");
//        mFirebaseAnalytics.logEvent("MyProfileActivity", params);
        /*Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "com.luminous.mpartner.ui.activities.MyProfileActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);*/

        //Check current version
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        //Check Play Store Version
//        VersionChecker versionChecker = new VersionChecker();
//        latestVersion = null;
//        try {
//            latestVersion = versionChecker.execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        //Compare Current Version with Playstore Version
        if (latestVersion != null) {
            //if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            if (Float.parseFloat(currentVersion) < Float.parseFloat(latestVersion) && !currentVersion.equalsIgnoreCase(latestVersion)) {
                if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                    showUpdateDialog();
                }
            }
        }

        prepareToolbar();
        prepareViews();
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("A New Update is Available");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.luminous.mpartner")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitActivity.exitApplication(getApplicationContext());
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }

    private void prepareViews() {

        //Setting typefaces
//        tfRegular = FontProvider.getInstance().tfRegular;
//        binding.tvName.setTypeface(tfRegular);
//        binding.etEmail.setTypeface(tfRegular);
//
//        binding.fscCode.setTypeface(tfRegular);
//        binding.disCode.setTypeface(tfRegular);
//
//        binding.etPhoneNum.setTypeface(tfRegular);
//        binding.etAddress.setTypeface(tfRegular);

        binding.etEmail.setEnabled(false);
        binding.fscCode.setEnabled(false);
        binding.disCode.setEnabled(false);

        binding.etPhoneNum.setEnabled(false);
        binding.etAddress.setEnabled(false);
        binding.ivMyInfo.setEnabled(false);


        if (mProfileDetail != null) {
//            AppSharedPrefrences.getInstance(this).setUserName(mProfileDetail.Name);
//            String temp = mProfileDetail.UserImage;
//            temp = temp.replaceAll(" ", "%20");

//            ImageLoader imageLoader = ImageLoader.getInstance();
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.drawable.user) // resource or drawable
//                    .showImageForEmptyUri(R.drawable.user) // resource or drawable
//                    .showImageOnFail(R.drawable.user) // resource or drawable
//                    .build();
//            imageLoader.displayImage(temp, ivImage, options);

            binding.tvName.setText("" + mProfileDetail.getName());
            binding.etEmail.setText("" + mProfileDetail.getEmail());
            binding.etPhoneNum.setText("" + mProfileDetail.getPhone());
            binding.etAddress.setText("" + mProfileDetail.getAddress1());
            binding.fscCode.setText("" + mProfileDetail.getCity());
            binding.disCode.setText("" + mProfileDetail.getDistrict());

        }
    }

    private void prepareToolbar() {


        setSupportActionBar(binding.toolbarLayout.toolbar);

        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.toolbarLayout.title.setText(getString(R.string.my_profile));
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.toolbarLayout.tvEdit.setVisibility(View.GONE);
        binding.toolbarLayout.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    public void updateUser() {
        String mode = binding.toolbarLayout.tvEdit.getText().toString();
        if (mode.equalsIgnoreCase("Save")) {
            BitmapDrawable drawable = (BitmapDrawable) binding.ivMyInfo.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_avatar);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //Check if bitmap is null by Anusha 27/08/2018 TASK#4436
            String image_str = "";
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
                byte[] byte_arr = stream.toByteArray();
//                image_str = Base64.encode(byte_arr);
            }

            if (binding.fscCode.getText().toString().length() < 5) {
                Toast.makeText(this, "Please enter valid fse code", Toast.LENGTH_SHORT).show();
            } else if (binding.fscCode.getText().toString().length() > 5) {
                Toast.makeText(this, "Please enter valid fse code", Toast.LENGTH_SHORT).show();
            } else if (binding.disCode.getText().toString().length() < 7) {
                Toast.makeText(this, "Please enter valid distributor code", Toast.LENGTH_SHORT).show();
            } else if (binding.disCode.getText().toString().length() > 7) {
                Toast.makeText(this, "Please enter valid distributor code", Toast.LENGTH_SHORT).show();
            } else {
                updateProfileData();
            }
        } else {
            binding.toolbarLayout.tvEdit.setText("Save");
            binding.etAddress.setEnabled(true);
            binding.etPhoneNum.setEnabled(true);
            binding.fscCode.setEnabled(true);
            binding.disCode.setEnabled(true);
            binding.etEmail.setEnabled(true);


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.etEmail.getWindowToken(), 0);

            binding.ivMyInfo.setEnabled(true);
        }
    }

    private void updateProfileData() {

        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.sscPartnerDetailst(userId), ProfileActivity.this, ProfileActivity.class.getSimpleName());

        apiInterface.sscPartnerDetailst(url)
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
                            ProfileEntity profileEntity = new Gson().fromJson(jsonArray.getString(0), ProfileEntity.class);

//                            Toast.makeText(ProfileActivity.this, "Information updated successfully.", Toast.LENGTH_SHORT).show();
                            finish();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }


    public void selectImageDialog(View v) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    takeImageFromCamera();
                } else if (items[item].equals("Choose from Library")) {
                    takeImageFromGallery();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takeImageFromCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_USE);

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private void takeImageFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE);

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_USE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Granted");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else {
                    Log.e(TAG, "Permission Denied");
                }
                return;
            }
            case READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Granted");
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
                } else {
                    Log.e(TAG, "Permission Denied");
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap thumbnail = null;
            if (requestCode == REQUEST_CAMERA) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                imageUri = data.getData();

            } else if (requestCode == REQUEST_GALLERY) {
                try {
                    Uri selectedImage = data.getData();
                    imageUri = selectedImage;
                    thumbnail = CommonUtility.getBitmap(getApplicationContext(), selectedImage);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Please select another image", Toast.LENGTH_SHORT).show();
                }
            }
//            AppSharedPrefrences.getInstance(this).setProfilePicPath(CommonUtility.profileImgsaveToInternalSorage(this, thumbnail));
            binding.ivMyInfo.setImageBitmap(thumbnail);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
