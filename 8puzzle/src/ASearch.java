import java.util.*;

public class ASearch {
    public enum Direction {
        TOP(-1, 0), RIGHT(0, 1), BOTTOM(1, 0), LEFT(0, -1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public class Coordinator {
        final int x;
        final int y;

        Coordinator(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Coordinator move(Direction direction) {
            return new Coordinator(x + direction.dx, y + direction.dy);
        }
    }

    private int[][] blocks;

    public ASearch(int[][] blocks) {
        this.blocks = blocks;
    }

    private int dimension() {
        return blocks.length;
    }

    private double heuristic(Coordinator start, Coordinator destination) {
        return Math.sqrt(Math.pow(destination.x - start.x, 2) + Math.pow(destination.y - start.y, 2));
    }

    public boolean isInRange(Coordinator coordinator) {
        return (0 <= coordinator.x && coordinator.x < dimension() &&
                0 <= coordinator.y && coordinator.y < dimension());
    }

    public boolean isBlock(Coordinator coordinator) {
        return blocks[coordinator.x][coordinator.y] == 1;
    }

    public void solve(Coordinator start, Coordinator end) {
        Set<Coordinator> visited = new HashSet<>();
        Queue<Coordinator> queue = new PriorityQueue<>(Comparator.comparingDouble(o -> heuristic(o, end)));

        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinator currentCoordinator = queue.poll();

            if (currentCoordinator.equals(end)) {
                break;
            }

            for (Direction direction : Direction.values()) {
                Coordinator newCoordinator = currentCoordinator.move(direction);

                if (isInRange(newCoordinator) && !isBlock(newCoordinator) && !visited.contains(newCoordinator)) {
                    queue.add(newCoordinator);
                    visited.add(newCoordinator);
                }
            }
        }
    }
}
