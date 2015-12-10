package com.brightcreations.ibeaconposition.beacon;

/**
 * Created by mahmoudhabib on 4/7/15.
 */
public final class PositionHelper {
    public static PositionHelper getInstance() {
        if (sInstance == null) {
            sInstance = new PositionHelper();
        }
        return sInstance;
    }

    private static PositionHelper sInstance;

    private final String TAG = getClass().getSimpleName();

    private PositionHelper() {

    }
}