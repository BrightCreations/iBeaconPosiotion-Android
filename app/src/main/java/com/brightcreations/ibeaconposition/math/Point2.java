package com.brightcreations.ibeaconposition.math;

/**
 * Created by mahmoudhabib on 12/8/15.
 */
public class Point2 {
    public int x;
    public int y;

    public Point2() {

    }

    public Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2(Point2 pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Point2) {
            Point2 point = (Point2) o;
            return this.x == point.x && this.y == point.y;
        }
        return false;
    }
}
