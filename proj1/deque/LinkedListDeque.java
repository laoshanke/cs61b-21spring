package deque;
import java.util.Iterator;


public class LinkedListDeque<T> implements Iterable<T> {
    private node sentinel; //
    private int size;
    private class node{
    T item ;
    node prev;
    node next;
    node(T item, node prev, node next){
        this.item = item;
        this.prev = prev;
        this.next = next;
    }
    }

    public LinkedListDeque(){

        sentinel = new node(null,null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    public T getRecursive(int index){
        if(index < 0 || index >= size){
            return null;
        }
        return getRecursiveHelper(sentinel.next ,index);
    }
    private T getRecursiveHelper(node p,int index){
        if(index == 0){
            return p.item;
        }
        return  getRecursiveHelper(p.next,index-1);
    }
    public void addFirst(T item){
        node p = new node(item,sentinel,sentinel.next);
        sentinel.next = p;
        p.next.prev = p;
        size++;
    }
    public void addLast(T item){
        node p = new node(item,sentinel.prev,sentinel);
        sentinel.prev = p;
        p.prev.next = p;
        size++;
    }
    public boolean isEmpty(){
        return  size == 0;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        StringBuilder stringSB = new StringBuilder();
        stringSB.append("{");

        node p = sentinel.next; // 从sentinel.next开始遍历
        while (p != sentinel) {
            stringSB.append(p.item);
            stringSB.append(" ");
            p = p.next;
        }
        stringSB.append("}");
        stringSB.append("\n");
        System.out.println(stringSB.toString());

    }
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T p = (T) sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return (T) p;
    }
    public T removeLast(){
        if (isEmpty()){
            return null;
        }
        T p = (T) sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return (T) p;
    }
    public T get(int index){
        if (index < 0 || index >= size) {
            return null;
        }
        node p = sentinel.next;
        while (p != sentinel && index > 0) {
            p = p.next;
            index--;
        }
        return p != sentinel ? (T) p.item : null;
    }
    public DequeIterator iterator(){
        return new DequeIterator ();
    }
    public class DequeIterator implements Iterator<T>{
        private node current;
        public DequeIterator(){
            current = sentinel.next;

        }
        public boolean hasNext(){
            return current!= sentinel;
        }
        public T next(){
            T item = (T) current.item;
            current = current.next; // 移动到下一个节点
            return item;
        }
    }
    public boolean equals(Object o){
        if (this == o) {
            return true; // 如果是同一个对象，直接返回true
        }
        if (o == null || getClass() != o.getClass()) {
            return false; // 类型不匹配
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        node p1 = this.sentinel.next;
        node p2 = other.sentinel.next;
        while (p1 != sentinel) {
            if (!p1.item.equals(p2.item)) {
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }

}