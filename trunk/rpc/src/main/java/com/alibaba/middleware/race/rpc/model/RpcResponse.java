package com.alibaba.middleware.race.rpc.model;


import java.io.Serializable;

/**
 * Created by huangsheng.hs on 2015/3/27.
 */
public class RpcResponse{
//    static private final long serialVersionUID = -4364536436151723421L;

    private Throwable throwable;
    private Object appResponse;
    private long threadId;
    private int requestId;

    public RpcResponse(){}

    public RpcResponse(Object appResponse,Throwable throwable,long threadId,int requestId)
    {
        this.throwable=throwable;
        this.appResponse=appResponse;
        this.threadId=threadId;
        this.requestId=requestId;
    }

    public long getThreadId() {
        return threadId;
    }

    public Object getAppResponse() {
        return appResponse;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getErrorMsg()
    {
        if(throwable!=null)
        {
            return throwable.getMessage();
        }
        else{
            return null;
        }
    }

    public int getRequestId() {
        return requestId;
    }

    public boolean isError(){
        return throwable == null ? false:true;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "throwable=" + throwable +
                ", appResponse=" + (appResponse==null?null:appResponse.toString()) +
                ", threadId=" + threadId +
                ", requestId=" + requestId +
                '}';
    }
}