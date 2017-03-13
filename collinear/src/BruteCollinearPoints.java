import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;
import java.util.stream.Collectors;

public class BruteCollinearPoints {
    private Point[] points;

    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new NullPointerException();
        }

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new NullPointerException();
            }

            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException();
                }
            }
        }

        this.points = points.clone();
    }

    private boolean areCollinear(Point a, Point b, Point c, Point d) {
        double slopeFromAToB = a.slopeTo(b);
        double slopeFromBToC = b.slopeTo(c);
        double slopeFromCToD = c.slopeTo(d);

        return Double.compare(slopeFromAToB, slopeFromBToC) == 0 &&
                Double.compare(slopeFromBToC, slopeFromCToD) == 0;
    }

    public int numberOfSegments() {
        return segments().length;
    }

    public LineSegment[] segments() {
        Set<Set<Integer>> set = new HashSet<>();
        List<LineSegment> segments = new ArrayList<>();

        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 1; k++) {
                    for (int l = k + 1; l < points.length; l++) {
                        if (areCollinear(points[i], points[j], points[k], points[l])) {
                            Set<Integer> collinearPointSet = new HashSet<>();

                            collinearPointSet.add(i);
                            collinearPointSet.add(j);
                            collinearPointSet.add(k);
                            collinearPointSet.add(l);

                            set.add(collinearPointSet);
                        }
                    }
                }
            }
        }

        for (Set<Integer> collinear : set) {
            List<Point> collinearPoints = collinear
                    .stream()
                    .map(i -> points[i])
                    .collect(Collectors.toList());

            Collections.sort(collinearPoints);

            segments.add(new LineSegment(
                    collinearPoints.get(0),
                    collinearPoints.get(collinearPoints.size() - 1)
            ));
        }

        return segments.toArray(new LineSegment[segments.size()]);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
