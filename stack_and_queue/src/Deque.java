import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private class Node {
        private Item value;
        private Node prev;
        private Node next;

        private Node(Item value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public Deque() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        if (item == null) {
            throw new NullPointerException();
        }

        if (head == null) {
            head = new Node(item, null, null);
            tail = head;
        } else {
            head = new Node(item, null, head);
            head.next.prev = head;
        }

        size += 1;
    }

    public void addLast(Item item) {
        if (item == null) {
            throw new NullPointerException();
        }

        if (tail == null) {
            head = new Node(item, null, null);
            tail = head;
        } else {
            tail = new Node(item, tail, null);
            tail.prev.next = tail;
        }

        size += 1;
    }

    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node ref = head;
        size -= 1;

        if (size == 0) {
            head = tail = null;
        } else {
            head = ref.next;
            head.prev = null;
            ref.next = null;
        }

        return ref.value;
    }

    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node ref = tail;
        size -= 1;

        if (size == 0) {
            head = tail = null;
        } else {
            tail = ref.prev;
            tail.next = null;
            ref.prev = null;
        }

        return ref.value;
    }

    public Iterator<Item> iterator() {
        Node ref = head;

        return new Iterator<Item>() {
            private Node runner = ref;

            @Override
            public boolean hasNext() {
                return runner != null;
            }

            @Override
            public Item next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                Node result = runner;
                runner = runner.next;
                return result.value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();

        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.addFirst(4);

        System.out.println(deque.removeLast());
        System.out.println(deque.removeLast());
        System.out.println(deque.removeLast());
        System.out.println(deque.removeLast());
    }
}
