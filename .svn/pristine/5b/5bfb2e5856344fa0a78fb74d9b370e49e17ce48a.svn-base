package com.alibaba.middleware.race.mom.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-13.
 */
public class DistributedQueue<T> implements SimpleQueue<T>{

    private int size;
    private int mask;
    private BlockingQueue<T>[] blockingQueues=new BlockingQueue[size];
    private AtomicInteger putIndex=new AtomicInteger(0);
    private AtomicInteger getIndex=new AtomicInteger(0);

    public DistributedQueue(int bitNumber) {
        this.size = (1<<bitNumber);
        this.mask = ~((-1)<<bitNumber);
        for(int i=0;i<size;++i)
        {
            blockingQueues[i]=new LinkedBlockingQueue();
        }
    }

    public boolean put(T t)
    {
        int index=(putIndex.getAndIncrement())&mask;
        while (true)
        {
            try {
                blockingQueues[index].put(t);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean putOnce(T t, AtomicBoolean once) {
        if(once.compareAndSet(false,true))
        {
            return put(t);
        }else{
            return false;
        }
    }

    public T get()
    {
        int index=(getIndex.getAndIncrement())&mask;
        T t;
        while (true)
        {
            try {
                t=blockingQueues[index].take();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    @Override
    public int size() {
        return 0;
    }

    public T get(int index)
    {
        T t;
        while (true)
        {
            try {
                t=blockingQueues[index].take();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return t;
    }
}
