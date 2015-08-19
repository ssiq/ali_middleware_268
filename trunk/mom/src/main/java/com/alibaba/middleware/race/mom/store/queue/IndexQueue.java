package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.model.IndexMessage;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.store.util.StringSerilizer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-9.
 *
 * 包括复用的4字节的flag,16字节的msgId,16字节的文件名,8字节的时间,4字节的位置
 *
 */
public class IndexQueue extends FixedItemSizeQueue{

    private boolean isNew=false;
    private FileChannel fileChannel;
    private final static int msgIdOffSet=0;
    private final static int msgIdLength=16;
    private final static int fileNameOffSet=msgIdOffSet+msgIdLength;
    private final static int fileNameLength=16;
    private final static int timeOffSet=fileNameOffSet+fileNameLength;
    private final static int timeLength=8;
    private final static int locationOffSet=timeOffSet+timeLength;
    private final static int locationLength=4;
    private ConcurrentMap<String,IndexMessage> msgMap;
    private ConcurrentLinkedQueue<IndexMessage> unsendMessageQueue;
    private StringSerilizer stringSerilizer=new StringSerilizer();

    private class InnerIndexMessage implements IndexMessage
    {
        private int index;
        private final int regMask=2;

        public InnerIndexMessage(int index) {
            this.index = index;
        }

        public void setUse()
        {
            remove(index);
            MessageBlockQueue messageBlockQueue=messageBlockQueueManager.getQueue(getPutQueueName());
            if(messageBlockQueue!=null)
            {
                ByteBuffer byteBuffer=slice();
                int location=byteBuffer.getInt(index + flagLength + locationOffSet);
                messageBlockQueue.remove(location);
            }else{
                System.out.println("no one to change");
            }
            pop();
        }

        public boolean isUsed()
        {
            int flag=getFlag(index);
            return isUse(flag);
        }

        @Override
        public byte[] getMessage() {
            ByteBuffer byteBuffer=slice();
            String queueName=getPutQueueName();
            MessageBlockQueue messageBlockQueue=messageBlockQueueManager.getQueue(queueName);
            int location=byteBuffer.getInt(index+flagLength+locationOffSet);
            return messageBlockQueue.get(location);
        }

//        @Override
        public long getBornTime() {
            ByteBuffer byteBuffer=slice();
            byteBuffer.position(index+flagLength+timeOffSet);
            return byteBuffer.getLong();
        }

//        @Override
//        public boolean isRegistered() {
//            int flag=getFlag(index);
//            return isMask(flag,regMask);
//        }
//
//        @Override
//        public void setRegistered() {
//            setFlag(regMask,index);
//            changeContent();
//        }

//        @Override
        public String getMsgId() {
            ByteBuffer byteBuffer=slice();
            byteBuffer.position(index+flagLength+msgIdOffSet);
            byte[] bytes=new byte[msgIdLength];
            byteBuffer.get(bytes);
            return stringSerilizer.decode(bytes);
        }

        private AtomicInteger leftToSend=new AtomicInteger(0);
        private AtomicBoolean end=new AtomicBoolean(false);
        @Override
        public void up() {
            leftToSend.incrementAndGet();
        }

        @Override
        public void down() {
            if(leftToSend.decrementAndGet()==0&&end.get())
            {
//                return true;
            }else {
//                return false;
            }
        }

        @Override
        public void downOnce(AtomicBoolean once) {
//            if(once.get()||!once.compareAndSet(false,true))return false;
//            return down();
        }

        @Override
        public void endUp() {
            end.set(true);
//            return leftToSend.get()==0;
        }

        @Override
        public int getIndex() {
            return index;
        }

        //        @Override
        public String getPutQueueName() {
            ByteBuffer byteBuffer=slice();
            byteBuffer.position(index + flagLength + fileNameOffSet);
            byte[]bytes=new byte[fileNameLength];
            byteBuffer.get(bytes);
            return stringSerilizer.decode(bytes);
        }
    }


    public IndexQueue(String fileName,int fileLength,
                      ConcurrentMap<String,IndexMessage> msgMap,
                      ConcurrentLinkedQueue<IndexMessage> unsendMessageQueue){
        super(locationLength+locationOffSet);
        this.fileName=fileName;
        this.msgMap=msgMap;
        this.unsendMessageQueue=unsendMessageQueue;
        this.fileLength=fileLength;
        forceLevel=2;
    }

    public void start() throws IOException
    {
        File file=new File(fileName);
        if(!file.exists())
        {
            isNew=true;
        }
        randomAccessFile=new RandomAccessFile(fileName,"rw");
        if(isNew)
        {
            randomAccessFile.setLength(fileLength);
        }
        fileChannel=randomAccessFile.getChannel();
        mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());
        this.fileLength=(int)fileChannel.size();
        if(!isNew)
        {
            int itemSize=getItemSize();
            for(int i=initialBegin;i<end.get();i+=itemSize)
            {
                IndexMessage indexMessage=new InnerIndexMessage(i);
                if(!indexMessage.isUsed())
                {
//                    if(!indexMessage.isRegistered())
//                    {
//                        unsendMessageQueue.add(indexMessage);
//                    }else{
//                        msgMap.putIfAbsent(indexMessage.getMsgId(),indexMessage);
//                    }
                }
            }
        }
        haveStarted();
    }

    private Serializer serializer=Serializer.getMessageSerialier();

