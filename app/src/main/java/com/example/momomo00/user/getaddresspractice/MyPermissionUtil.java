package com.example.momomo00.user.getaddresspractice;

import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;

public class MyPermissionUtil {
    private Activity mActivity;
    private List<String> mPermissions;
    private ShouldProvideRationaleListener mShouldProvideRationale;
    private RequestPermissionResultListener mRequestPermissionResultListener;

    public MyPermissionUtil(Activity activity, String[] permissions) {
        mActivity = activity;
        mPermissions = new ArrayList<>();
        setPermission(permissions);
        mShouldProvideRationale = null;
    }

    public void setPermission(String permission) {
        mPermissions.add(permission);
    }

    public void setPermission(String[] permissions) {
        for(String permission: permissions) {
            setPermission(permission);
        }
    }

    public void setShouldProvideRationale(ShouldProvideRationaleListener shouldProvideRationale) {
        mShouldProvideRationale = shouldProvideRationale;
    }

    public void setRequestPermissionResultListener(RequestPermissionResultListener requestPermissionResultListener) {
        mRequestPermissionResultListener = requestPermissionResultListener;
    }

    public boolean checkPermission() {
        boolean result = true;
        int permissionState;

        for (String permission: mPermissions) {
            permissionState = ActivityCompat.checkSelfPermission(mActivity, permission);
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        }

        return result;
    }

    public void requestPermissions(int requestCode) {
        boolean shouldProvideRationale;

        for (String permission: mPermissions) {
            shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, permission);

            if (shouldProvideRationale) {
                if(mShouldProvideRationale != null) {
                    mShouldProvideRationale.showRationale();
                }
            }
        }

        String[] permissions = mPermissions.toArray(new String[0]);
        ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
    }

    public void requestPermissionResult(String[] permissions, int[] grantResults) {
        boolean grantPermission = true;

        for (int grantResult: grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                grantPermission = false;
                break;
            }
        }

        if(mRequestPermissionResultListener != null) {
            if(grantPermission) {
                mRequestPermissionResultListener.onAllGranted(permissions, grantResults);
            } else {
                mRequestPermissionResultListener.onDenied(permissions, grantResults);
            }
        }
    }

    public interface ShouldProvideRationaleListener {
        void showRationale();
    }

    public interface RequestPermissionResultListener {
        void onAllGranted(String[] permissions, int[] grantResults);
        void onDenied(String[] permissions, int[] grantResults);
    }
}
