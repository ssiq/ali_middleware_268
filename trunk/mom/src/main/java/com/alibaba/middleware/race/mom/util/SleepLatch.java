package com.alibaba.middleware.race.mom.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wlw on 15-8-17.
 */
public class SleepLatch {

    private ConcurrentLinkedQueue<Thread> concurrentLinkedQueue=new ConcurrentLinkedQueue<>();
    private AtomicBoolean isAwaked=new AtomicBoolean(false);

    public boolean await(long millis)
    {
        Thread thread=Thread.currentThread();
        concurrentLinkedQueue.add(thread);
        if(isAwaked.get())return true;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            return true;
        }
        return false;
    }

    public void wakeAll()
    {
        Thread thread;

        isAwaked.set(true);
        while (true)
        {
            thread=concurrentLinkedQueue.poll();
            if(thread==null)
            {
                break;
            }
            thread.interrupt();
        }
    }

}
