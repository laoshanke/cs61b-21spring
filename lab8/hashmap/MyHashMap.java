package hashmap;

import java.util.*;

import static java.lang.Math.abs;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    static final double LOADFACTOR= 0.75;
    double loadFactor;
    static final int INITIALSIZE = 16;
    private int initialSize;
    private int size;
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        initialSize = INITIALSIZE;
        size = 0;
        loadFactor = LOADFACTOR;
        buckets = createTable( initialSize);
    }

    public MyHashMap(int initialSize) {
        this.loadFactor = LOADFACTOR;
        this.initialSize = initialSize;
        size = 0;
        buckets = createTable( initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.loadFactor = maxLoad;
        this.initialSize = initialSize;
        size = 0;
        buckets = createTable( initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    /** Removes all of the mappings from this map. */
    public void clear(){
        size = 0;
        buckets = createTable( initialSize);
    }
    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return get(key) != null;
    }
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        int hashcd   = abs(key.hashCode())% buckets.length;
        Collection<Node> bucket = buckets[hashcd];
        for(Node node : bucket){
            if(node.key.equals(key)){
                return node.value;
            }
        }
        return null;
    }
    /** Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value){
        int hashcd   = abs(key.hashCode())% buckets.length;
        if (buckets[hashcd] == null) {
            buckets[hashcd] = createBucket(); // 初始化桶
        }
        Collection<Node> bucket = buckets[hashcd];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                node.value = value; // 更新值
                return;
            }
        }
        bucket.add(createNode(key, value));
        size++;
        if(( double)size/buckets.length >= loadFactor){
            resize(buckets.length*2);
        }
    }
    public void resize(int newSize){
        Collection<Node>[] newBuckets = createTable(newSize);
        for(Collection<Node> bucket : buckets){
            for(Node node : bucket){
                int newHashcd   = abs(node.key.hashCode())% newBuckets.length;
                if (newBuckets[newHashcd] == null) {
                    newBuckets[newHashcd] = createBucket(); // 初始化桶
                }
                newBuckets[newHashcd].add(node);
            }
        }
        buckets = newBuckets;
    }
    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet(){
        Set<K> keys = new HashSet<>();
        for(Collection<Node> bucket : buckets){
            for(Node node : bucket){
                keys.add(node.key);
            }
        }
        return keys;
    }
    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key){
        int hashcd   = abs(key.hashCode())% buckets.length;
        Collection<Node> bucket = buckets[hashcd];
        for(Node node : bucket){
            if(node.key.equals(key)){
                bucket.remove(node);
                size = size - 1;
                return node.value;
            }
        }
        return null;
    }
    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value){
        int hashcd   = abs(key.hashCode())% buckets.length;
        Collection<Node> bucket = buckets[hashcd];
        for(Node node : bucket){
            if(node.key.equals(key) && node.value.equals(value))
                bucket.remove(node);
        }
        return null;
    }
    private class HashMapIterator<K> implements Iterator<K> {
        private int currentBucketIndex = 0;
        private Iterator<Node> currentIterator;

        public HashMapIterator() {
            advanceToNextBucket();
        }

        private void advanceToNextBucket() {
            while (currentBucketIndex < buckets.length) {
                Collection<Node> bucket = buckets[currentBucketIndex];
                if (bucket != null && !bucket.isEmpty()) {
                    currentIterator = bucket.iterator();
                    currentBucketIndex++;
                    return;
                }
                currentBucketIndex++;
            }
            currentIterator = null;
        }

        @Override
        public boolean hasNext() {
            if (currentIterator == null) return false;
            if (currentIterator.hasNext()) return true;
            advanceToNextBucket();
            return currentIterator != null && currentIterator.hasNext();
        }

        @Override
        public K next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (K) currentIterator.next().key;
        }
    }
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }
    }

