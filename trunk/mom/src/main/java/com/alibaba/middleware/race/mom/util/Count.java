package com.alibaba.middleware.race.mom.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wlw on 15-8-19.
 */
public class Count {
    private ScheduledExecutorService scheduledExecutorService;
    private AtomicLong time=new AtomicLong(0);
    private AtomicInteger times=new AtomicInteger(0);

    public Count(final String str) {
        this.scheduledExecutorService= Executors.newScheduledThreadPool(0);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(str+" ave time:"+(time.doubleValue()/times.doubleValue()));
            }
        },0,1, TimeUnit.SECONDS);
    }

    public void add(long t)
    {
        time.getAndAdd(t);
        times.incrementAndGet();
    }
}
