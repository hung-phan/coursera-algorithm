import java.util.Collections;
import java.util.Stack;

public class PercolationStats {
    private int n;
    private int trials;
    private double[] fractions;

    private class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.n = n;
            this.trials = trials;
            this.fractions = new double[trials];

            this.calculate();
        }
    }

    private Stack<Position> generateRandomPositions() {
        Stack<Position> result = new Stack<>();

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                result.push(new Position(i, j));
            }
        }

        Collections.shuffle(result);

        return result;
    }

    private void calculate() {
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            Stack<Position> randomPositions = generateRandomPositions();

            while (!percolation.percolates()) {
                Position position = randomPositions.pop();

                percolation.open(position.x, position.y);
            }

            fractions[i] = percolation.numberOfOpenSites() * 1.0 / (n * n);
        }
    }

    public double mean() {
        double result = 0.0;

        for (double fraction : fractions) {
            result += fraction;
        }

        return result / trials;
    }

    public double stddev() {
        double result = 0.0;
        double meanResult = mean();

        for (double fraction : fractions) {
            result += Math.pow(fraction - meanResult, 2);
        }

        return Math.sqrt(result / (trials - 1));
    }

    public double confidenceLo() {
        return mean() - 1.96 * stddev() / Math.sqrt(trials);
    }

    public double confidenceHi() {
        return mean() + 1.96 * stddev() / Math.sqrt(trials);
    }

    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        System.out.println(String.format("mean                    = %f", stats.mean()));
        System.out.println(String.format("stddev                  = %f", stats.stddev()));
        System.out.println(String.format("95%% confidence interval = [%f, %f]", stats.confidenceLo(), stats.confidenceHi()));
    }
}
