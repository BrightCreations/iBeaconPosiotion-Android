package com.brightcreations.ibeaconposition.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.brightcreations.ibeaconposition.android.R;
import com.brightcreations.ibeaconposition.beacon.Constants;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CHECK_LOCATION_PERMISSION = 0;

    private static final int ACTION_START_MONITORING = 0;
    private static final int ACTIon_START_RANGING = 1;

    private final String TAG = getClass().getSimpleName();

    private int mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        if (requestCode == REQUEST_CHECK_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();
        }
    }

    private void init() {
        initBeaconManager();
    }

    private void initBeaconManager() {
        BeaconManager beaconManager = BeaconManager
                .getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers()
                .add(new BeaconParser().setBeaconLayout(Constants.BEACON_LAYOUT));
    }

    public void startMonitoring(View view) {
        startMonitoringActivity();
    }

    public void startRanging(View view) {
        startRangingActivity();
    }

    private void startMonitoringActivity() {
        mAction = ACTION_START_MONITORING;
        if (checkPermissionsGranted(Manifest.permission.ACCESS_COARSE_LOCATION,
                REQUEST_CHECK_LOCATION_PERMISSION)) {
            Intent intent = new Intent(MainActivity.this, MonitoringActivity.class);
            startActivity(intent);
        }
    }

    private void startRangingActivity() {
        mAction = ACTIon_START_RANGING;
        if (checkPermissionsGranted(Manifest.permission.ACCESS_COARSE_LOCATION,
                REQUEST_CHECK_LOCATION_PERMISSION)) {
            Intent intent = new Intent(MainActivity.this, RangingActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkPermissionsGranted(String permission,
            int requestCode) {
        boolean granted = ContextCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "Permission Granted ? " + granted);
        if (!granted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                showRationale();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        permission
                }, requestCode);
            }
        }
        return granted;
    }

    private void showRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("Location permission is required");
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermissionsGranted(Manifest.permission.ACCESS_COARSE_LOCATION,
                        REQUEST_CHECK_LOCATION_PERMISSION);
            }
        });
        builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void permissionGranted() {
        switch (mAction) {
            case ACTION_START_MONITORING:
                startMonitoringActivity();
                break;
            case ACTIon_START_RANGING:
                startRangingActivity();
                break;
        }
    }

}
