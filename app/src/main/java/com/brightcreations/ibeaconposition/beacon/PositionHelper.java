package com.brightcreations.ibeaconposition.beacon;

import com.brightcreations.ibeaconposition.math.Point2;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahmoudhabib on 4/7/15.
 */
public final class PositionHelper {
    public static final int CALCULATE_NEAREST_AVERAGE = 0;
    public static final int CALCULATE_NEAREST = 1;

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
        if (!beacons.isEmpty()) {
            switch (calculateMethod) {
                case CALCULATE_NEAREST_AVERAGE:
                    position = calculateNearestAverage(beacons);
                    break;
                case CALCULATE_NEAREST:
                    position = calculateNearest(beacons);
                    break;
            }
        }
        return position;
    }

    private Point2 calculateNearestAverage(List<Beacon> beacons) {
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

    private Point2 calculateNearest(List<Beacon> beacons) {
        Beacon nearest = mBeaconsHelper.getNearestBeacon(beacons);
        BeaconData beaconData = mBeaconsHelper.getBeaconData(nearest.getId3().toString());
        return beaconData.getRoomPosition();
    }

}