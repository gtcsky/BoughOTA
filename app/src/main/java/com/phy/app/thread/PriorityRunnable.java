package com.phy.app.thread;

/**
 * Created by zhoululu on 17/4/30.
 */

public abstract class PriorityRunnable implements Runnable {


    public long priority;

    public PriorityRunnable(long priority) {
        this.priority = priority;
    }


    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

}
