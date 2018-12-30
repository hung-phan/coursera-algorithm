import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int CHARACTER_SET = 256;

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        String str = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(str);

        for (int i = 0, len = str.length(); i < len; i++) {
            int offset = csa.index(i);

            if (offset == 0) {
                BinaryStdOut.write(i);

                break;
            }
        }

        for (int i = 0, len = str.length(); i < len; i++) {
            int offset = csa.index(i);

            BinaryStdOut.write(str.charAt((offset + len - 1) % len), 8);
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String str = BinaryStdIn.readString();
        char[] charArray = str.toCharArray();
        char[] sortedCharArray = new char[str.length()];
        int[] count = radixSort(charArray);

        for (int i = 0; i < str.length(); i++) {
            sortedCharArray[count[charArray[i]]++] = charArray[i];
        }

        int[] next = new int[str.length()];
        count = radixSort(sortedCharArray);

        for (int i = 0; i < str.length(); i++) {
            next[count[charArray[i]]++] = i;
        }

        for (int i = 0, j = first; i < str.length(); i++) {
            BinaryStdOut.write(sortedCharArray[j]);
            j = next[j];
        }

        BinaryStdOut.close();
    }

    private static int[] radixSort(char[] array) {
        int[] count = new int[CHARACTER_SET + 1];

        for (char c : array) {
            count[c + 1]++;
        }

        for (int r = 0; r < CHARACTER_SET; r++) {
            count[r + 1] += count[r];
        }

        return count;
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid argument. Only accept 1 argument");
        }

        if ("-".equals(args[0])) {
            transform();
        } else if ("+".equals(args[0])) {
            inverseTransform();
        } else {
            throw new IllegalArgumentException("Invalid command. Only accept '+' or '-'");
        }

    }
}
