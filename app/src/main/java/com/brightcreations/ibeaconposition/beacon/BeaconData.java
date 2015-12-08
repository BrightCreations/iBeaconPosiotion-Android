package com.brightcreations.ibeaconposition.beacon;

import com.brightcreations.ibeaconposition.math.Point2;

/**
 * Created by mahmoudhabib on 12/8/15.
 */
public class BeaconData {
    private String id;
    private Point2 position;
    private Point2 roomPosition;

    public BeaconData() {
    }

    public BeaconData(String id, Point2 position,
            Point2 roomPosition) {
        this.id = id;
        this.position = position;
        this.roomPosition = roomPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point2 getPosition() {
        return position;
    }

    public void setPosition(Point2 position) {
        this.position = position;
    }

    public Point2 getRoomPosition() {
        return roomPosition;
    }

    public void setRoomPosition(Point2 roomPosition) {
        this.roomPosition = roomPosition;
    }

    @Override
    public String toString() {
        return "{id: " + this.id + ", pos: " + position.toString() + ", roomPos: " + roomPosition
                .toString() + "}";
    }
}
