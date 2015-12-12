package com.brightcreations.ibeaconposition.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.brightcreations.ibeaconposition.android.R;
import com.brightcreations.ibeaconposition.beacon.BeaconData;
import com.brightcreations.ibeaconposition.beacon.BeaconsHelper;
import com.brightcreations.ibeaconposition.beacon.Constants;
import com.brightcreations.ibeaconposition.beacon.PositionHelper;
import com.brightcreations.ibeaconposition.math.Point2;
import com.brightcreations.ibeaconposition.service.SubmitPositionService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {
    private static final int REQUEST_CHECK_LOCATION_PERMISSION = 0;

    private static final int APPROXIMATE_POSITION = 0;
    private static final int NEAREST_BEACON = 1;

    private static final String FILE_NAME = "beacons_json.json";

    private final String TAG = getClass().getSimpleName();

    private String mUserName = "Mahmoud Habib";

    private Region mRegion;
    private BeaconManager mBeaconManager;
    private BeaconsHelper mBeaconsHelper;
    private PositionHelper mPositionHelper;

    private int mMethod = APPROXIMATE_POSITION;

    private View mMainLayout;
    private ImageView mPinImageView;

    private int mWidth;
    private int mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mMainLayout != null) {
            mWidth = mMainLayout.getWidth();
            mHeight = mMainLayout.getHeight();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRanging();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRanging();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CHECK_LOCATION_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(this);
        try {
            mBeaconManager
                    .startRangingBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
            Log.e(TAG, "Error Starting Ranging", e);
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        List<Beacon> matchingBeacons = mBeaconsHelper.getMatchingBeacons(collection, mRegion);

        for (Beacon beacon : matchingBeacons) {
            Log.d(TAG, "Beacon " + beacon.toString() + " Dist " + beacon
                    .getDistance() + " Rssi " + beacon.getRssi());
        }

        updatePosition(matchingBeacons);
    }

    private void init() {
        mMainLayout = findViewById(R.id.main_layout);
        mPinImageView = (ImageView) findViewById(R.id.pin_image_view);

        initBeaconManager();
        mBeaconsHelper = BeaconsHelper.getInstance(getApplicationContext());
        mBeaconsHelper.setOnFileLoadListener(new BeaconsHelper.OnFileLoadListener() {
            @Override
            public void onReady() {
                Log.d(TAG, "File Ready");
            }

            @Override
            public void onFail() {
                Log.d(TAG, "File Fail");
            }
        });
        mBeaconsHelper.init(FILE_NAME);

        mPositionHelper = PositionHelper.getInstance();
        mPositionHelper.init(mBeaconsHelper);
    }

    private void initBeaconManager() {
        mRegion = new Region(Constants.UUID, null, null, null);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers()
                .add(new BeaconParser().setBeaconLayout(Constants.BEACON_LAYOUT));
    }

    private void startRanging() {
        if (checkPermissionsGranted(Manifest.permission.ACCESS_COARSE_LOCATION,
                REQUEST_CHECK_LOCATION_PERMISSION)) {
            mBeaconManager.bind(this);
        }
    }

    private void stopRanging() {
        if (mBeaconManager != null) {
            if (mBeaconManager.isBound(this)) {
                try {
                    mBeaconManager
                            .stopRangingBeaconsInRegion(
                                    mRegion);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error Stopping Ranging ", e);
                } finally {
                    mBeaconManager.unbind(this);
                }
            }
        }
    }

    private void updatePosition(List<Beacon> beacons) {
        Point2 position = null;
        switch (mMethod) {
            case APPROXIMATE_POSITION:
                position = mPositionHelper.getApproximatePosition(beacons);
                Log.d(TAG, "Approximate Pos " + position);
                break;
            case NEAREST_BEACON:
                BeaconData beaconData = mPositionHelper.getNearestBeacon(beacons);
                if (beaconData != null) {
                    Log.d(TAG, "Nearest Beacon " + beaconData.toString());
                    position = beaconData.getRoomPosition();

                    submitPosition(beaconData.getId(), mUserName);
                }
                break;
        }
        updatePinPosition(position);
    }

    private void submitPosition(String beaconId, String userName) {
        Intent intent = new Intent(RangingActivity.this, SubmitPositionService.class);
        intent.putExtra(SubmitPositionService.EXTRA_BEACON_ID, beaconId);
        intent.putExtra(SubmitPositionService.EXTRA_NAME, userName);
        startService(intent);
    }

    private void updatePinPosition(final Point2 pos) {
        if (pos != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showPin();
                    float ratioX = mWidth / 100.0f;
                    float ratioY = mHeight / 100.0f;

                    float translationX = (pos.x * ratioX);
                    translationX -= mPinImageView.getWidth() / 2;

                    float translationY = (pos.y * ratioY);
                    translationY -= mPinImageView.getWidth() / 2;

                    mPinImageView.animate().translationX(translationX)
                            .translationY(translationY).setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator());
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hidePin();
                }
            });
        }
    }


    private void showPin() {
        mPinImageView.animate().alpha(1.0f).setDuration(300);
    }

    private void hidePin() {
        mPinImageView.animate().alpha(0.0f).setDuration(300);
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
        startRanging();
    }
}
