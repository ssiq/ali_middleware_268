package com.alibaba.middleware.race.mom.util;

import com.alibaba.middleware.race.mom.util.SimpleQueue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-15.
 */
public class WrapConcurrentListQueue<T> implements SimpleQueue<T> {

    private ConcurrentLinkedQueue<T> concurrentLinkedQueue;
    private AtomicInteger atomicInteger=new AtomicInteger(0);

    public WrapConcurrentListQueue(ConcurrentLinkedQueue<T> concurrentLinkedQueue) {
        this.concurrentLinkedQueue = concurrentLinkedQueue;
    }

    @Override
    public boolean put(T t) {
        boolean res=concurrentLinkedQueue.add(t);
        if(res)
        {
            atomicInteger.incrementAndGet();
        }
        return res;
    }

    @Override
    public boolean putOnce(T t, AtomicBoolean once) {
        if(once.get()||!once.compareAndSet(false,true))        return false;
        return put(t);
    }

    @Override
    public T get() {
        atomicInteger.decrementAndGet();
        T res=concurrentLinkedQueue.poll();
        if(res==null)
        {
            atomicInteger.incrementAndGet();
        }
        return res;
    }

    @Override
    public int size() {
        return atomicInteger.get();
    }
}
