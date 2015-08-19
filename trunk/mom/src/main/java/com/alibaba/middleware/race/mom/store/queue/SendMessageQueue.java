package com.alibaba.middleware.race.mom.store.queue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by wlw on 15-8-13.
 */
public class SendMessageQueue extends FixedSizeLengthFieldQueue {

    private FileChannel fileChannel;

    public SendMessageQueue(int itemSize,String filename,int fileLength) {
        super(itemSize);
        this.fileName=filename;
        this.fileLength=fileLength;
        this.forceLevel=3;
    }

    @Override
    public void start() throws IOException {
        boolean isNew=false;
        File file=new File(fileName);
        if(!file.exists())
        {
            isNew=true;
        }
        this.randomAccessFile=new RandomAccessFile(fileName,"rw");
        if(isNew)
        {
           randomAccessFile.setLength(fileLength);
        }
        this.fileChannel=randomAccessFile.getChannel();
        this.fileLength=(int)fileChannel.size();
        this.mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,0,this.fileLength);
        if(!isNew)
        {
            initHeader();
        }
        haveStarted();
    }

    private static class SendMessageFactory implements QueueFactory
    {

        private int itemSize;
        private int fileLength;

        public SendMessageFactory(int fileLength, int itemSize) {
            this.fileLength = fileLength;
            this.itemSize = itemSize;
        }

        @Override
        public Object getNewQueue(String name) {
            return new SendMessageQueue(itemSize,name,fileLength);
        }
    }

    public static QueueFactory getFactory(int itemSize,int fileLength)
    {
        return new SendMessageFactory(fileLength,itemSize);
    }

}
