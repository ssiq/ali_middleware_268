package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.model.IndexMessage;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-18.
 */
public class Item {
    private MappedByteBuffer mappedByteBuffer;
    private AtomicInteger usingNum;
    private int size;

    public Item(MappedByteBuffer mappedByteBuffer,AtomicInteger usingNum) {
        this.mappedByteBuffer = mappedByteBuffer;
        this.usingNum=usingNum;
        this.size=mappedByteBuffer.limit();
    }

    private AtomicInteger end=new AtomicInteger(4);
    private AtomicInteger realEnd=new AtomicInteger(4);
    private final int manageLength=12;

    private ByteBuffer slice(int position)
    {
        ByteBuffer byteBuffer=mappedByteBuffer.slice();
        byteBuffer.position(position);
        return byteBuffer;
    }

    private int endInt=0x3C3C3C3C;
    private int beginFlag=0x1;
    private int put(byte[]bytes)
    {
        int totalLen=bytes.length+manageLength;
        int nowEnd=end.addAndGet(totalLen);
        if(nowEnd>size)
        {
            return -1;
        }
        realEnd.addAndGet(totalLen);
        int nowBegin=nowEnd-bytes.length;
        ByteBuffer byteBuffer=slice(nowBegin);
        byteBuffer.putInt(beginFlag);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.putInt(endInt);
        return nowBegin;
    }

    private class InnerIndexMessage implements IndexMessage
    {

        private int index;
        private MessageQueue messageQueue;
        private AtomicBoolean isUsedBool=new AtomicBoolean(false);

        public InnerIndexMessage(int index,MessageQueue messageQueue) {
            this.messageQueue=messageQueue;
            this.index = index;
        }


        @Override
        public boolean isUsed() {
            return this.isUsedBool.get();
        }

        @Override
        public byte[] getMessage() {
            return this.messageQueue.get(index);
        }

        private void setUsed()
        {
//            this.isUsedBool.set(true);
//            this.messageQueue.remove(this.index);
//            this.messageQueue.pop();
        }

        private AtomicInteger leftToSend=new AtomicInteger(0);
        private AtomicBoolean end=new AtomicBoolean(false);
        @Override
        public void up() {
            leftToSend.incrementAndGet();
        }

        @Override
        public void down() {
            if(this.leftToSend.decrementAndGet()==0&&this.end.get())
            {
                this.setUsed();
            }
        }

        @Override
        public void downOnce(AtomicBoolean once) {
            if(once.get()||!once.compareAndSet(false,true))return;
            this.down();
        }

        @Override
        public void endUp() {
            this.end.set(true);
            if(this.leftToSend.get()==0)
            {
                this.setUsed();
            }
        }

        @Override
        public int getIndex() {
            return index;
        }

    }

    public IndexMessage putMessage(Message message,byte[] bytes)
    {
        int index=put(bytes);
        if(index==-1)
        {
            return null;
        }else{
            return null;
        }
    }

    private Item next;

    public Item getNext() {
        return next;
    }

    public void setNext(Item next) {
        this.next = next;
    }
}
