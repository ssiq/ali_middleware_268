package com.alibaba.middleware.race.rpc.model;

import com.alibaba.middleware.race.rpc.async.AsynFuture;
import com.alibaba.middleware.race.rpc.async.ResponseCallbackListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wlw on 15-7-30.
 */
public class RequestIdManager {
    private static Map<Long, RequestIdManager> minRequstIdMap=new ConcurrentHashMap<Long, RequestIdManager>();

    public static RequestIdManager getMyRequestId()
    {
        return getThreadRequestId(Thread.currentThread().getId());
    }

    public static RequestIdManager getThreadRequestId(long threadId)
    {
        if(minRequstIdMap.containsKey(threadId)){
            return minRequstIdMap.get(threadId);
        }else{
            RequestIdManager requestIdManager =new RequestIdManager();
            minRequstIdMap.put(threadId, requestIdManager);
            return requestIdManager;
        }
    }

    private int minId=0;
    private int nowId=0;
    private Map<Integer, ResponseCallbackListener> asyncMethod=new ConcurrentHashMap<Integer, ResponseCallbackListener>();
    private Map<Integer, Long> outTimeMap=new ConcurrentHashMap<Integer, Long>();
    private AsynFuture asynFuture;

    public  int getNextRequest()
    {
        return nowId++;
    }

    public void getOneResponse(int reqeustId)
    {
        minId=reqeustId+1;
    }

    public boolean isValid(int requestId)
    {
        return minId<=requestId;
    }

    public <T extends ResponseCallbackListener> void attachAsync(int requestId, T listener)
    {
        asyncMethod.put(requestId, listener);
    }

    public boolean isAsync(int requestId)
    {
        return asyncMethod.containsKey(requestId);
    }

    public ResponseCallbackListener getListener(int requestId)
    {
        return asyncMethod.get(requestId);
    }

    public boolean isOutTime(int requestId)
    {
        return outTimeMap.get(requestId)<System.currentTimeMillis();
    }

    public void setOutTime(int requestId,long outTime)
    {
        outTimeMap.put(requestId, outTime);
    }

    public AsynFuture getAsynFuture() {
        return asynFuture;
    }

    public void setAsynFuture(AsynFuture asynFuture) {
        this.asynFuture = asynFuture;
    }
}
