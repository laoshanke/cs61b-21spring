package bstmap;


import java.util.HashSet;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K> , V> implements Map61B<K, V> {
    private Node root;
    private int size;

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    public class Node{
        public K key;
        public V value;
        public Node left;
        public Node right;
        public Node parent;

        public Node(K key, V value, Node parent){
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.left = null;
            this.right = null;
        }
        public void NodeChild(Node left, Node right){
            this.left = left;
            this.right = right;
        }

    }
    public BSTMap(){
        root = new Node(null, null, null);
        size = 0;
    }
    private void insert( Node root, K key, V value){
        if( root == null){
            root = new Node(key, value, null);
        }else if( key.compareTo(root.key) < 0){
            insert(root.left, key, value);
        }else if ( key.compareTo(root.key) > 0){
            insert(root.right, key, value);
        }else{
            root.value = value;
        }
        size++;
    }
    private V node_get( Node t, K key){
        if( t.key == null){
            return null;
        }else if( key.compareTo(t.key) < 0){
            return node_get(t.left, key);
        }else if( key.compareTo(t.key) > 0){
            return node_get(t.right, key);
        }else{
            return t.value;
        }
    }

    @Override
    public void clear(){
        root = null;    }
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return node_get(root, key) != null;
    }
    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key){
        return node_get(root, key);
    }

    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }
    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        insert(root, key, value);
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet(){
        HashSet <K> keys = new HashSet<>();
        keyset_Helper(keys, root);
        return keys;
    }
    private void keyset_Helper(HashSet <K> keys, Node t){
        if( t != null && t.key != null){
            keys.add(t.key);
            keyset_Helper(keys, t.left);
            keyset_Helper(keys, t.right);
        }
    }


    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key){
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        throw new UnsupportedOperationException();
    }


}
