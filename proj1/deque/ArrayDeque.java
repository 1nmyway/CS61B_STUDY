package deque;

import java.util.Iterator;

public class ArrayDeque<T> {
    T items[];
    int size;
    static int actualSize=8;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
    }
    public void addLast(T item) {
        if (size == items.length) {
            if (size>=100){
            resize((int) (size * 1.01));
            actualSize = (int) (size * 1.01);
            }
            else{
                resize(size * 2);
                actualSize = (int) (size * 2);
            }

        }
        items[size] = item;
        size++;
    }
    public void addFirst(T item){
        firstResize(size+1);
        actualSize++;
        items[0]=item;
        size++;

    }
    public boolean isEmpty(){
        if (size==0)
            return true;
        return false;
    }
    public void firstResize(int capacity){
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 1, size);
        items = a;
    }
    public void resize(int capacity){
    T[] a = (T[]) new Object[capacity];
    System.arraycopy(items, 0, a, 0, size);
    items = a;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        for(int i=0;i<size;i++){
            System.out.println(items[i]);
        }
    }
    public T removeFirst() {
        if (!isEmpty()) {
            T x = items[0];
            items[0] = null;
            size--;
            return x;
        }
        return null;
    }
    public T removeLast(){
        if (!isEmpty()) {
            if((double)size/actualSize<0.25){
                resize((int)(actualSize/2));
                actualSize = (int)(actualSize/2);
            }
            T x = getLast();
            items[size - 1] = null;
            size--;
            return x;
        }
        return null;
    }
    public T get(int index){
        if (!isEmpty())
            return items[index];
        return null;
    }
    public T getLast(){
        return items[size-1];
    }

//    public static void main(String[] args) {
//        ArrayDeque<Integer> L = new ArrayDeque<>();
//        L.addLast(10);
//        L.addLast(20);
//        L.addLast(30);
//        L.addLast(40);
//        L.addLast(50);
//        L.addLast(50);
//        L.addLast(50);
//        L.addLast(50);
//        L.addLast(50);
//
//       L.removeLast();
//        L.removeLast();
//        L.removeLast();
//        L.removeLast();
//        L.removeLast();
//        L.removeLast();
//        L.removeLast();
//
//        System.out.println(L.get(0)+" "+actualSize+" "+L.size());
//    }

}
