import java.util.ArrayList;

public class Board {
    private enum Direction {
        TOP(-1, 0), RIGHT(0, 1), BOTTOM(1, 0), LEFT(0, -1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private static final int SPACE = 0;
    private int[][] blocks;

    public Board(int[][] blocks) {
        this.blocks = blocks.clone();
    }

    private Board createNewBoard(int i1, int j1, int i2, int j2) {
        Board newBoard = new Board(blocks);
        int[][] newBlocks = newBoard.blocks;

        int swapValue = newBlocks[i1][j1];
        newBlocks[i1][j1] = newBlocks[i2][j2];
        newBlocks[i2][j2] = swapValue;

        return newBoard;
    }

    private int getExpectedValue(int i, int j) {
        return (i == dimension() - 1 && j == dimension() - 1) ? SPACE : i * dimension() + j + 1;
    }

    private int getExpectedMove(int i, int j) {
        return Math.abs((blocks[i][j] - 1) / dimension() - i) + Math.abs((blocks[i][j] - 1) % dimension() - j);
    }

    private boolean isInBoard(int i, int j) {
        return (0 <= i && i < dimension() && 0 <= j && j < dimension());
    }

    public int dimension() {
        return blocks.length;
    }

    public int hamming() {
        int count = 0;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != SPACE && blocks[i][j] != getExpectedValue(i, j)) count++;
            }
        }

        return count;
    }

    public int manhattan() {
        int count = 0;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != SPACE) count += getExpectedMove(i, j);
            }
        }

        return count;
    }

    public boolean isGoal() {
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != getExpectedValue(i, j)) return false;
            }
        }

        return true;
    }

    public Board twin() {
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension() - 1; j++) {
                if (blocks[i][j] != SPACE && blocks[i][j + 1] != SPACE) {
                    return createNewBoard(i, j, i, j + 1);
                }
            }
        }

        throw new Error("No available twin for current board");
    }

    public boolean equals(Object y) {
        if (!(y instanceof Board)) throw new IllegalArgumentException();

        Board otherBoard = (Board) y;

        if (dimension() != otherBoard.dimension()) return false;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != otherBoard.blocks[i][j]) return false;
            }
        }

        return true;
    }

    public Iterable<Board> neighbors() {
        boolean found = false;
        int zeroX = 0;
        int zeroY = 0;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] == 0) {
                    found = true;
                    zeroX = i;
                    zeroY = j;
                    break;
                }
            }

            if (found) break;
        }

        if (!found) throw new Error("The blocks state is contaminated");

        ArrayList<Board> result = new ArrayList<>();

        for (Direction direction: Direction.values()) {
            int newX = zeroX + direction.dx;
            int newY = zeroY + direction.dy;

            if (isInBoard(newX, newY)) result.add(createNewBoard(zeroX, zeroY, newX, newY));
        }

        return result;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(dimension());
        stringBuilder.append('\n');

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                stringBuilder.append(String.format(" %d ", blocks[i][j]));
            }
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}
