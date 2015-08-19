package com.alibaba.middleware.race.rpc.async;

import java.util.concurrent.*;

/**
 * Created by wlw on 15-7-30.
 */
public class AsynFuture implements Future {

    private Object o;
    private CountDownLatch countDownLatch;
    private boolean done=false;

    public AsynFuture() {
        countDownLatch=new CountDownLatch(1);
    }

    public boolean cancel(boolean b) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return done;
    }

    public Object get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return o;
    }

    public Object get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        if(countDownLatch.await(l,timeUnit))
        {
            return o;
        }else{
            throw new TimeoutException();
        }
    }

    public void setResult(Object o)
    {
        this.o=o;
        countDownLatch.countDown();
        done=true;
    }

}
