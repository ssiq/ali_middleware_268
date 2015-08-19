package com.alibaba.middleware.race.mom.model;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wlw on 15-8-5.
 */
public class OnceSempore {
    private Semaphore semaphore;
    private AtomicBoolean released = new AtomicBoolean(false);

    public OnceSempore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release()
    {
        if(this.released.compareAndSet(false, true))
        {
            semaphore.release();
        }
    }
}
