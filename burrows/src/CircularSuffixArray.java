public class CircularSuffixArray {
    private static final int CHARACTER_SET = 256;
    private final int length;
    private final OffsetString[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        length = s.length();
        indices = new OffsetString[length];

        for (int i = 0, len = length(); i < len; i++) {
            indices[i] = new OffsetString(s, i);
        }

        sort();
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("EBNYXMAODP");

        System.out.println(csa.length);

        for (OffsetString os : csa.indices) {
            System.out.println(os);
        }
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException();
        }

        return indices[i].offset;
    }

    private void radixSortRecursive(int low, int high, OffsetString[] aux, int bit) {
        int[] count = new int[CHARACTER_SET + 1];

        for (int i = low; i <= high; i++) {
            count[indices[i].charAt(bit) + 1]++;
        }

        for (int i = 0; i < CHARACTER_SET; i++) {
            count[i + 1] += count[i];
        }

        for (int i = low; i <= high; i++) {
            aux[count[indices[i].charAt(bit)]++] = indices[i];
        }

        System.arraycopy(aux, 0, indices, low, high - low + 1);

        for (int i = 0; i < CHARACTER_SET; i++) {
            int newLow = low + count[i];
            int newHigh = low + count[i + 1] - 1;

            if (newLow < newHigh) {
                radixSortRecursive(newLow, newHigh, aux, bit + 1);
            }
        }
    }

    private void sort() {
        if (length <= 1) {
            return;
        }

        radixSortRecursive(0, length - 1, new OffsetString[length], 0);
    }

    private class OffsetString implements Comparable<OffsetString> {
        private final String val;
        private final int length;
        private final int offset;

        OffsetString(String val, int offset) {
            this.val = val;
            this.offset = offset;
            this.length = val.length();
        }

        @Override
        public int compareTo(OffsetString otherOS) {
            for (int i = 0; i < length; i++) {
                int diff = this.charAt(i) - otherOS.charAt(i);

                if (diff != 0) {
                    return diff;
                }
            }

            return 0;
        }

        int charAt(int i) {
            if (i >= length) {
                return 0;
            }

            return val.charAt((i + offset) % length);
        }

        @Override
        public String toString() {
            return String.format("OffsetString{offset=%d}", offset);
        }
    }
}
