import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private static final int BORDER_ENERGY = 1000;
    private int[][] colors;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        width = picture.width();
        height = picture.height();
        colors = new int[height][width];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                colors[row][col] = picture.get(col, row).getRGB();
            }
        }
    }

    private static void validateSeam(int[] seam, int index, int high) {
        if (seam[index] < 0 || seam[index] >= high || (index > 0 && Math.abs(seam[index] - seam[index - 1]) > 1)) {
            throw new IllegalArgumentException("Seam is illegal");
        }
    }

    // current picture
    public Picture picture() {
        Picture newPicture = new Picture(width, height);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newPicture.set(col, row, new Color(colors[row][col]));
            }
        }

        return newPicture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int col, int row) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IllegalArgumentException();
        }

        if (row == 0 || row == height - 1 || col == 0 || col == width - 1) {
            return BORDER_ENERGY;
        }

        int deltaX = gradientSquare(col + 1, row, col - 1, row);
        int deltaY = gradientSquare(col, row + 1, col, row - 1);

        return Math.sqrt(deltaX + deltaY);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[][] edgeTo = new int[height][width];
        double[][] distTo = new double[height][width];

        resetDistTo(distTo);

        for (int col = 0; col < width; col++) {
            distTo[0][col] = BORDER_ENERGY;
        }

        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                relax(Orientation.VERTICAL, row, col, edgeTo, distTo);
            }
        }

        int minCol = 0;
        double minDist = Double.POSITIVE_INFINITY;

        for (int col = 0; col < width; col++) {
            if (minDist > distTo[height - 1][col]) {
                minCol = col;
                minDist = distTo[height - 1][col];
            }
        }

        return trace(Orientation.VERTICAL, edgeTo, minCol);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[][] edgeTo = new int[height][width];
        double[][] distTo = new double[height][width];

        resetDistTo(distTo);

        for (int row = 0; row < height; row++) {
            distTo[row][0] = BORDER_ENERGY;
        }

        for (int col = 0; col < width - 1; col++) {
            for (int row = 0; row < height; row++) {
                relax(Orientation.HORIZONTAL, row, col, edgeTo, distTo);
           }
        }

        int minRow = 0;
        double minDist = Double.POSITIVE_INFINITY;

        for (int row = 0; row < height; row++) {
            if (minDist > distTo[row][width - 1]) {
                minRow = row;
                minDist = distTo[row][width - 1];
            }
        }

        return trace(Orientation.HORIZONTAL, edgeTo, minRow);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width) {
            throw new IllegalArgumentException("Seam is illegal");
        }

        if (height <= 1) {
            throw new IllegalArgumentException("Height of the picture is less than 1");
        }

        for (int col = 0; col < width; col++) {
            validateSeam(seam, col, width);

            for (int row = seam[col]; row < height - 1; row++) {
                colors[row][col] = colors[row + 1][col];
            }
        }

        height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height) {
            throw new IllegalArgumentException("Seam is illegal");
        }

        if (width <= 1) {
            throw new IllegalArgumentException("Weight of the picture is less than 1");
        }

        for (int row = 0; row < height; row++) {
            validateSeam(seam, row, width);

            for (int col = seam[row]; col < width - 1; col++) {
                colors[row][col] = colors[row][col + 1];
            }
        }

        width--;
    }

    private int[] trace(Orientation orientation, int[][] edgeTo, int start) {
        int[] seam;

        if (orientation == Orientation.VERTICAL) {
            seam = new int[height];

            for (int row = height - 1, col = start; row >= 0; row--) {
                seam[row] = col;
                col -= edgeTo[row][col];
            }
        } else {
            seam = new int[width];

            for (int row = start, col = width - 1; col >= 0; col--) {
                seam[col] = row;
                row -= edgeTo[row][col];
            }
        }

        return seam;
    }

    private void relax(Orientation orientation, int row, int col, int[][] edgeTo, double[][] distTo) {
        for (int i = -1; i <= 1; i++) {
            int nextRow;
            int nextCol;

            if (orientation == Orientation.VERTICAL) {
                nextRow = row + 1;
                nextCol = col + i;

                if (nextCol < 0 || nextCol >= width) {
                    continue;
                }
            } else {
                nextRow = row + i;
                nextCol = col + 1;

                if (nextRow < 0 || nextRow >= height) {
                    continue;
                }
            }

            double nextEnergy = distTo[row][col] + energy(nextCol, nextRow);

            if (distTo[nextRow][nextCol] > nextEnergy) {
                // we store the direction
                edgeTo[nextRow][nextCol] = i;
                distTo[nextRow][nextCol] = nextEnergy;
            }
        }
    }

    private void resetDistTo(double[][] distTo) {
        for (double[] row : distTo) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
    }

    private int gradientSquare(int x1, int y1, int x2, int y2) {
        Color color1 = new Color(colors[y1][x1]);
        Color color2 = new Color(colors[y2][x2]);
        int red = color1.getRed() - color2.getRed();
        int green = color1.getGreen() - color2.getGreen();
        int blue = color1.getBlue() - color2.getBlue();

        return red * red + green * green + blue * blue;
    }

    private enum Orientation { VERTICAL, HORIZONTAL }
}
