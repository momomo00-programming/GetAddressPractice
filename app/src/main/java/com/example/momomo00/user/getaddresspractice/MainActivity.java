package com.example.momomo00.user.getaddresspractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_CODE = 34;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mAddressTextView;
    private Button mGetAddressButton;

    private FusedLocationProviderClient mFusedLocationClient;
    private AddressResultReceiver mResultReceiver;
    private MyPermissionUtil mMyPermissionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        if (mMyPermissionUtil.checkPermission()) {
            mGetAddressButton.setEnabled(true);
        } else {
            mMyPermissionUtil.requestPermissions(REQUEST_PERMISSIONS_CODE);
        }
    }

    private void init() {
        mGetAddressButton = findViewById(R.id.get_address_button);
        mGetAddressButton.setOnClickListener(mOnClickListener);
        mGetAddressButton.setEnabled(false);

        mAddressTextView = findViewById(R.id.show_address_text_view);
        mLatitudeTextView = findViewById(R.id.show_latitude_text_view);
        mLongitudeTextView = findViewById(R.id.show_longitude_text_view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mMyPermissionUtil = new MyPermissionUtil(this, UsePermissionConstants.PERMISSIONS);
        mMyPermissionUtil.setRequestPermissionResultListener(new MyPermissionUtil.RequestPermissionResultListener() {
            @Override
            public void onAllGranted(String[] permissions, int[] grantResults) {
                mGetAddressButton.setEnabled(true);
            }

            @Override
            public void onDenied(String[] permissions, int[] grantResults) {}
        });

        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                mMyPermissionUtil.requestPermissionResult(permissions,grantResults);
                break;
            default:
                break;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.get_address_button:
                    onClickTheGetAddressButton();
                    break;
                default:
                    break;
            }
        }
    };

    private void onClickTheGetAddressButton() {
        mGetAddressButton.setEnabled(false);

        if(mMyPermissionUtil.checkPermission()) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            showLocation(location);
                        }
                    });
        }
    }

    private void showLocation(Location location) {
        mGetAddressButton.setEnabled(true);

        if(location == null) {
            return;
        }

        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));

        if(!Geocoder.isPresent()) {
            return;
        }

        startAddressAcquisitionService(location);
    }

    private void startAddressAcquisitionService(Location location) {
        mGetAddressButton.setEnabled(false);
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(LocationConstants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mGetAddressButton.setEnabled(true);
            String address = resultData.getString(LocationConstants.RESULT_DATA_KEY);
            mAddressTextView.setText(address);
        }
    }

}