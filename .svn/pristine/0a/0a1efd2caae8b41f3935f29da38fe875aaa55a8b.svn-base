package com.alibaba.middleware.race.mom.service;

import com.alibaba.middleware.race.mom.error.AsyncInvokeTooQuickException;
import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import com.alibaba.middleware.race.mom.model.CallbackListener;
import com.alibaba.middleware.race.mom.model.OnceSempore;
import com.alibaba.middleware.race.mom.model.ResponseFuture;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by wlw on 15-8-4.
 */
public abstract class AbstractService {

    protected Semaphore semaphoreAsync;
    protected Map<Integer, Pair<RequestProcesser, ExecutorService>> processMap;
    protected Map<Integer, ResponseFuture> futureMap;
    protected long timeOut;

    protected abstract ExecutorService getCallbackExecutor();

    public void processAction(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage)
    {
        if(transmittingMessage.isRequeset())
        {
            processRequest(ctx,transmittingMessage);
        }else if(transmittingMessage.isResponse()){
            processResponse(ctx,transmittingMessage);
        }else{
            return;
        }
    }

    private void processRequest(final ChannelHandlerContext ctx, final TransmittingMessage transmittingMessage)
    {
        final Pair<RequestProcesser, ExecutorService> pair=processMap.get(transmittingMessage.getAction());
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                pair.getO1().process(ctx, transmittingMessage);
            }
        };
        pair.getO2().submit(runnable);
    }

    public void processResponse(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage){
        try{
            final ResponseFuture responseFuture=futureMap.get(transmittingMessage.getOpaque());
            if(responseFuture==null)return;
            ExecutorService callbackExecutor=this.getCallbackExecutor();
            if(transmittingMessage==null)System.out.println("I receive one empty response");
            if(System.currentTimeMillis()-responseFuture.getBeginTimestamp()>timeOut)
            {
                responseFuture.setState(false);
            }
            responseFuture.onResponse(transmittingMessage);
            if(responseFuture.getCallbackListener()!=null)
            {
                responseFuture.release();
                callbackExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        responseFuture.invokeListener();
                    }
                });
            }
        }finally {
            futureMap.remove(transmittingMessage.getOpaque());
        }
    }

    //这个需要在start之前调用
    public void addResponseService(int action,RequestProcesser requestProcesser, ExecutorService executorService)
    {
        processMap.put(action,new Pair<RequestProcesser, ExecutorService>(requestProcesser,executorService));
    }

    protected void removeFuture(int op)
    {
        futureMap.remove(op);
    }

    public TransmittingMessage doSyncInvoke(Channel channel, final TransmittingMessage transmittingMessage)
            throws InterruptedException, InvokeFailException, RemoteRunOuttimeException
    {
        final ResponseFuture responseFuture=new ResponseFuture();
        futureMap.put(transmittingMessage.getOpaque(), responseFuture);
        channel.writeAndFlush(transmittingMessage).addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess())
                {
                    responseFuture.setState(true);
                }else{
                    responseFuture.setState(false);
                    responseFuture.onResponse(null);
                    responseFuture.setThrowable(future.cause());
                    removeFuture(transmittingMessage.getOpaque());
                }
            }
        });

        TransmittingMessage response=responseFuture.getResponse(timeOut, TimeUnit.MILLISECONDS);
        if(response!=null)
        {
            return response;
        }else{
            if(!responseFuture.isOK())
            {
                throw new InvokeFailException();
            }else{
                throw new RemoteRunOuttimeException();
            }
        }
    }

    public void doAsyncInvoke(Channel channel, final TransmittingMessage transmittingMessage,CallbackListener callbackListener)
            throws InterruptedException, AsyncInvokeTooQuickException
    {
        boolean acquire=semaphoreAsync.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
        if(acquire)
        {
            final OnceSempore onceSempore=new OnceSempore(semaphoreAsync);
            final ResponseFuture responseFuture=new ResponseFuture();
            futureMap.put(transmittingMessage.getOpaque(),responseFuture);
            responseFuture.setCallbackListener(callbackListener);
            responseFuture.setOnceSempore(onceSempore);
            channel.writeAndFlush(transmittingMessage).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if(future.isSuccess())
                    {
                        responseFuture.setState(true);
                    }else{
                        try{
                            responseFuture.setState(false);
                            responseFuture.onResponse(null);
                            responseFuture.setThrowable(future.cause());
                            removeFuture(transmittingMessage.getOpaque());
                        }finally {
                            onceSempore.release();
                        }
                    }
                }
            });
        }else{
            throw new AsyncInvokeTooQuickException();
        }
    }

    public void doOneWayInvoke(Channel channel,TransmittingMessage transmittingMessage){
        transmittingMessage.setOneWay();
        channel.writeAndFlush(transmittingMessage);
    }

    protected void shutdown()
    {
        Iterator<Pair<RequestProcesser, ExecutorService>> iterator=processMap.values().iterator();
        while (iterator.hasNext())
        {
            ExecutorService executorService=iterator.next().getO2();
            if(!executorService.isShutdown())
            {
                executorService.shutdown();
            }
        }
    }
}
