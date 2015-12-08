package com.brightcreations.ibeaconposition.beacon;

import org.altbeacon.beacon.Beacon;

import java.util.Comparator;

/**
 * Created by mahmoudhabib on 12/8/15.
 */
public class BeaconsDistanceComparator implements Comparator<Beacon> {
    @Override
    public int compare(Beacon lhs, Beacon rhs) {
        Double a = lhs.getDistance();
        Double b = rhs.getDistance();
        return a.compareTo(b);
    }
}
