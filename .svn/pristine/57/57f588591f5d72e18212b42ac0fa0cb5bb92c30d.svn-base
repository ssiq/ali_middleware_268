package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.util.Sem;
import com.alibaba.middleware.race.mom.util.SleepLatch;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wlw on 15-8-9.
 *
 * 每一个区域的开始都有一个4字节(32位)的flag,flag的第0位用于表明一个区域是否使用,其他的位可以由子类定义
 *
 */
public abstract class AbstractMessageQueue implements MessageQueue{

    protected int forceLevel;
    protected String fileName;
    protected MappedByteBuffer mappedByteBuffer;
    protected AtomicInteger end=new AtomicInteger(4);
//    protected AtomicInteger begin=new AtomicInteger(12);
    protected AtomicInteger addedNumber=new AtomicInteger(0);
    protected int initialBegin=4;
    protected int fullOffSet=200;
    protected int fileLength;
    private AtomicBoolean change=new AtomicBoolean(false);
    protected RandomAccessFile randomAccessFile;
    protected int useMask=0x1;
    protected static int flagLength=4;
    protected int endInt=0xCC;
    protected static int endLength=4;
    protected static int manageLength=flagLength+endLength;
    protected boolean isNew=false;
    protected AtomicInteger reallyEnd=new AtomicInteger(initialBegin);

    protected int getFlag(int position)
    {
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(position);
        return byteBuffer.getInt();
    }

    //需要保证只有一个线程调用
    protected void setFlag(int mask,int position)
    {
        int flag=getFlag(position);
        flag|=mask;
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(position);
        byteBuffer.putInt(flag);
    }

    protected boolean isMask(int flag,int mask)
    {
        return (flag&mask)!=0;
    }

    protected boolean isUse(int flag) {
        return isMask(flag, useMask);
    }

    @Override
    public void remove(int index) {
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(index);
        byteBuffer.putInt(0);
    }

    protected ByteBuffer slice()
    {
        return mappedByteBuffer.slice();
    }

    private AtomicInteger changeContentNumber=new AtomicInteger(0);
    protected void changeContent()
    {
        changeContentNumber.incrementAndGet();
        change.set(true);
    }

    protected void addOne()
    {
        addedNumber.incrementAndGet();
    }

    protected abstract boolean isEndIntOK(int index);

    protected void initHeader()
    {
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(0);
//        addedNumber=new AtomicInteger(byteBuffer.getInt());
//        begin=new AtomicInteger(byteBuffer.getInt());
        end=new AtomicInteger(byteBuffer.getInt());
        reallyEnd.set(end.get());
    }

    @Override
    public abstract int add(byte[] bytes, int flag);

    @Override
    public int add(byte[] bytes) {
        return add(bytes,0);
    }

    @Override
    public void pop() {
//        changeContent();
        addedNumber.decrementAndGet();
//        System.out.println("left number "+addedNumber.get());
        if(addedNumber.get()==0)
        {
            if(isNew&isForced())
            {
                remove();
            }else if(!isNew){
                remove();
            }
        }
    }

    @Override
    public abstract byte[] get(int index);

    protected boolean isChange()
    {
        return change.compareAndSet(true,false);
    }

    private int max_put_number=20;

    private static AtomicInteger forceTimes =new AtomicInteger(0);
    private static AtomicInteger oneForceTime=new AtomicInteger(0);
    private static AtomicLong waitTime=new AtomicLong(0);
    private static AtomicInteger waitTimes=new AtomicInteger(0);
    private static AtomicLong forceTime=new AtomicLong(0);
    private static AtomicLong lastForceTime=new AtomicLong(0);
    private static AtomicBoolean firstForce=new AtomicBoolean(false);
    private static AtomicLong totalGetime=new AtomicLong(0);
    private static ScheduledExecutorService showTimes= Executors.newSingleThreadScheduledExecutor();
//    static {
//        showTimes.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
////                System.out.println("force times:" + forceTimes.get());
////                System.out.println("one force times:"+oneForceTime.get());
////                System.out.println("wait time:"+waitTime.doubleValue()/waitTimes.doubleValue());
//                System.out.println("total force time:"+forceTime.get());
//                System.out.println("average force time:"+forceTime.doubleValue()/forceTimes.doubleValue());
//                System.out.println("average gad time:"+totalGetime.doubleValue()/(forceTimes.get()-1));
//            }
//        },0,1,TimeUnit.SECONDS);
//    }

    private CountDownLatch waitForceBegin =new CountDownLatch(1);
//    protected CountDownLatch waitEndForce=new CountDownLatch(1);
//    private SleepLatch waitForceBegin=new SleepLatch();
    private SleepLatch waitForceEnd=new SleepLatch();
    private CountDownLatch twiceEnd=new CountDownLatch(1);
    private AtomicBoolean twiceIn=new AtomicBoolean(false);

