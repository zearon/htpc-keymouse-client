package com.zearon.tvasistant;

import android.util.Log;

import java.util.Collection;
import java.util.Vector;

/**
 * Created by zhiyuangong on 17/5/11.
 */
public class StringRingQueue {
    private int capacity;
    private int head = 0;
    private int size = 0;
    private Vector<String> container;
    private String toStringPrefix;

    public StringRingQueue(int capacity, String toStringPrefix) {
        this.capacity = capacity;
        this.toStringPrefix = toStringPrefix;
        container = new Vector<>(capacity);
    }

    public synchronized void add(String str) {
        if (size < capacity) {
            container.add(str);
            ++ size;
        } else {
            container.set(head, str);
            head = ++head % capacity;
        }
    }

    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(toStringPrefix);

//        boolean notToEnd = true;
//        for ( int i = head; (i < size && notToEnd) || (i < head && !notToEnd); i = (++i % size) ) {
        // Reverse iterate the container and append the element string to en end of the string buffer.
        boolean notToHead = true;
        int firstVisited = (head - 1 + size) % size;
        for ( int i = firstVisited ; (notToHead && i >= 0) || (!notToHead && i > firstVisited);
              i = ((--i + size) % size) ) {

            sb.append(container.get(i));
            if (i == 0) {
                notToHead = false;
            }
        }
        return sb.toString();
    }
}
