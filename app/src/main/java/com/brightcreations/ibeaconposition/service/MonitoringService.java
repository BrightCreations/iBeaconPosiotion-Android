package com.brightcreations.ibeaconposition.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.brightcreations.ibeaconposition.beacon.Constants;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

/**
 * Created by mahmoudhabib on 12/6/15.
 */
public class MonitoringService extends Service implements BeaconConsumer, MonitorNotifier {
    public static final String ACTION_ENTER_REGION = "com.brightcreations.ibeaconposition.action.ACTION_ENTER_REGION";
    public static final String ACTION_EXIT_REGION = "com.brightcreations.ibeaconposition.action.ACTION_EXIT_REGION";
    public static final String ACTION_MONITOR_STARTED = "com.brightcreations.ibeaconposition.action.ACTION_MONITOR_STARTED";
    public static final String ACTION_MONITOR_FAILED = "com.brightcreations.ibeaconposition.action.ACTION_MONITOR_FAILED";
    public static final String ACTION_STOP_SERVICE = "com.brightcreations.ibeaconposition.action.ACTION_STOP_SERVICE";

    public static final String EXTRA_REGION_UNIQUE_ID = "com.brightcreations.ibeaconposition.extra.EXTRA_REGION_UNIQUE_ID";
    public static final String EXTRA_REGION_ID_1 = "com.brightcreations.ibeaconposition.extra.EXTRA_REGION_ID_1";
    public static final String EXTRA_REGION_ID_2 = "com.brightcreations.ibeaconposition.extra.EXTRA_REGION_ID_2";
    public static final String EXTRA_REGION_ID_3 = "com.brightcreations.ibeaconposition.extra.EXTRA_REGION_ID_3";


    private final String TAG = getClass().getSimpleName();

    private BeaconManager mBeaconManager;

    private RequestsBroadcastReceiver mRequestsBroadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mRequestsBroadcastReceiver == null) {
            mRequestsBroadcastReceiver = new RequestsBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STOP_SERVICE);
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mRequestsBroadcastReceiver, intentFilter);
        }

        if (mBeaconManager == null) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
        }

        mBeaconManager.bind(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBeaconManager != null) {
            mBeaconManager.unbind(this);
            mBeaconManager = null;
        }

        if (mRequestsBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestsBroadcastReceiver);
            mRequestsBroadcastReceiver = null;
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "Beacon Service Connected");
        mBeaconManager.setMonitorNotifier(this);
        try {
            mBeaconManager
                    .startMonitoringBeaconsInRegion(new Region(Constants.UUID, null, null, null));
            Log.d(TAG, "Monitoring Started");
        } catch (RemoteException e) {
            Log.e(TAG, "Error Starting Monitoring ", e);
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG, "Entered Region " + region.toString());
        notifyEnterRegion(region);
    }

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "Exited Region " + region.toString());
        notifyExitRegion(region);
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.i(TAG, "State Changed For Region " + region.toString());
    }

    private void stop() {
        stopSelf();
    }

    private void notifyEnterRegion(Region region) {
        sendBroadcast(ACTION_ENTER_REGION, getRegionBundle(region));
    }

    private void notifyExitRegion(Region region) {
        sendBroadcast(ACTION_EXIT_REGION, getRegionBundle(region));
    }

    private Bundle getRegionBundle(Region region) {
        Bundle bundle = new Bundle();
        String uniqueId = region.getUniqueId();
        bundle.putString(EXTRA_REGION_UNIQUE_ID, uniqueId);

        Identifier id1 = region.getId1();
        if (id1 != null) {
            bundle.putString(EXTRA_REGION_ID_1, id1.toString());
        }

        Identifier id2 = region.getId2();
        if (id2 != null) {
            bundle.putString(EXTRA_REGION_ID_2, id2.toString());
        }

        Identifier id3 = region.getId3();
        if (id3 != null) {
            bundle.putString(EXTRA_REGION_ID_3, id3.toString());
        }
        return bundle;
    }

    private void sendBroadcast(String action, Bundle extras) {
        Intent broadcast = new Intent(action);
        broadcast.putExtras(extras);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    private class RequestsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_STOP_SERVICE:
                    stop();
                    break;
            }
        }
    }
}
