import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int CHARACTER_SET = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        Node<Integer> alphabet = createASCIITable();

        while (!BinaryStdIn.isEmpty()) {
            int c = BinaryStdIn.readChar();
            Pair<Node<Integer>, Integer> pair = alphabet.find(c, 0);

            alphabet.remove(pair.t);
            alphabet = alphabet.addFirst(pair.t.value);

            BinaryStdOut.write(pair.u, 8);
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        Node<Integer> alphabet = createASCIITable();

        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            Node<Integer> c = alphabet.get(index, 0);

            BinaryStdOut.write(c.value, 8);

            alphabet.remove(c);
            alphabet = alphabet.addFirst(c.value);
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
            if (next != null) {
                return next.addLast(val);
            }

            Node<E> newNode = new Node<>(val, this, null);

            next = newNode;

            return newNode;
        }

        private Node<E> get(int index, int offset) {
            if (index == offset) {
                return this;
            }

            if (next == null) {
                throw new IndexOutOfBoundsException();
            }

            return next.get(index, offset + 1);
        }

        private Pair<Node<E>, Integer> find(E val, int offset) {
            if (value == val) {
                return new Pair<>(this, offset);
            }

            if (next == null) {
                throw new IllegalArgumentException(String.format("Cannot find character %s", val));
            }

            return next.find(val, offset + 1);
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
    }
}
