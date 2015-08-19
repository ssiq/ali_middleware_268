package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.model.NotSendMessagePair;
import com.alibaba.middleware.race.mom.store.util.StringSerilizer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by wlw on 15-8-10.
 *
 * 记录的信息包括4字节的flag,16字节的groupId,16字节的MsgId
 *
 */
public class NotSendQueue extends FixedItemSizeQueue {

    private boolean newFile=false;
    private FileChannel fileChannel;
//    private static int flagOffSet=0;
//    private static int flagLenght=0;
    private static int groupIdOffset=0;
    private static int groupIdLength=16;
    private static int msgIdOffSet=groupIdLength+groupIdOffset;
    private static int msgIdLength=16;
    private StringSerilizer stringSerilizer=new StringSerilizer();
    private ConcurrentLinkedQueue<NotSendMessagePair> notSendConcurrentQueue;

    private class InnerNotSendMessage implements NotSendMessagePair
    {
        private String groupId;
        private String msgId;
        private int index;

        public InnerNotSendMessage(String groupId, String msgId, int index) {
            this.groupId = groupId;
            this.msgId = msgId;
            this.index = index;
        }

        @Override
        public String getGroupId() {
            return groupId;
        }

        @Override
        public String getMsgId() {
            return msgId;
        }

        @Override
        public void setSend() {
            remove(index);
            pop();
        }

        @Override
        public boolean isSend() {
            int flag=getFlag(index);
            return !isUse(flag);
        }

        @Override
        public String toString() {
            return "InnerNotSendMessage{" +
                    "groupId='" + groupId + '\'' +
                    ", msgId='" + msgId + '\'' +
                    ", index=" + index +
                    '}';
        }

        @Override
        public  boolean equals(Object obj) {
            NotSendMessagePair another=(NotSendMessagePair)obj;
            return this.groupId.equals(another.getGroupId())&&this.msgId.equals(another.getMsgId());
        }
    }

    public NotSendQueue(String fileName,int fileLength,
                        ConcurrentLinkedQueue<NotSendMessagePair> notSendConcurrentQueue)
    {
        super(msgIdOffSet+msgIdLength+flagLength+endLength);
        forceLevel=1;
        this.fileName=fileName;
        this.notSendConcurrentQueue=notSendConcurrentQueue;
        this.fileLength=fileLength;
    }

    public void start() throws IOException
    {
        File file=new File(fileName);
        if(!file.exists())
        {
            newFile=true;
        }
        randomAccessFile=new RandomAccessFile(fileName,"rw");
        if(newFile)
        {
            randomAccessFile.setLength(fileLength);
        }
        fileChannel=randomAccessFile.getChannel();
        mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,0,fileChannel.size());
        this.fileLength=(int)fileChannel.size();
        if(!newFile)
        {
            initHeader();
            int itemSize=getItemSize();
            for(int i=initialBegin;i<end.get();i+=itemSize)
            {
                byte[] bytes=get(i);
                String groupId=stringSerilizer.decode(bytes, groupIdOffset, groupIdLength);
                String msgId=stringSerilizer.decode(bytes,msgIdOffSet,msgIdLength);
                NotSendMessagePair notSendMessagePair=new InnerNotSendMessage(groupId,msgId,i);
                if(!notSendMessagePair.isSend())
                {
                    notSendConcurrentQueue.add(new InnerNotSendMessage(groupId,msgId,i));
                }
            }
        }
        haveStarted();
    }


    public NotSendMessagePair addNotSend(String groupId,String msgId)
    {
        byte[] bytes=getBuffer();
        stringSerilizer.encode(groupId,bytes,groupIdOffset,groupIdLength);
        stringSerilizer.encode(msgId,bytes,msgIdOffSet,msgIdLength);
        int res=add(bytes);
        if(res==-1)
        {
            return null;
        }else{
            NotSendMessagePair notSendMessagePair=new InnerNotSendMessage(groupId,msgId,res);
            notSendConcurrentQueue.add(notSendMessagePair);
            return notSendMessagePair;
        }
    }

    private static class NotSendQueueFactory implements QueueFactory
    {

        private int fileLength;
        private ConcurrentLinkedQueue<NotSendMessagePair> notSendConcurrentQueue;

        public NotSendQueueFactory(int fileLength,
                                   ConcurrentLinkedQueue<NotSendMessagePair> notSendConcurrentQueue) {
            this.fileLength = fileLength;
            this.notSendConcurrentQueue = notSendConcurrentQueue;
        }

        @Override
        public Object getNewQueue(String name) {
            return new NotSendQueue(name,fileLength,notSendConcurrentQueue);
        }
    }

    public static QueueFactory getFactory(int fileLength,
                                          ConcurrentLinkedQueue<NotSendMessagePair> notSendConcurrentQueue)
    {
        return new NotSendQueueFactory(fileLength,notSendConcurrentQueue);
    }
}
