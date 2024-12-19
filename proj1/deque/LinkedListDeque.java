package deque;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<Item> implements Deque<Item>, Iterable<Item>{
    private class IntNode {
        public Item item;
        public IntNode next;
        public IntNode prev;

        public IntNode( IntNode prev,Item i,IntNode next) {
            item = i;
            this.next = next;
            this.prev = prev;
        }
    }

    /* The first item (if it exists) is at sentinel.next. */
    private IntNode sentinel;
    private int size;


    /** Creates an empty timingtest.SLList. */
    public LinkedListDeque() {
        sentinel = new IntNode(null, null,null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public LinkedListDeque(Item x) {
        sentinel = new IntNode(null, null,null);
        sentinel.next = new IntNode(sentinel, null,null);
        sentinel.prev=sentinel;
        size = 1;
    }
    public boolean isEmpty(){
        return size==0;
    }

    /** Adds x to the front of the list. */
    public void addFirst(Item x) {
        IntNode newNode = new IntNode(sentinel, x,sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size = size + 1;
    }

    /** Returns the first item in the list. */
    public Item getFirst() {
        return sentinel.next.item;
    }

    /** Adds x to the end of the list. */
    public void addLast(Item x) {
        IntNode newNode = new IntNode(sentinel.prev,x,sentinel);
        sentinel.prev.next=newNode;
        sentinel.prev=newNode;
        size = size + 1;

    }

    /** returns last item in the list */
    public Item getLast(){
        return sentinel.prev.item;
    }
    public Item removeFirst(){
        if (!isEmpty()) {
            IntNode firstNode = sentinel.next;
            sentinel.next = firstNode.next;
            firstNode.next.prev = sentinel;
            size--;
            return firstNode.item;
        }
        return null;
    }

    public Item removeLast(){
        if (!isEmpty()){
            IntNode lastNode = sentinel.prev;
            lastNode.prev.next=sentinel;
            sentinel.prev=lastNode.prev;
            size--;
            return lastNode.item;
        }
        return null;
    }


    /** Returns the size of the list. */
    public int size() {
        return size;
    }

    public Item getRecursive(int index){
        return getRecursive2(sentinel,index);
    }
    private Item getRecursive2(IntNode p,int index){

        if (index==-1)
            return p.item;
        return getRecursive2(p.next,index-1);
    }

    public Item get(int index) {

        DequeIterator<Item> iterator = new DequeIterator<>();
        int currentIndex = 0;

        while (iterator.hasNext()) {
            if (currentIndex == index) {
                return iterator.next();
            }
            currentIndex++;
            iterator.next(); // 移动到下一个节点
        }
        return null;
    }
    public class DequeIterator<T> implements Iterator<T> {
        private IntNode current;

        public DequeIterator() {
            current = sentinel.next; // 从哨兵节点的下一个节点开始迭代
        }

        @Override
        public boolean hasNext() {
            return current != sentinel && current.item != null; // 检查是否到达哨兵节点且当前节点有数据
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException(); // 如果没有下一个元素，则抛出异常
            }
            T item = (T) current.item; // 这里不需要强制类型转换，因为 T 已经是泛型类型了（但在这里由于泛型擦除，编译器可能仍然需要它）
            current = current.next; // 移动到下一个节点
            return item;
        }

    }

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
    public Iterator<Item> iterator() {
        return new DequeIterator<Item>();
    }
    public void printDeque() {
        if (isEmpty()) {
            return; // 或者抛出一个异常，取决于你的需求
        }
        DequeIterator<Item> iterator = new DequeIterator<>();
        while (iterator.hasNext()) {

            System.out.println(iterator.next()); // 移动到下一个节点
        }
    }
//    public static void main(String[] args) {
//        LinkedListDeque<String> L = new LinkedListDeque<>();
//        ArrayDeque<String> A = new ArrayDeque<>();
//        L.addLast("abc");
//        A.removeFirst();
//        A.addFirst("ab");
//        A.removeFirst();
//        A.addFirst("abcd");
//
//        System.out.println(L.equals(A));
//    }

}

