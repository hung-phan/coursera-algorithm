import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;

public class KdTree {
    private enum Orientation {
        HORIZONTAL, VERTICAL;

        static Orientation getDefaultOrientation() {
            return VERTICAL;
        }

        static Orientation getNextOrientation(Orientation orientation) {
            switch (orientation) {
                case HORIZONTAL:
                    return VERTICAL;
                case VERTICAL:
                    return HORIZONTAL;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private class Node<T> {
        Node<T> left;
        Node<T> right;
        Orientation orientation;
        T value;

        Node(T _value, Orientation _orientation) {
            value = _value;
            orientation = _orientation;
            left = null;
            right = null;
        }
    }

    private final RectHV defaultRect;
    private Point2D nearestPoint;
    private Point2D queryPoint;
    private Node<Point2D> root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
        defaultRect = new RectHV(0, 0, 1, 1);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        if (root == null) {
            root = new Node<>(p, Orientation.getDefaultOrientation());
            size++;
        } else {
            Node<Point2D> node = root;

            while (true) {
                Point2D point2D = node.value;
                Orientation orientation = node.orientation;

                if (p.equals(point2D)) {
                    return;
                }

                if ((orientation == Orientation.VERTICAL && p.x() < point2D.x()) ||
                        (orientation == Orientation.HORIZONTAL && p.y() < point2D.y())) {
                    if (node.left != null) {
                        node = node.left;
                    } else {
                        node.left = new Node<>(p, Orientation.getNextOrientation(orientation));
                        size++;
                        return;
                    }
                } else {
                    if (node.right != null) {
                        node = node.right;
                    } else {
                        node.right = new Node<>(p, Orientation.getNextOrientation(orientation));
                        size++;
                        return;
                    }
                }
            }
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        Node<Point2D> node = root;

        while (node != null) {
            Point2D point2D = node.value;

            if (p.equals(point2D)) {
                return true;
            }

            if ((node.orientation == Orientation.VERTICAL && p.x() < point2D.x()) ||
                    (node.orientation == Orientation.HORIZONTAL && p.y() < point2D.y())) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return false;
    }

    public void draw() {
        StdDraw.setScale(0, 1);
        draw(root, defaultRect);
    }

    private void draw(Node<Point2D> node, RectHV rect) {
        if (node != null) {
            Point2D point2D = node.value;

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.02);
            new Point2D(point2D.x(), point2D.y()).draw();
            StdDraw.setPenRadius();

            if (node.orientation == Orientation.VERTICAL) {
                StdDraw.setPenColor(StdDraw.RED);
                new Point2D(point2D.x(), rect.ymin()).drawTo(new Point2D(point2D.x(), rect.ymax()));
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                new Point2D(rect.xmin(), point2D.y()).drawTo(new Point2D(rect.xmax(), point2D.y()));
            }

            draw(node.left, getLower(rect, node));
            draw(node.right, getHigher(rect, node));
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }

        LinkedList<Point2D> list = new LinkedList<>();

        findNodeInRange(list, root, rect, defaultRect);

        return list;
    }

    private RectHV getLower(RectHV container, Node<Point2D> node) {
        Point2D point2D = node.value;

        return (node.orientation == Orientation.VERTICAL)
                ? new RectHV(container.xmin(), container.ymin(), point2D.x(), container.ymax())
                : new RectHV(container.xmin(), container.ymin(), container.xmax(), point2D.y());
    }

    private RectHV getHigher(RectHV container, Node<Point2D> node) {
        Point2D point2D = node.value;

        return (node.orientation == Orientation.VERTICAL)
                ? new RectHV(point2D.x(), container.ymin(), container.xmax(), container.ymax())
                : new RectHV(container.xmin(), point2D.y(), container.xmax(), container.ymax());
    }

    private void findNodeInRange(LinkedList<Point2D> list, Node<Point2D> node, RectHV query, RectHV container) {
        if (node != null && container.intersects(query)) {
            Point2D point2D = node.value;

            if (query.contains(point2D)) {
                list.addFirst(point2D);
            }

            findNodeInRange(list, node.left, query, getLower(container, node));
            findNodeInRange(list, node.right, query, getHigher(container, node));
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        nearestPoint = null;
        queryPoint = p;

        nearestNeighborSearch(root, defaultRect);

        return nearestPoint;
    }

    private void nearestNeighborSearch(Node<Point2D> node, RectHV container) {
        if (node != null) {
            Point2D point2D = node.value;

            if (nearestPoint == null) {
                nearestPoint = node.value;
            } else if (queryPoint.distanceTo(point2D) < queryPoint.distanceTo(nearestPoint)) {
                nearestPoint = point2D;
            }

            if (container.distanceSquaredTo(queryPoint) < queryPoint.distanceSquaredTo(nearestPoint)) {
                RectHV lower = getLower(container, node);
                RectHV higher = getHigher(container, node);

                if (lower.contains(queryPoint)) {
                    nearestNeighborSearch(node.left, lower);
                    nearestNeighborSearch(node.right, higher);
                } else {
                    nearestNeighborSearch(node.right, higher);
                    nearestNeighborSearch(node.left, lower);
                }
            }
        }
    }
}
