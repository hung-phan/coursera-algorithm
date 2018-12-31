import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int CHARACTER_SET = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        Node<Integer> alphabet = createASCIITable();

        while (!BinaryStdIn.isEmpty()) {
            int c = BinaryStdIn.readChar();
            Pair<Node<Integer>, Integer> pair = alphabet.find(c);

            if (alphabet != pair.t) {
                alphabet = alphabet.addFirst(pair.t.value);
                alphabet.remove(pair.t);
            }

            BinaryStdOut.write(pair.u, 8);
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        Node<Integer> alphabet = createASCIITable();

        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            Node<Integer> c = alphabet.get(index);

            if (alphabet != c) {
                alphabet = alphabet.addFirst(c.value);
                alphabet.remove(c);
            }

            BinaryStdOut.write(c.value, 8);
        }

        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid argument. Only accept 1 argument");
        }

        if ("-".equals(args[0])) {
            encode();
        } else if ("+".equals(args[0])) {
            decode();
        } else {
            throw new IllegalArgumentException("Invalid command. Only accept '+' or '-'");
        }
    }

    private static Node<Integer> createASCIITable() {
        Node<Integer> head = new Node<>(0, null, null);
        Node<Integer> runner = head;

        for (int i = 1; i < CHARACTER_SET; i++) {
            runner = runner.addLast(i);
        }

        return head;
    }

    private static class Pair<T, U> {
        private final T t;
        private final U u;

        Pair(T t, U u) {
            this.t = t;
            this.u = u;
        }
    }

    private static class Node<E> {
        private final E value;
        private Node<E> next;
        private Node<E> prev;

        Node(E value, Node<E> prev, Node<E> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        private Node<E> addFirst(E val) {
            Node<E> head = new Node<>(val, null, this);

            prev = head;

            return head;
        }

        private Node<E> addLast(E val) {
            Node<E> runner = this;

            while (runner.next != null) {
                runner = runner.next;
            }

            Node<E> last = new Node<>(val, runner, null);

            runner.next = last;

            return last;
        }

        private Node<E> get(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException();
            }

            Node<E> runner = this;
            int currentIndex = 0;

            while (runner != null && currentIndex != index) {
                currentIndex++;
                runner = runner.next;
            }

            if (runner == null) {
                throw new IndexOutOfBoundsException();
            }

            return runner;
        }

        private Pair<Node<E>, Integer> find(E val) {
            Node<E> runner = this;
            int currentIndex = 0;

            while (runner != null && runner.value.equals(val)) {
                currentIndex++;
                runner = runner.next;
            }

            if (runner == null) {
                throw new IllegalArgumentException(String.format("Cannot find character %s", val));
            }

            return new Pair<>(runner, currentIndex);
        }

        private void remove(Node<E> node) {
            if (node == null) {
                return;
            }

            Node<E> prevNode = node.prev;
            Node<E> nextNode = node.next;

            if (prevNode != null) {
                prevNode.next = nextNode;
            }

            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
        }

        @Override
        public String toString() {
            return String.format("%s -> %s}", value, next);
        }
    }
}
