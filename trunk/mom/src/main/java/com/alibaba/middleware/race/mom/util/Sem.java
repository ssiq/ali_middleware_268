package com.alibaba.middleware.race.mom.util;

import com.alibaba.middleware.race.mom.store.util.StringSerilizer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-15.
 */
public class Sem
{
    private CountDownLatch countDownLatch=new CountDownLatch(1);
    private AtomicInteger upNumber=new AtomicInteger(0);
    private AtomicInteger downNumber=new AtomicInteger(0);
    private AtomicBoolean upEnd=new AtomicBoolean(false);
//    private static AtomicInteger atomicInteger=new AtomicInteger(0);
//    private int id=atomicInteger.incrementAndGet();

    public Sem() {
//        System.out.println("create sem "+id);
    }

    public void await()
    {
        if(upNumber.get()<=downNumber.get()){
//            System.out.println("awaited one "+id);
            return;
        }

        while (true)
        {
            try {
                countDownLatch.await();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("awaited one "+id);
    }

    public void up()
    {
//        System.out.println(""+id+" up");
        upNumber.incrementAndGet();
//        System.out.println("" + id + "end up");
    }

    public void down()
    {
//        System.out.println("down");
        downNumber.incrementAndGet();
        if(downNumber.get()>=upNumber.get()&&upEnd.get())
        {
//            System.out.println("awaited one "+id);
            countDownLatch.countDown();
        }
//        System.out.println("end down");
    }

    public void end()
    {
        upEnd.set(true);
        if(downNumber.get()>=upNumber.get())
        {
//            System.out.println("awaited one:"+id);
            countDownLatch.countDown();
        }
    }

}
