package com.example.momomo00.user.getaddresspractice;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mAddressTextView;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        Button getAddressButton = findViewById(R.id.get_address_button);
        getAddressButton.setOnClickListener(mOnClickListener);

        mAddressTextView = findViewById(R.id.show_address_text_view);
        mLatitudeTextView = findViewById(R.id.show_latitude_text_view);
        mLongitudeTextView = findViewById(R.id.show_longitude_text_view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.get_address_button:
                    onClickTheGetAddressButton();
                    break;
                default:
                    break;
            }
        }
    };

    private void onClickTheGetAddressButton() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        showLocation(location);
                    }
                });

    }

    private void showLocation(Location location) {
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
    }
}