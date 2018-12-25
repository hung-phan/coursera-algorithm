import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int R = ALPHABET.length();
    private Node root;
    private BoggleBoard board;
    private Set<String> words;
    private boolean[][] mark;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        root = new Node();

        for (String s : dictionary) {
            putWord(s);
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard givenBoard) {
        board = givenBoard;
        words = new HashSet<>();
        mark = new boolean[board.rows()][board.cols()];

        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                recursiveSearchValidWord(i, j, "");
            }
        }

        return words;
    }

    private void recursiveSearchValidWord(int i, int j, String currentWord) {
        char ch = board.getLetter(i, j);

        currentWord = currentWord + (ch == 'Q' ? "QU" : ch);

        if (!isValidPrefix(currentWord)) {
            return;
        }

        if (hasWord(currentWord)) {
            words.add(currentWord);
        }

        mark[i][j] = true;

        for (Direction direction : Direction.values()) {
            int newI = direction.dx + i;
            int newJ = direction.dy + j;

            if (isValidIndex(newI, newJ) && !mark[newI][newJ]) {
                recursiveSearchValidWord(newI, newJ, currentWord);
            }
        }

        mark[i][j] = false;
    }

    private boolean isValidIndex(int i, int j) {
        return 0 <= i && i < board.rows() && 0 <= j && j < board.cols();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int wordLength = word.length();

        if (wordLength <= 2) {
            return 0;
        } else if (wordLength <= 4) {
            return 1;
        } else if (wordLength == 5) {
            return 2;
        } else if (wordLength == 6) {
            return 3;
        } else if (wordLength == 7) {
            return 5;
        } else {
            return 11;
        }
    }

    private Node get(Node x, String word, int d) {
        if (x == null) {
            return null;
        }
        if (d == word.length()) {
            return x;
        }

        return get(x.next[charAt(word, d)], word, d + 1);
    }

    private void putWord(String word) {
        root = put(root, word, 0);
    }

    private Node put(Node x, String word, int d) {
        if (x == null) {
            x = new Node();
        }

        if (d == word.length()) {
            x.hasWord = true;
            return x;
        }

        char c = charAt(word, d);

        x.next[c] = put(x.next[c], word, d + 1);

        return x;
    }

    private char charAt(String s, int d) {
        return (char) (s.charAt(d) - 'A');
    }

    private boolean isValidPrefix(String prefix) {
        Node x = root;

        for (int i = 0; i < prefix.length() && x != null; i++) {
            x = x.next[charAt(prefix, i)];
        }

        return x != null;
    }

    private boolean hasWord(String word) {
        Node x = get(root, word, 0);

        return x != null && x.hasWord && word.length() > 2;
    }

    private enum Direction {
        NW(-1, -1),
        N(-1, 0),
        NE(-1, 1),

        SW(1, -1),
        S(1, 0),
        SE(1, 1),

        E(0, 1),
        W(0, -1);

        private final int dx;
        private final int dy;

        private Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private static class Node {
        private boolean hasWord;
        private Node[] next = new Node[R];
    }
}
