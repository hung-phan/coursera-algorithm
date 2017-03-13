import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private WeightedQuickUnionUF quickUnionUF;
    private boolean[][] grid;
    private int openSites;
    private int n;

    private enum Direction {
        LEFT(0, -1), RIGHT(0, 1), TOP(-1, 0), BOTTOM(1, 0);

        int dx;
        int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be larger than 0");
        } else {
            this.n = n;
            this.grid = new boolean[n][n];
            this.openSites = 0;
            this.quickUnionUF = new WeightedQuickUnionUF(n * n + 2);
        }
    }

    private int convertCoordinator(int row, int col) {
        return (row - 1) * n + col;
    }

    private boolean isValidCoordinator(int row, int col) {
        return 1 <= row && row <= n && 1 <= col && col <= n;
    }

    public void open(int row, int col) {
        if (!isOpen(row, col)) {
            final int currentPosition = this.convertCoordinator(row, col);

            this.grid[row - 1][col - 1] = true;
            this.openSites++;

            for (Direction direction : Direction.values()) {
                final int newRow = row + direction.dx;
                final int newCol = col + direction.dy;

                if (newRow == 0) {
                    this.quickUnionUF.union(currentPosition, 0);
                } else if (newRow == n + 1) {
                    this.quickUnionUF.union(currentPosition, n * n + 1);
                } else if (this.isValidCoordinator(newRow, newCol) && this.grid[newRow - 1][newCol - 1]) {
                    this.quickUnionUF.union(currentPosition, this.convertCoordinator(newRow, newCol));
                }
            }
        }
    }

    public boolean isOpen(int row, int col) {
        if (!isValidCoordinator(row, col)) {
            throw new IndexOutOfBoundsException(String.format("Invalid coordinator (%d, %d).", row, col));
        }

        return this.grid[row - 1][col - 1];
    }

    public boolean isFull(int row, int col) {
        return this.isOpen(row, col) && this.quickUnionUF.connected(0, this.convertCoordinator(row, col));
    }

    public boolean percolates() {
        return this.quickUnionUF.connected(0, n * n + 1);
    }

    public int numberOfOpenSites() {
        return this.openSites;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                buffer.append(this.grid[i][j] ? 'x' : '_').append(' ');
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }
}
