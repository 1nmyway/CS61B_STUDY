package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>, Iterable<Item>{
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

    public boolean isEmpty(){
        if (size==0)
            return true;
        return false;
    }
    public void addFirst(Item item){
        addFirstResize(size+1);
        items[0]=item;
        size++;
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
        // 检查对象是否为当前类的实例
        if (o instanceof Deque<?>) {
            ArrayDeque<?> other = (ArrayDeque<?>) o;

            // 如果两个队列大小不同，则它们不可能相等
            if (this.size() != other.size()) {
                return false;
            }

            // 使用迭代器遍历两个队列，并比较每个元素
            Iterator<Item> thisIterator = this.iterator();
            Iterator<?> otherIterator = other.iterator();

            while (thisIterator.hasNext() && otherIterator.hasNext()) {
                // 如果在任何位置元素不相等，则返回 false
                if (!thisIterator.next().equals(otherIterator.next())) {
                    return false;
                }
            }
        }else{
            return false;
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
//        LinkedListDeque<Integer> L = new LinkedListDeque<>();
//        ArrayDeque<Integer> A = new ArrayDeque<>();
//        ArrayDeque<Integer> A2 = new ArrayDeque<>();
//        L.addLast(20);
//        L.addFirst(10);
//        A.addLast(20);
//        A.addFirst(10);
//        A2.addLast(20);
//        A2.addFirst(10);
//        System.out.println(A2.equals(A));
//    }
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


