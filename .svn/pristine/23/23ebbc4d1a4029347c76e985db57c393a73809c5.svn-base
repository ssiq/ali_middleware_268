package com.alibaba.middleware.race.mom.store.queue;

import java.nio.ByteBuffer;

/**
 * Created by wlw on 15-8-10.
 */
public abstract class FixedItemSizeQueue extends AbstractMessageQueue {

    private int itemSize;
    private ThreadLocal<byte[]> byteBuffer;

    public FixedItemSizeQueue(final int itemSize)
    {
        this.itemSize=itemSize;
        fullOffSet=0;
        byteBuffer=new ThreadLocal<byte[]>(){
            @Override
            protected byte[] initialValue() {
                return new byte[itemSize-flagLength-endLength];
            }
        };
    }

    protected byte[] getBuffer()
    {
        return byteBuffer.get();
    }

    protected int getItemSize()
    {
        return itemSize;
    }

    protected int getRealItemSize()
    {
        return itemSize-flagLength-endLength;
    }

    //返回的值是flag的开始
    @Override
    public int add(byte[] bytes, int flag) {
        if(bytes.length+manageLength>itemSize)return -1;
        int nowEnd=end.addAndGet(itemSize);
        if(nowEnd>fileLength-itemSize)
        {
            end.addAndGet(-itemSize);
            return -1;
        }

        flag|=useMask;
        int nowBegin=nowEnd-itemSize;
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(nowBegin+4);
        byteBuffer.put(bytes);
        byteBuffer.putInt(endInt);
        byteBuffer.putInt(nowBegin,flag);
        changeContent();
        addedNumber.getAndIncrement();
        return nowBegin;
    }

    @Override
    protected boolean isEndIntOK(int index) {
        ByteBuffer byteBuffer=slice();
        byteBuffer.position(index + itemSize - endLength);
        return byteBuffer.getInt()==endInt;
    }

    @Override
    public byte[] get(int index) {
        if((index-initialBegin)%itemSize!=0)return null;

        int flag=getFlag(index);
        if(!isUse(flag))return null;

        if(!isEndIntOK(index))return null;

        ByteBuffer byteBuffer=slice();
        byte[] bytes=getBuffer();
        byteBuffer.position(index+flagLength);
        byteBuffer.get(bytes);
        return bytes;
    }

    protected void add(ByteBuffer byteBuffer,byte[]bytes)
    {
        add(byteBuffer,0,bytes);
    }

    protected void add(ByteBuffer byteBuffer,int flag,byte[]bytes)
    {
        flag|=useMask;
        byteBuffer.putInt(flag);
        byteBuffer.put(bytes);
        byteBuffer.putInt(endInt);
        changeContent();
    }
}
