package com.alibaba.middleware.race.mom.store.impl;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.broker.ContextExecutorService;
import com.alibaba.middleware.race.mom.broker.GroupChannel;
import com.alibaba.middleware.race.mom.broker.RunnableFactory;
import com.alibaba.middleware.race.mom.message.DefaultTopicManager;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.model.IndexMessage;
import com.alibaba.middleware.race.mom.model.NotSendMessagePair;
import com.alibaba.middleware.race.mom.service.Pair;
import com.alibaba.middleware.race.mom.store.MessageStore;
import com.alibaba.middleware.race.mom.store.queue.MessageBlockQueue;
import com.alibaba.middleware.race.mom.store.queue.SubscriptQueue;
import com.alibaba.middleware.race.mom.util.NameCreater;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wlw on 15-8-16.
 */
public class QueueListMessageStore implements MessageStore {

    private int fileSize;
    private String path;
    private ExecutorService executorService;
    private RunnableFactory runnableFactory;
    private int minFlushPaged;

    public QueueListMessageStore(int fileSize, String path, ExecutorService executorService,RunnableFactory runnableFactory) {
        this.fileSize = fileSize;
//        this.fileSize=4096*1000;
//        this.minFlushPaged=fileSize;
        this.runnableFactory=runnableFactory;
        this.path = path;
        this.executorService=executorService;
    }

    private static String messageDict="message";
    private static String subscriptDict="subscript";
//    private String indexDict="index";

    private boolean checkAndCreateDict(String name)
    {
        File file=new File(path+name);
        if(file.exists()&&file.isDirectory())
        {
            return true;
        }else if(!file.isDirectory()){
            file.delete();
        }
        return file.mkdir();
    }

    //queue map
    private ConcurrentMap<String,MessageBlockQueue> messageQueueMap;

