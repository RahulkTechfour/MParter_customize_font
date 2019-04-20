package com.luminous.mpartner.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.luminous.mpartner.utilities.PermissionUtils;

public class CameraPermissionFragment extends Fragment {
    private static final int REQUEST_CAMERA_FILE_PERMISSIONS = 10;
    public static final String TAG = "CameraPermission";

    private CameraFilePermissionCallback mCallback;
    private static boolean sCameraAndFilePermissionDenied;

    public static CameraPermissionFragment newInstance() {
        return new CameraPermissionFragment();
    }

    public CameraPermissionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CameraFilePermissionCallback) {
            mCallback = (CameraFilePermissionCallback) context;
        } else {
            throw new IllegalArgumentException("activity must extend CallBackMethod");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void checkCameraAndFilePermissions() {
        if (PermissionUtils.hasSelfPermission(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            mCallback.onCameraFilePermissionGranted();
        } else {
            // UNCOMMENT TO SUPPORT ANDROID M RUNTIME PERMISSIONS
            if (!sCameraAndFilePermissionDenied) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_FILE_PERMISSIONS);
            }
        }
    }

    public void setCameraPermissionDenied(boolean cameraPermissionDenied) {
        this.sCameraAndFilePermissionDenied = cameraPermissionDenied;
    }

    public static boolean isCameraAndFilePermissionDenied() {
        return sCameraAndFilePermissionDenied;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_FILE_PERMISSIONS) {
            if (PermissionUtils.verifyPermissions(grantResults)) {
                mCallback.onCameraFilePermissionGranted();
            } else {
                mCallback.onCameraFilePermissionDenied();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public interface CameraFilePermissionCallback {
        void onCameraFilePermissionGranted();

        void onCameraFilePermissionDenied();
    }

}