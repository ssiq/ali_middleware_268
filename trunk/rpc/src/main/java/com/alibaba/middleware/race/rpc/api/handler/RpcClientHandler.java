package com.alibaba.middleware.race.rpc.api.handler;

import com.alibaba.middleware.race.rpc.async.AsynFuture;
import com.alibaba.middleware.race.rpc.async.ResponseCallbackListener;
import com.alibaba.middleware.race.rpc.model.RequestIdManager;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wlw on 15-7-27.
 */
@ChannelHandler.Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private Map<Long,Object> map;
    private CountDownLatch countDownLatch;
    private Map<Long,CountDownLatch> waitMap;

    public RpcClientHandler(Map<Long, Object> map, CountDownLatch countDownLatch,Map<Long,CountDownLatch> waitMap) {
        this.map = map;
        this.countDownLatch = countDownLatch;
        this.waitMap=waitMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        System.out.println("active handler");
//        System.out.println("ctx==null:"+(ctx==null));
        countDownLatch.countDown();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("accept the result:"+msg.toString());
//        RpcResponse rpcResponse=(RpcResponse)msg;
        RpcResponse rpcResponse=(RpcResponse)msg;
        RequestIdManager requestIdManager= RequestIdManager.getThreadRequestId(rpcResponse.getThreadId());
        int requestId=rpcResponse.getRequestId();
        if(requestIdManager.isAsync(requestId))
        {
            AsynFuture asynFuture=requestIdManager.getAsynFuture();
            asynFuture.setResult(rpcResponse);
            ResponseCallbackListener responseCallbackListener=requestIdManager.getListener(requestId);
            if(rpcResponse.getThrowable()==null)
            {
                if(requestIdManager.isOutTime(requestId))
                {
                    responseCallbackListener.onTimeout();
                }else{
                    responseCallbackListener.onResponse(rpcResponse.getAppResponse());
                }
            }else{
//                System.out.println("I have one exception:"+rpcResponse.getThrowable());
                responseCallbackListener.onException((Exception) rpcResponse.getThrowable());
            }
        }else{
            if(requestIdManager.isValid(requestId))
            {
                map.put(rpcResponse.getThreadId(), rpcResponse);
                waitMap.get(rpcResponse.getThreadId()).countDown();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
