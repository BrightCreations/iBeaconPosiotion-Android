package com.brightcreations.ibeaconposition.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.brightcreations.ibeaconposition.android.R;
import com.brightcreations.ibeaconposition.service.MonitoringService;

public class MonitoringActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private MonitoringBroadcastReceiver mMonitoringBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMonitoringBroadcastReceiver == null) {
            mMonitoringBroadcastReceiver = new MonitoringBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MonitoringService.ACTION_ENTER_REGION);
            intentFilter.addAction(MonitoringService.ACTION_EXIT_REGION);
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mMonitoringBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMonitoringBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(mMonitoringBroadcastReceiver);
            mMonitoringBroadcastReceiver = null;
        }
    }

    private void init() {
        startBeaconsMonitoring();
    }

    private void startBeaconsMonitoring() {
        Intent intent = new Intent(MonitoringActivity.this, MonitoringService.class);
        startService(intent);
    }

    private void enteredRegion(Bundle bundle) {
        Log.d(TAG, "Entered Region " + bundle.toString());

    }

    private void exitedRegion(Bundle bundle) {
        Log.d(TAG, "Exited Region " + bundle.toString());
    }

    private class MonitoringBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MonitoringService.ACTION_ENTER_REGION:
                    enteredRegion(intent.getExtras());
                    break;
                case MonitoringService.ACTION_EXIT_REGION:
                    exitedRegion(intent.getExtras());
                    break;
            }
        }
    }
}