//    private static int[] upperSize=new int[]{1<<5,1<<8,1<<11,1<<14,1<<17,1<<20,1<<23};
//    private static int queue_number=upperSize.length;
//    private static QueueManager<SendMessageQueue>[] queueManagers=new QueueManager[queue_number];
//    public static QueueManager<SendMessageQueue> getQueueManager(int length)
//    {
//        for(int i=0;i<upperSize.length;++i)
//        {
//            if(length<upperSize[i])
//            {
//                return queueManagers[i];
//            }
//        }
//        return queueManagers[queue_number-1];
//    }
//    public static void initMessageQueues(String path,IdFactory idFactory,
//                                         ConcurrentSet<MessageQueue>[]messageQueues)
//    {
//        for(int i=0;i<queue_number;++i)
//        {
//            int fileLength=upperSize[i]*100<(1<<25)?(upperSize[i]*100):(1<<25);
//            queueManagers[i]=new QueueManager<>(SendMessageQueue.getFactory(upperSize[i],fileLength),
//                    idFactory,path,messageQueues);
//        }
//    }


    private static QueueManager<MessageBlockQueue> messageBlockQueueManager;

    public static void setMessageBlockQueueManager(QueueManager<MessageBlockQueue> outmessageBlockQueueManager) {
        messageBlockQueueManager = outmessageBlockQueueManager;
    }

    public IndexMessage putMessage(Message message)
    {
//        System.out.println("begin put message");
        ByteBuffer mByteBuffer=allocSpace(getItemSize());
        if(mByteBuffer==null)return null;
//        System.out.println("end alloc,position is:"+mByteBuffer.position());
        byte[]messageBytes=serializer.encode(message);
//        System.out.println("encode the message size is:"+messageBytes.length);
        MessageBlockQueue messageBlockQueue=messageBlockQueueManager.getNowQueue();
//        System.out.println("get the message queue:"+messageBlockQueue.getName());
        int res=messageBlockQueue.add(messageBytes);
//        messageBlockQueue.putMessage(message);
        int recTime=10;
        while (res==-1&&recTime>0)
        {
            messageBlockQueue=messageBlockQueueManager.getNewQueue();
            res=messageBlockQueue.add(messageBytes);
            --recTime;
        }
//        System.out.println("end store message res is:"+res);
        if(res==-1)
        {
            System.out.println("store fail");
            return null;
        }

        long time=System.currentTimeMillis();
        messageBlockQueue.force();
        System.out.println("force time:"+(System.currentTimeMillis()-time));

        int pos=mByteBuffer.position();
        byte[] bytes=new byte[getItemSize()];
        stringSerilizer.encode(message.getMsgId(),bytes,msgIdOffSet,msgIdLength);
        String queueName=messageBlockQueue.getName();
        stringSerilizer.encode(queueName, bytes, fileNameOffSet, fileNameLength);
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        byteBuffer.position(timeOffSet);
        byteBuffer.putLong(message.getBornTime());
        byteBuffer.putInt(res);
        this.add(mByteBuffer, bytes);
//        System.out.println("put message success");
        IndexMessage indexMessage=new InnerIndexMessage(pos);
        msgMap.put(message.getMsgId(),indexMessage);
        return indexMessage;
    }

    public static boolean forceMessageBlockQueue(String messageQueueName)
    {
        System.out.println("queue name:"+messageQueueName);
        MessageBlockQueue messageBlockQueue=messageBlockQueueManager.getQueue(messageQueueName);
        if(messageBlockQueue!=null)
        {
            messageBlockQueue.force();
            return true;
        }else{
            System.out.println("no one to force");
        }
        return false;
    }

    private static class IndexQueueFactory implements QueueFactory
    {

        private int fileLength;
        private ConcurrentMap<String,IndexMessage> msgMap;
        private ConcurrentLinkedQueue<IndexMessage> unsendMessageQueue;

        public IndexQueueFactory(int fileLength,
                                 ConcurrentLinkedQueue<IndexMessage> unsendMessageQueue,
                                 ConcurrentMap<String, IndexMessage> msgMap) {
            this.fileLength = fileLength;
            this.unsendMessageQueue = unsendMessageQueue;
            this.msgMap = msgMap;
        }

        @Override
        public Object getNewQueue(String name) {
            return new IndexQueue(name,fileLength,msgMap,unsendMessageQueue);
        }
    }

    public static QueueFactory getFactory(int fileLength,
                                          ConcurrentLinkedQueue<IndexMessage> unsendMessageQueue,
                                          ConcurrentMap<String, IndexMessage> msgMap)
    {
        return new IndexQueueFactory(fileLength,unsendMessageQueue,msgMap);
    }
}
