package com.brightcreations.ibeaconposition.math;


/**
 * Created by mahmoudhabib on 3/29/15.
 */
public final class MathUtils {
    public static Point2 trilateration(Point2 a, Point2 b, Point2 c, double distA,
            double distB,
            double distC) {
        double w, z, x, y, y2;

        w =
                (distA * distA) - (distB * distB) - (a.getX() * a.getX()) - (a.getY() * a
                        .getY()) + (b.getX() * b.getX()) +
                        (b.getY() * b.getY());

        z =
                ((distB * distB) - (distC * distC) - (b.getX() * b.getX()) - (b.getY() * b
                        .getY()) + (c.getX() * c.getX()) +
                        (c.getY() * c.getY()));

        x =
                (w * (c.getY() - b.getY()) - z * (b.getY() - a.getY())) /
                        (2 * ((b.getX() - a.getX()) * (c.getY() - b.getY()) - (c.getX() - b
                                .getX()) * (b.getY() - a.getY())));

        y = (w - 2 * x * (b.getX() - a.getX())) / (2 * (b.getY() - a.getY()));

        y2 = (z - 2 * x * (c.getX() - b.getX())) / (2 * (c.getY() - b.getY()));

        y = (y + y2) / 2;


        return new Point2((int) x, (int) y);
    }

    public static double distanceBetweenTwoPoints(Point2 first, Point2 second) {

        double a = first.getX() - second.getX();
        double b = first.getY() - second.getY();

        return Math.sqrt((a * a) + (b * b));
    }

    public static Point2 midpoint(Point2 a, Point2 b) {
        Point2 location;

        location = new Point2();
        int midX = (a.getX() + b.getX()) / 2;
        int midY = (a.getY() + b.getY()) / 2;
        location.setX(midX);
        location.setY(midY);

        return location;
    }

    public static double meterToPixel(double meter) {
        return meter * 100.0 / 3.0;
    }
}
