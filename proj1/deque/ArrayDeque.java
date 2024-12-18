package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>{
    Item items[];
    int size;
    static int actualSize=8;
    public ArrayDeque(){
        items = (Item[]) new Object[8];
        size = 0;
    }
    public void addLast(Item item) {
        if (size == items.length) {
            if (size>=100){
            resize((int) (size * 1.01));
            }
            else{
                resize(size * 2);
            }

        }
        items[size] = item;
        size++;
    }
    public void addFirst(Item item){
        addFirstResize(size+1);
        items[0]=item;
        size++;

    }
    public boolean isEmpty(){
        if (size==0)
            return true;
        return false;
    }
    public void addFirstResize(int capacity){
        Item[] a = (Item[]) new Object[capacity];
        System.arraycopy(items, 0, a, 1, size);
        items = a;
        actualSize=capacity;
    }
    public void resize(int capacity){
        Item[] a = (Item[]) new Object[capacity];
    System.arraycopy(items, 0, a, 0, size);
    items = a;
    actualSize=capacity;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        for(int i=0;i<size;i++){
            System.out.println(items[i]);
        }
    }
    public Item removeFirst() {
        if (!isEmpty()) {
            Item x = items[0];
            removeFirstResize(size);
            size--;
            return x;
        }
        return null;
    }
    public void removeFirstResize(int capacity){
        Item[] a = (Item[]) new Object[capacity];
        System.arraycopy(items, 1, a, 0, size-1);
        items = a;
        actualSize=capacity;
    }
    public Item removeLast(){
        if (!isEmpty()) {
            if((double)size/actualSize<0.25){
                resize((int)(actualSize/2));
            }
            Item x = getLast();
            items[size - 1] = null;
            size--;
            return x;
        }
        return null;
    }
    public Item get(int index){
        if (!isEmpty()&&index<size)
            return items[index];
        return null;
    }
    public Item getLast(){
        return items[size-1];
    }
    public Iterator<Item> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<?> ad = (ArrayDeque<?>) o;
        if (ad.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (ad.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }

    private class ArrayDequeIterator implements Iterator<Item> {
        private int index;

        ArrayDequeIterator() {
            index = 0;
        }

        public boolean hasNext() {
            return index < size;
        }

        public Item next() {
            Item item = get(index);
            index += 1;
            return item;
        }
    }

//    public static void main(String[] args) {
//        ArrayDeque<Integer> L = new ArrayDeque<>();
//        L.addLast(20);
//        L.addLast(30);
//        L.addFirst(10);
//        L.removeFirst();
//        L.isEmpty();
//
//        System.out.println(L.size()+" "+L.get(0)+" "+L.get(1)+" "+L.get(2)+" "+L.size());
//    }

}
