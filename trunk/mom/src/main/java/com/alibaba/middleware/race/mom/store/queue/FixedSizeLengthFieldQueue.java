package com.alibaba.middleware.race.mom.store.queue;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wlw on 15-8-12.
 */
public abstract class FixedSizeLengthFieldQueue extends AbstractMessageQueue {

    private int fixedSize;

    public FixedSizeLengthFieldQueue(int fixedSize) {
        this.fixedSize = fixedSize;
        end=new AtomicInteger(8);
        initialBegin=8;
    }

    @Override
    public void force() {
        if(!isChange())return;
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(0);
        byteBuffer.putInt(end.get());
        byteBuffer.putInt(fixedSize);
        mappedByteBuffer.force();
    }

    @Override
    protected void initHeader() {
        super.initHeader();
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(4);
        fixedSize=byteBuffer.getInt();
    }

    @Override
    public int add(byte[] bytes, int flag) {
        if(bytes.length>fixedSize-manageLength-4)return -1;
        if(end.get()>=fileLength)return -1;
        int nowEnd=end.addAndGet(fixedSize);
        if(nowEnd>fileLength)
            return -1;

        int nowBegin=nowEnd-fixedSize;
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(nowBegin+flagLength);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.putInt(nowEnd - endLength, endInt);
        flag|=useMask;
        byteBuffer.putInt(nowBegin,flag);
        changeContent();
        addedNumber.getAndIncrement();
        return nowBegin;
    }

    @Override
    protected boolean isEndIntOK(int index) {
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(index + fixedSize - endLength);
        return byteBuffer.getInt()==endInt;
    }

    @Override
    public byte[] get(int index) {
        if(index>end.get())return null;
        if((index-initialBegin)%fixedSize!=0)return null;

        if(!isUse(getFlag(index)))return null;
        if(!isEndIntOK(index))return null;

        ByteBuffer byteBuffer=slice();
        byteBuffer.position(index+flagLength);
        int length=byteBuffer.getInt();
        byte[] bytes=new byte[length];
        byteBuffer.get(bytes);
        return bytes;
    }
}