    private String subpath;
    private String messaePath;
    @Override
    public boolean start() {
        File file=new File(path);
        if(!file.exists())
        {
            if(!file.mkdir())
            {
                System.out.println("the path does not exist and create fail");
                return false;
            }
        }
        if(!file.isDirectory())
        {
            System.out.println("the path is not a directory");
            return false;
        }
        if(!file.canRead()||!file.canWrite())
        {
            System.out.println("the path can not be read or written");
            return false;
        }
        checkAndCreateDict(subscriptDict);
        checkAndCreateDict(messageDict);

        String subpath=path+subscriptDict+"/";
        File subscriptDictFile=new File(subpath);
        String[] subList=subscriptDictFile.list();
        for(int i=0;i<subList.length;++i)
        {
            SubscriptQueue subscriptQueue=new SubscriptQueue(subpath+subList[i],fileSize,innerTopicManager);
            try {
                subscriptQueue.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("load subscript fail");
                return false;
            }
        }
        innerTopicManager.initialEnd();
        nowSubsciptQueue=new SubscriptQueue(subpath+NameCreater.newName(),fileSize,innerTopicManager);
        try {
            nowSubsciptQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("subscript create fail");
            return false;
        }

        messaePath=path+messageDict+"/";
        File messaageDictFile=new File(messaePath);
        String[] messageDictList=messaageDictFile.list();
        for(int i=0;i<messageDictList.length;++i)
        {
            MessageBlockQueue messageBlockQueue=new MessageBlockQueue(messaePath+messageDictList[i],
                    fileSize,msgMap);
            try {
                messageBlockQueue.start();
            } catch (IOException e) {
                System.out.println("message laod fail");
                e.printStackTrace();
                return false;
            }
        }
        nowMessageQueue =new MessageBlockQueue(messaePath+NameCreater.newName(),fileSize,msgMap);

        try {
            nowMessageQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("create message fail");
            return false;
        }
        endBlock=preAllocFile(nowMessageQueue,oncePreAllocNum);

        forceLateService.schedule(new Runnable() {
            @Override
            public void run() {
                long t=lastForceTime;
//                System.out.println("do rate:"+forced.get()+" "+putted.get());
                if(!forced.get())
                {
                    if(putted.get())
                    {
                        forced.set(false);
                        doForce(getNowMessageQueue());
                        forceLateService.schedule(this, maxforceGad-(System.currentTimeMillis()-t), TimeUnit.MILLISECONDS);
                    }
                }
                forceLateService.schedule(this, maxforceGad, TimeUnit.MILLISECONDS);
            }
        },maxforceGad,TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public ConcurrentLinkedQueue<NotSendMessagePair> getDonotSendMessage() {
        return null;
    }

    private class InnerTopicManager extends DefaultTopicManager<GroupChannel>
    {

        private boolean isInitialEnd=false;

        @Override
        public void addTopic(String topic, String filter, GroupChannel t) {
            List<GroupChannel> list=this.getTopic(topic,filter);
            if (isInitialEnd&&(list==null||list.isEmpty()||!list.contains(t)))
            {
                System.out.println("register to store");
                registerSubscript(t.getGroupId(), topic, filter, System.currentTimeMillis());
            }
            super.addTopic(topic, filter, t);
        }

        public void initialEnd()
        {
            isInitialEnd=true;
        }
    }

    private InnerTopicManager innerTopicManager=new InnerTopicManager();
    private SubscriptQueue nowSubsciptQueue;

    @Override
    public void registerSubscript(String groupId, String topic, String filter, long subscriptTime) {

    }

    @Override
    public TopicManager<GroupChannel> getSubsciptInfo() {
        return innerTopicManager;
    }


    //pre alloc
    private final int oncePreAllocNum=3;
    private MessageBlockQueue lowerBlock;//当更新到这块时,pre alloc线程就会开始跑
    private MessageBlockQueue endBlock;
    private ExecutorService preAllocService=Executors.newSingleThreadExecutor();
    private int allocNumberLowLevel=1;
    private MessageBlockQueue preAllocFile(MessageBlockQueue messageBlockQueue,int num)
    {
        if(messageBlockQueue==null)return null;
        for(int i=0;i<num;++i)
        {
            messageBlockQueue=messageBlockQueue.getNewQueue(messaePath);
            if(num-i==allocNumberLowLevel)lowerBlock=messageBlockQueue;
        }
        return messageBlockQueue;
    }

    private ConcurrentMap<String,IndexMessage> msgMap=new ConcurrentHashMap<>();
    private MessageBlockQueue nowMessageQueue;
    private static AtomicInteger storeTime=new AtomicInteger(0);

//    private static ScheduledExecutorService showTimes= Executors.newSingleThreadScheduledExecutor();
//    static {
//        showTimes.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("store times:" + storeTime.get());
//            }
//        },0,1,TimeUnit.SECONDS);
//    }

//    private CountDownLatch waitForChangeNowQueue=new CountDownLatch(1);

    private MessageBlockQueue getNowMessageQueue()
    {
        return nowMessageQueue;
    }

    private static int cores=Runtime.getRuntime().availableProcessors()*2;
//    private SimpleQueue<Pair<>>

    private long lastForceTime=System.currentTimeMillis();
    private long maxforceGad=800;
    private AtomicBoolean putted=new AtomicBoolean(false);
    private AtomicBoolean forced=new AtomicBoolean(false);

    private ScheduledExecutorService forceLateService=Executors.newScheduledThreadPool(1);

    private Lock newQueueLock=new ReentrantLock();
    private MessageBlockQueue getNewMessageQueue(final MessageBlockQueue useQueue)
    {
        try{
            newQueueLock.lock();
            if(useQueue==nowMessageQueue)
            {
//                System.out.println("change queue");
                nowMessageQueue=nowMessageQueue.getNewQueue(messaePath);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        forced.set(true);
//                        fileNumber.incrementAndGet();
//                        try{
//                            setlock.lock();
//                            nowPos=0;
//                        }finally {
//                            setlock.unlock();
//                        }
//
//                        try{
//                            flLock.lock();
//                            nowFlashPos=0;
//                        }finally {
//                            flLock.unlock();
//                        }

                        doForce(useQueue);
                    }
                });
                if(nowMessageQueue==lowerBlock)
                {
                    preAllocService.submit(new Runnable() {
                        @Override
                        public void run() {
                            endBlock=preAllocFile(endBlock,oncePreAllocNum);
                        }
                    });
                }
            }
            return nowMessageQueue;
        }finally {
            newQueueLock.unlock();
        }
    }

    private void doForce(MessageBlockQueue useQueue)
    {
//        System.out.println("force");
        putted.set(false);
        Pair<Runnable,Runnable> pair=runnableFactory.snapshot();
        executorService.submit(pair.getO1());
        lastForceTime=System.currentTimeMillis();
        useQueue.force();
        executorService.submit(pair.getO2());
    }

    private int nowFlashPos=0;
    private int nowPos=0;
    private Lock setlock=new ReentrantLock();
    private AtomicInteger fileNumber=new AtomicInteger(0);
    private void setNowPage(int pos,int fn)
    {
        try{
            setlock.lock();
            if(fn!=fileNumber.get())return;
            if(pos>nowPos)
            {
                if(pos>nowPos)
                {
                    nowPos=pos;
                }
            }
        }finally {
            setlock.unlock();
        }
    }

    private Lock flLock=new ReentrantLock();
    private void setFlushPage(int pos,int fn)
    {
        try{
            flLock.lock();
            if(fn!=fileNumber.get())return;
            if(pos>nowFlashPos)
            {
                nowFlashPos=pos;
            }
        }finally {
            flLock.unlock();
        }
    }

    @Override
    public IndexMessage storeMessage(Message message, byte[] messageByte) {
        storeTime.incrementAndGet();
//        System.out.println("store time:"+storeTime.get());
//        int id=nowMessageQueue.getId();
        putted.set(true);
//        int fn=fileNumber.get();
        MessageBlockQueue messageBlockQueue=getNowMessageQueue();
        IndexMessage indexMessage=messageBlockQueue.putMessage(message,messageByte);
        int tTimes=10;
        while (indexMessage==null&&tTimes>0)
        {
//            System.out.println("get null from :"+id);
//            nowMessageQueue=nowMessageQueue.getNext(messaePath);
//            nowMessageQueue=nowMessageQueue.getNewQueue(messaePath);
//            System.out.println("index message null");
            messageBlockQueue=getNewMessageQueue(messageBlockQueue);
            indexMessage=messageBlockQueue.putMessage(message, messageByte);
//            fn=fileNumber.get();
            --tTimes;
        }
//        if(tTimes!=10)
//        {
//            System.out.println("try time:"+(10-tTimes));
//        }
//        setNowPage(indexMessage.getIndex(),fn);
        return indexMessage;
    }


//    @Override
//    public void setSendOver(String messageId) {
////        System.out.println("set "+messageId+" send over");
////        IndexMessage indexMessage=msgMap.get(messageId);
////        indexMessage.setUse();
////        msgMap.remove(messageId);
//    }

    @Override
    public void shutdown() {

    }

    @Override
    public void force() {
//        System.out.println("now pos:"+nowPos+" nowflushPos:"+nowFlashPos);
//        if(nowPos-nowFlashPos>=minFlushPaged)
//        {
//            ContextExecutorService.getExecutorService().submit(new Runnable() {
//                @Override
//                public void run() {
//                    setFlushPage(nowPos, fileNumber.get());
//                    long t = System.currentTimeMillis();
//                    doForce(getNowMessageQueue());
//                    System.out.println("use time:" + (System.currentTimeMillis() - t));
//                }
//            });
//        }
    }
}
