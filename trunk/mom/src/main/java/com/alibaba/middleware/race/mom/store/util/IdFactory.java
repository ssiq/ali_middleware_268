package com.alibaba.middleware.race.mom.store.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wlw on 15-8-11.
 */
public class IdFactory {

    private String name;
    private MappedByteBuffer mappedByteBuffer;
    private AtomicLong nowId;

    private long getId()
    {
        ByteBuffer byteBuffer=mappedByteBuffer.slice();
        byteBuffer.position(0);
        return byteBuffer.getLong();
    }

    private void setId(long id)
    {
        ByteBuffer byteBuffer=mappedByteBuffer.slice();
        byteBuffer.position(0);
        byteBuffer.putLong(id);
    }

    public IdFactory(String path,String name) throws IOException{
        if(!path.endsWith("/"))
        {
            path+="/";
        }
        int fileLength=8;
        this.name = path+name;
        File file=new File(path+"id-"+name);
        boolean isNew=false;
        if(!file.exists())
        {
            isNew=true;
        }
        RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
        if(isNew)
        {
            randomAccessFile.setLength(fileLength);
        }
        FileChannel fileChannel=randomAccessFile.getChannel();
        mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,0,fileLength);
        nowId=new AtomicLong(getId());
    }

    public String getNowId()
    {
        return name+nowId;
    }

//    private Lock lock=new ReentrantLock();
    public boolean next(long index)
    {
        if(nowId.compareAndSet(index,index+1))
        {
            setId(nowId.get());
            return true;
        }else{
            return false;
        }
    }

    public long getNextIndex(long index)
    {
        if(nowId.compareAndSet(index,index+1))
        {
            setId(nowId.get());
        }
        return nowId.get();
    }

    public long getIdIndex()
    {
        return nowId.get();
    }

    public String wrapIndex(long index)
    {
        return name+index;
    }

    public void force()
    {
        mappedByteBuffer.force();
    }
}
