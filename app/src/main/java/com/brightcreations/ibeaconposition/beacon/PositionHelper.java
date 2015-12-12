package com.brightcreations.ibeaconposition.beacon;

import com.brightcreations.ibeaconposition.math.Point2;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

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

    private BeaconsHelper mBeaconsHelper;

    private PositionHelper() {
    }

    public void init(BeaconsHelper beaconsHelper) {
        mBeaconsHelper = beaconsHelper;
    }

    public Point2 getPosition(List<Beacon> beacons, int calculateMethod) {
        Point2 position = null;
//        if (!beacons.isEmpty()) {
//            switch (calculateMethod) {
//                case APPROXIMATE_POSITION:
//                    position = getApproximatePosition(beacons);
//                    break;
//                case NEAREST_BEACON:
//                    position = getNearestBeacon(beacons);
//                    break;
//            }
//        }
        return position;
    }

    public Point2 getApproximatePosition(List<Beacon> beacons) {
        List<BeaconData> beaconsData = mBeaconsHelper.getBeaconsData(beacons);
        List<Point2> positions = new ArrayList<>();
        for (BeaconData beaconData : beaconsData) {
            positions.add(beaconData.getPosition());
        }
        double distancesSum = 0;
        for (Beacon beacon : beacons) {
            distancesSum += beacon.getDistance();
        }

        double[] distanceAverages = new double[beacons.size()];
        for (int i = 0; i < beacons.size(); i++) {
            Beacon beacon = beacons.get(i);
            distanceAverages[i] = 1 - (beacon.getDistance() / distancesSum);
            Point2 position = positions.get(i);
            position.x *= distanceAverages[i];
            position.y *= distanceAverages[i];
        }

        float sumX = 0;
        float sumY = 0;
        for (Point2 position : positions) {
            sumX += position.x;
            sumY += position.y;
        }
        float averageX = (sumX / positions.size()) - 1;
        float averageY = (sumY / positions.size()) - 1;

        return new Point2((int) averageX, (int) averageY);
    }

    public BeaconData getNearestBeacon(List<Beacon> beacons) {
        BeaconData beaconData = null;
        Beacon nearest = mBeaconsHelper.getNearestBeacon(beacons);
        if (nearest != null) {
            beaconData = mBeaconsHelper.getBeaconData(nearest);
        }
        return beaconData;
    }

}