package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.util.Sem;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wlw on 15-8-10.
 */
public abstract class LengthFieldQueue extends AbstractMessageQueue {

//    @Override
//    public int add(byte[] bytes, int flag) {
//        if(end.get()>fileLength)return -1;
//        int nowEnd=end.addAndGet(bytes.length+4+manageLength);
//        if(nowEnd>fileLength)
//        {
//            end.getAndAdd(-bytes.length-4);
//            return -1;
//        }
//        int nowBegin=nowEnd-bytes.length-4-manageLength;
//        ByteBuffer byteBuffer=slice();
//        byteBuffer.position(nowBegin);
//        flag|=useMask;
//        byteBuffer.putInt(flag);
//        byteBuffer.putInt(bytes.length);
//        byteBuffer.put(bytes);
//        byteBuffer.putInt(endInt);
//        addedNumber.getAndIncrement();
//        changeContent();
//        return nowBegin;
//    }

    private Lock lock=new ReentrantLock();


//    private static ExecutorService executorService= Executors.newCachedThreadPool();
    private AtomicBoolean onceForced=new AtomicBoolean(false);

    private static AtomicInteger idc=new AtomicInteger(0);
    protected int id=idc.getAndIncrement();

    @Override
    public int add(byte[] bytes, int flag) {
        if(isFull())return -1;
        int totalLen=bytes.length+manageLength+4;
        int nowEnd=end.addAndGet(totalLen);
        if(nowEnd>fileLength){
            full.set(true);
            if(onceForced.compareAndSet(false,true))
            {
////                System.out.println("id :"+id+" end");
                sem.end();
//                executorService.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        doForce();
//                    }
//                });
            }
            return -1;
        }
        addOne();
        reallyEnd.addAndGet(totalLen);
        sem.up();
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(nowEnd-totalLen);
        byteBuffer.putInt(flag|useMask);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.putInt(endInt);
        sem.down();
        changeContent();
        return nowEnd-totalLen;
    }

    //the method should never use
    @Override
    protected boolean isEndIntOK(int index) {
        return false;
    }

    @Override
    public byte[] get(int index) {
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(index);
        int flag=byteBuffer.getInt();
        if(!isUse(flag))return null;
        int length=byteBuffer.getInt();
        int nowPos=byteBuffer.position();
        if(endInt!=byteBuffer.getInt(nowPos+length))return null;
        byte[]bytes=new byte[length];
        byteBuffer.get(bytes);
        return bytes;
    }
}
