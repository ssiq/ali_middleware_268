package com.alibaba.middleware.race.rpc.model;

import com.alibaba.middleware.race.rpc.api.util.MethodCache;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by wlw on 15-7-25.
 */
public class RpcRequest {
    private Object[] args;
    private long threadId;
    private short methodId;
    private int requestId;
    private boolean isContext;

    public RpcRequest(){
        threadId=Thread.currentThread().getId();
    }

    public RpcRequest(Method method,Object[] args)
    {
        this.methodId= MethodCache.getInstance().getMethodId(method);
        this.args=args;
        threadId=Thread.currentThread().getId();
        isContext=false;
//        this.requestId= RequestIdManager.getMyRequestId().getNextRequest();
    }

    public RpcRequest(Method method,Object[]args,boolean isContext)
    {
        if(isContext)
        {
            this.methodId= MethodCache.getContextMethodCache().getMethodId(method);
            this.args=args;
            threadId=Thread.currentThread().getId();
            this.isContext=true;
//            this.requestId= RequestIdManager.getMyRequestId().getNextRequest();
        }else{
            this.methodId= MethodCache.getInstance().getMethodId(method);
            this.args=args;
            threadId=Thread.currentThread().getId();
            this.isContext=false;
//            this.requestId= RequestIdManager.getMyRequestId().getNextRequest();
        }
    }

    public short getMethodId() {
        return methodId;
    }

    public Object[] getArgs() {
        return args;
    }

    public long getThreadId() {
        return threadId;
    }

    public int getRequestId() {
        return requestId;
    }

    public boolean isContext() {
        return isContext;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "args=" + Arrays.toString(args) +
                ", threadId=" + threadId +
                ", methodId=" + methodId +
                ", requestId=" + requestId +
                ", isContext=" + isContext +
                '}';
    }
}
