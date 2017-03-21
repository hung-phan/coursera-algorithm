import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> set;

    public PointSET() {
        set = new TreeSet<>();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        if (contains(p)) {
            return;
        }

        set.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        return set.contains(p);
    }

    public void draw() {
        for (Point2D point2D : set) {
            point2D.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }

        LinkedList<Point2D> list = new LinkedList<>();

        for (Point2D point2D : set) {
            if (rect.contains(point2D)) {
                list.addFirst(point2D);
            }
        }

        return list;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        double minDistance = Double.MAX_VALUE;
        Point2D nearestPoint = null;

        for (Point2D point2D : set) {
            double distance = p.distanceTo(point2D);

            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = point2D;
            }
        }

        return nearestPoint;
    }

    public static void main(String[] args) {
    }
}
