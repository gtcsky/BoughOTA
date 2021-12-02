package com.phy.app.thread;

import java.util.Comparator;

/**
 * Created by zhoululu on 17/4/30.
 */

public class ComparePriority<T extends PriorityRunnable> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        return Long.valueOf(o1.getPriority()).compareTo(o2.getPriority());
    }

}
