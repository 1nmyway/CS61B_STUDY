package deque;
import deque.ArrayDeque;

import java.util.Comparator;
import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> implements Deque<T>{
    private Comparator<T> comparator;
    private T maxElement;

    // 构造函数，接受一个Comparator来初始化双端队列
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
        this.maxElement = null;
    }

    // 重写addLast方法，更新最大元素
    @Override
    public void addLast(T item) {
        super.addLast(item);
        updateMaxElement();
    }

    // 重写addFirst方法，更新最大元素
    @Override
    public void addFirst(T item) {
        super.addFirst(item);
        updateMaxElement();
    }
    public T getFirst(){
        return super.get(0);
    }

    // 更新最大元素的方法
    private void updateMaxElement() {
        if (isEmpty()) {
            maxElement = null;
        } else if (maxElement == null) {
            maxElement = getFirst();
        } else {
            T currentMax = maxElement;
            for (T item : this) {
                if (comparator.compare(item, currentMax) > 0) {
                    currentMax = item;
                }
            }
            maxElement = currentMax;
        }
    }

    // 返回由构造函数中的Comparator控制的双端队列中的最大元素
    public T max() {
        return maxElement;
    }

    // 返回由参数Comparator控制的双端队列中的最大元素
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T currentMax = getFirst();
        for (T item : this) {
            if (c.compare(item, currentMax) > 0) {
                currentMax = item;
            }
        }
        return currentMax;
    }

    // 测试MaxArrayDeque
//    public static void main(String[] args) {
//        Comparator<Integer> intComparator = Integer::compareTo;
//        MaxArrayDeque<Integer> maxDeque = new MaxArrayDeque<>(intComparator);
//
//        maxDeque.addLast(3);
//        maxDeque.addLast(1);
//        maxDeque.addLast(4);
//        maxDeque.addFirst(2);
//
//        System.out.println("Max element (using constructor comparator): " + maxDeque.max()); // 输出 4
//
//        Comparator<Integer> reverseIntComparator = (a, b) -> b.compareTo(a);
//        System.out.println("Max element (using provided comparator): " + maxDeque.max(reverseIntComparator)); // 输出 4
//
//        maxDeque.addLast(5);
//        System.out.println("Max element (using constructor comparator after adding 5): " + maxDeque.max()); // 输出 5
//    }
}