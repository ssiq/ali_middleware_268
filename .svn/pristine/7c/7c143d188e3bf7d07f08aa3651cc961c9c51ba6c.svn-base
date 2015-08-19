package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.store.util.IdFactory;
import io.netty.util.internal.ConcurrentSet;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-11.
 */
public class QueueManager<T extends MessageQueue> {

    private QueueFactory queueFactory;
    private IdFactory idFactory;
    private AtomicInteger atomicInteger;
    private ConcurrentMap<String,T> queueMap=new ConcurrentHashMap<>();
    private String path;
    private ConcurrentSet<MessageQueue>[] messageQueues;
    private ThreadLocal<T> queueThreadLocal;
    private ThreadLocal<Long> indexThreadLocal;

    public QueueManager(QueueFactory queueFactory, String prefix,
                        String path,ConcurrentSet<MessageQueue>[] messageQueues) throws IOException {
        this(queueFactory,new IdFactory(path,prefix),path,messageQueues);
    }

    public QueueManager(QueueFactory queueFactory, IdFactory idFactory,
                        String path,ConcurrentSet<MessageQueue>[] messageQueues)
    {
        this.queueFactory = queueFactory;
        if(path.endsWith("/"))
        {
            this.path=path;
        }else{
            this.path=path+"/";
        }
        this.idFactory=idFactory;
        this.messageQueues=messageQueues;
        final long index=idFactory.getIdIndex();
        this.indexThreadLocal=new ThreadLocal<Long>(){
            @Override
            protected Long initialValue() {
                return index;
            }
        };
        this.queueThreadLocal=new ThreadLocal<T>();
    }

    public T getNowQueue()
    {
        T res=queueThreadLocal.get();
        if(res==null)
        {
            res=getNewQueue();
        }
        return res;
    }

    private T getNewQueueWithName(long index) throws IOException
    {
//        System.out.println("create new queue");
        String name=idFactory.wrapIndex(index);
        indexThreadLocal.set(idFactory.getNextIndex(indexThreadLocal.get()));
        T res=queueMap.get(name);
        if(res!=null)
        {
            res.waitStarted();
//            System.out.println("get the exist new queue:"+res.getName());
            return res;
        }
        T queue=(T)queueFactory.getNewQueue(idFactory.getNowId());
        T newQueue=queueMap.putIfAbsent(name, queue);
        if(newQueue==null)
        {
            newQueue=queue;
            newQueue.start();
            messageQueues[newQueue.getForceLevel()].add(newQueue);
//            System.out.println("get the new queue:"+newQueue.getName());
        }else{
            newQueue.waitStarted();
//            System.out.println("get the exist new queue:"+newQueue.getName());
        }
        res=newQueue;
        queueThreadLocal.set(res);
        return res;
    }

    public T getNewQueue() {
        T res;
        while(true)
        {
            long index=indexThreadLocal.get();
            try {
                res=getNewQueueWithName(index);
                break;
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println("try get one new queue get exception");
            }
//            System.out.println("try get one new queue");
        }
        return res;
    }

    public T getQueue(String queueName)
    {
        return queueMap.get(queueName);
    }

    //这个方法只会在message store start的时候调用
    public T getNewQueue(String queueName) throws IOException {
        T t=queueMap.get(queueName);
        if(t==null)
        {
            t=(T)queueFactory.getNewQueue(path+queueName);
            t.start();
            queueMap.putIfAbsent(queueName,t);
            messageQueues[t.getForceLevel()].add(t);
        }
        return t;
    }
}
