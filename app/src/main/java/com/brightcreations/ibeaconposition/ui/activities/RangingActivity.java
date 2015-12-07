package com.brightcreations.ibeaconposition.ui.activities;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.brightcreations.ibeaconposition.android.R;
import com.brightcreations.ibeaconposition.beacon.Constants;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {
    private final String TAG = getClass().getSimpleName();

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        init();
    }

    private void init() {
        startRanging();
    }

    private void startRanging() {
        if (mBeaconManager == null) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
        }

        mBeaconManager.bind(this);
    }

    private void stopRanging() {
        if (mBeaconManager != null) {
            if (mBeaconManager.isBound(this)) {
                try {
                    mBeaconManager
                            .stopRangingBeaconsInRegion(
                                    new Region(Constants.UUID, null, null, null));
                } catch (RemoteException e) {
                    Log.e(TAG, "Error Stopping Ranging ", e);
                } finally {
                    mBeaconManager.unbind(this);
                    mBeaconManager = null;
                }
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(this);

        try {
            mBeaconManager
                    .startRangingBeaconsInRegion(new Region(Constants.UUID, null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, "Error Starting Ranging", e);
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        if (region.getUniqueId().equals(Constants.UUID)) {
            Log.d(TAG, "Beacons Found " + collection.size());
            for (Beacon beacon : collection) {
                Log.d(TAG, "Beacon " + beacon.toString());
            }
        }
    }
}
