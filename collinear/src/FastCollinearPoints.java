import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;
import java.util.stream.Collectors;

public class FastCollinearPoints {
    private Point[] points;

    public FastCollinearPoints(Point[] points) {
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

    public int numberOfSegments() {
        return segments().length;
    }

    private int indexOf(Point value) {
        for (int i = 0; i < points.length; i++) {
            if (points[i] == value) {
                return i;
            }
        }

        return -1;
    }

    public LineSegment[] segments() {
        Set<Set<Integer>> set = new HashSet<>();
        List<LineSegment> segments = new ArrayList<>();
        Point[] newPoints = points.clone();

        for (Point point : points) {
            Arrays.sort(newPoints, point.slopeOrder());

            int i = 1;

            while (i < newPoints.length) {
                Set<Integer> collinearPointSet = new HashSet<>();
                int j = i + 1;

                collinearPointSet.add(indexOf(point));
                collinearPointSet.add(indexOf(newPoints[i]));

                while (j < newPoints.length &&
                        Double.compare(point.slopeTo(newPoints[i]), point.slopeTo(newPoints[j])) == 0) {
                    collinearPointSet.add(indexOf(newPoints[j]));
                    j += 1;
                }

                if (collinearPointSet.size() >= 4) {
                    set.add(collinearPointSet);
                }

                i = j;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}