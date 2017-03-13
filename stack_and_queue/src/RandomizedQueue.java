import java.util.*;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] queue;
    private int size;

    public RandomizedQueue() {
        this.size = 0;
        this.queue = (Item[]) new Object[2];
    }

    private void resize(int newLength) {
        queue = Arrays.copyOf(this.queue, newLength);
    }

    private int getRandomIndex() {
        Random random = new Random();
        return random.nextInt(size);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return this.size;
    }

    public void enqueue(Item item) {
        if (item == null) {
            throw new NullPointerException();
        }

        queue[size] = item;
        size += 1;

        if (size == queue.length) {
            resize(queue.length * 2);
        }
    }

    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int randomIndex = getRandomIndex();
        Item item = queue[randomIndex];

        if (randomIndex == size - 1) {
            queue[randomIndex] = null;
        } else {
            queue[randomIndex] = queue[size - 1];
            queue[size - 1] = null;
        }

        size -= 1;

        if (2 <= queue.length / 4 && size <= queue.length / 4) {
            resize(queue.length / 4);
        }

        return item;
    }

    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return queue[getRandomIndex()];
    }

    private List<Integer> createRandomIndexes() {
        Integer[] indexes = new Integer[size];

        for (int i = 0; i < size; i += 1) {
            indexes[i] = i;
        }

        return Arrays.asList(indexes);
    }

    public Iterator<Item> iterator() {
        Item[] ref = this.queue;
        List<Integer> indexes = createRandomIndexes();

        Collections.shuffle(createRandomIndexes());

        return new Iterator<Item>() {
            private int index = 0;
            private int size = indexes.size();

            @Override
            public boolean hasNext() {
                return index != size;
            }

            @Override
            public Item next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return ref[indexes.get(index++)];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