    @Override
    public void force() {
        if(!isChange())return;
        CountDownLatch inTwiceEnd=twiceEnd;
        AtomicBoolean inTwiceIn=twiceIn;
        waitTimes.incrementAndGet();
        while (true)
        {
            try {
                long btime=System.currentTimeMillis();
                if(waitForceBegin.await(800,TimeUnit.MILLISECONDS))
                {

//                    break;
//                    System.out.println("I await :"+(System.currentTimeMillis()-btime));
//                    waitEndForce.await();
                    waitTime.getAndAdd(System.currentTimeMillis()-btime);
                    waitForceEnd.await(10000);
                }else{
                    if(waitForceBegin.getCount()==0)
                    {
//                        waitEndForce.await();
                        waitTime.getAndAdd(System.currentTimeMillis()-btime);
                        waitForceEnd.await(10000);
                        return;
                    }
                    if(inTwiceIn.compareAndSet(false,true))
                    {
                        twiceEnd=new CountDownLatch(1);
                        twiceIn=new AtomicBoolean(false);
                        doForceAction();
                        inTwiceEnd.countDown();
                        oneForceTime.incrementAndGet();
                    }else{
                        inTwiceEnd.wait();
                    }

//                    System.out.println("force time:" + forceTimes.incrementAndGet());
//                    break;
                }
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        CountDownLatch countDownLatch=waitForceBegin;
//        if(changeContentNumber.get()>=max_put_number)
//        {
//            waitForceBegin=new CountDownLatch(1);
//            changeContentNumber.set(0);
//        }else{
//            try {
//                if(countDownLatch.await(150, TimeUnit.MILLISECONDS))
//                    return;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            mappedByteBuffer.force();
//            System.out.println("force time:"+forceTimes.incrementAndGet());
////            System.out.println("in force one");
//        }
//        ByteBuffer byteBuffer=slice();
//        byteBuffer.position(0);
////        byteBuffer.putInt(addedNumber.get());
////        byteBuffer.putInt(begin.get());
////        byteBuffer.putInt(end.get());
//        mappedByteBuffer.force();
//        System.out.println("force time:" + forceTimes.incrementAndGet());
//        countDownLatch.countDown();
////        System.out.println("force one");
    }

    //所有的force操作都要调用这个函数
    protected void doForceAction()
    {
        long time=System.currentTimeMillis();
        if(firstForce.compareAndSet(false,true))
        {
            lastForceTime.set(time);
        }else{
            totalGetime.getAndAdd(time-lastForceTime.get());
            lastForceTime.set(time);
        }
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(0);
        byteBuffer.putInt(reallyEnd.get());
        mappedByteBuffer.force();
        forceTime.getAndAdd(System.currentTimeMillis() - time);
        forced.set(true);
        forceTimes.incrementAndGet();
    }

    protected Sem sem=new Sem();
    private AtomicBoolean forced=new AtomicBoolean(false);
    protected void doForce()
    {
//        System.out.println("doForce");
        sem.await();
//        System.out.println("force");
        waitForceBegin.countDown();
//        waitForceBegin.wakeAll();
        long time=System.currentTimeMillis();
        if(firstForce.compareAndSet(false,true))
        {
            lastForceTime.set(time);
        }else{
            totalGetime.getAndAdd(time-lastForceTime.get());
            lastForceTime.set(time);
        }
        doForceAction();
//        waitEndForce.countDown();
        forceTime.getAndAdd(System.currentTimeMillis()-time);
        waitForceEnd.wakeAll();
        forced.set(true);
        forceTimes.incrementAndGet();
//        System.out.println("force time:" + );
    }

    private boolean isForced()
    {
        return forced.get();
    }

    protected AtomicBoolean full=new AtomicBoolean(false);
    @Override
    public boolean isFull() {
        return full.get();
    }

    @Override
    public boolean isEmpty() {
        return addedNumber.get()<=0;
    }

    @Override
    public int getForceLevel() {
        return forceLevel;
    }

    @Override
    public int leftSpace() {
        return fileLength-end.get();
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    private AtomicBoolean closed=new AtomicBoolean(false);
    @Override
    public void close() {
        try {
            if(!closed.compareAndSet(false,true))return;
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean isClose()
    {
        return closed.get();
    }

    @Override
    public abstract void start() throws IOException;

    private CountDownLatch countDownLatch=new CountDownLatch(1);
    @Override
    public void waitStarted() {
        while(true)
        {
            try {
                countDownLatch.await();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void haveStarted()
    {
        countDownLatch.countDown();
    }

    protected boolean isStart()
    {
        return countDownLatch.getCount()==0;
    }

    protected ByteBuffer allocRealSize(int size)
    {
        int nowEnd=end.addAndGet(size);
        if(nowEnd>fileLength)
        {
            end.addAndGet(-size);
            return null;
        }
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(nowEnd-size);
        return byteBuffer;
    }

    @Override
    public ByteBuffer allocSpace(int size) {
        return allocRealSize(size+manageLength);
    }

    private static AtomicInteger removeNumber=new AtomicInteger(0);
    private static ScheduledExecutorService showRemoveNumberService=
            Executors.newSingleThreadScheduledExecutor();
    static {
//        showRemoveNumberService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("remove number:"+removeNumber.get());
//            }
//        },0,2,TimeUnit.SECONDS);
    }

    private AtomicBoolean removed=new AtomicBoolean(false);
    @Override
    public void remove() {
        if(!closed.get())close();
        if(removed.compareAndSet(false,true))
        {
            File file=new File(fileName);
            if(file.exists())file.delete();
//            System.out.println("remove one "+fileName);
            removeNumber.incrementAndGet();
        }
    }

    public void testForce()
    {
        mappedByteBuffer.force();
    }
}