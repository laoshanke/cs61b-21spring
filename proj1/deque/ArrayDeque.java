package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int capacity;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 7;
        nextLast = 0;
        capacity = 8;
    }
@Override
    public void addFirst(T item) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + capacity) % capacity;
        size++;
    }

    private void resize(int newCapacity) {
        T[] newItems = (T[]) new Object[newCapacity];
        int current = (nextFirst + 1) % capacity;
        for (int i = 0; i < size; i++) {
            newItems[i] = items[current];
            current = (current + 1) % capacity;
        }
        nextFirst = newCapacity - 1;
        nextLast = size;
        items = newItems;
        capacity = newCapacity;
    }
@Override
    public void addLast(T item) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % capacity;
        size++;
    }
@Override
    public boolean isEmpty() {
        return size == 0;
    }
@Override
    public int size() {
        return size;
    }
@Override
    public void printDeque() {
        StringBuilder sb = new StringBuilder();
        int current = (nextFirst + 1) % capacity;
        for (int i = 0; i < size; i++) {
            sb.append(items[current]).append(" ");
            current = (current + 1) % capacity;
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        System.out.println(sb.toString());
    }
@Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = (nextFirst + 1) % capacity;
        T item = items[nextFirst];
        items[nextFirst] = null;
        size--;
        if (capacity >= 16 && size < capacity / 4) {
            resize(capacity / 2);
        }
        return item;
    }
@Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = (nextLast - 1 + capacity) % capacity;
        T item = items[nextLast];
        items[nextLast] = null;
        size--;
        if (capacity >= 16 && size < capacity / 4) {
            resize(capacity / 2);
        }
        return item;
    }
@Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(nextFirst + 1 + index) % capacity];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int currentIndex;
        private int count;

        public ArrayDequeIterator() {
            currentIndex = (nextFirst + 1) % capacity;
            count = 0;
        }

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = items[currentIndex];
            currentIndex = (currentIndex + 1) % capacity;
            count++;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayDeque<?> other = (ArrayDeque<?>) o;
        if (size != other.size) return false;
        Iterator<?> thisIterator = iterator();
        Iterator<?> otherIterator = other.iterator();
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            Object thisItem = thisIterator.next();
            Object otherItem = otherIterator.next();
            if (thisItem == null && otherItem != null) return false;
            if (thisItem != null && !thisItem.equals(otherItem)) return false;
        }
        return true;
    }
}
