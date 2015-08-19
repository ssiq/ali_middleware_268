package com.alibaba.middleware.race.rpc.api.client;

import com.alibaba.middleware.race.rpc.api.handler.*;
import com.alibaba.middleware.race.rpc.api.handler.serializer.ClientSerializer;
import com.alibaba.middleware.race.rpc.api.handler.serializer.ProtoBufSerialier;
import com.alibaba.middleware.race.rpc.async.AsynFuture;
import com.alibaba.middleware.race.rpc.async.ResponseCallbackListener;
import com.alibaba.middleware.race.rpc.async.ResponseFuture;
import com.alibaba.middleware.race.rpc.model.RequestIdManager;
import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by wlw on 15-7-27.
 */
public class RpcClient {

    private Channel channel;
    private Map<Long,Object> map;
    private Map<Long,CountDownLatch> waitMap;
    private ThreadLocal<CountDownLatch> lockThreadLocal=new ThreadLocal<CountDownLatch>(){
        protected CountDownLatch initialValue()
        {
            return new CountDownLatch(1);
        }
    };
    private Map<String,ResponseCallbackListener> asynMethodName;

    public void connect(String ip,int port) throws InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup();
        final ClientSerializer clientSerializer=new ProtoBufSerialier();

        Bootstrap b=new Bootstrap();
        map=new ConcurrentHashMap<Long,Object>();
        waitMap=new ConcurrentHashMap<Long, CountDownLatch>();

        final CountDownLatch latch = new CountDownLatch(1);
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast("frameDecoder",
                                        new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                                ch.pipeline().addLast("decodeHandler",
                                        new RpcResponseDecoder(clientSerializer));
                                ch.pipeline().addLast("frameEncoder",
                                        new LengthFieldPrepender(2));
                                ch.pipeline().addLast("encodeHandler",
                                        new RpcRequestEncoder(clientSerializer));
                                ch.pipeline().addLast("actionHandler",
                                        new RpcClientHandler(map, latch, waitMap));
                            }
                        }
                );
        ChannelFuture channelFuture=b.connect(ip,port).sync();
        if(!channelFuture.isSuccess())
        {
            System.out.println("connect failed");
        }else{
            System.out.println("connect success");
        }
        latch.await(10, TimeUnit.SECONDS);
        channel=channelFuture.channel();
//        System.out.println("have connected");
//        System.out.println("my handler:"+channel.pipeline().get("actionHandler"));

    }

    public Object doSyncMethod(RpcRequest rpcRequest,int timeout) throws Throwable {
//        System.out.println("do Sync action");
//        ChannelHandlerContext ctx=channel.pipeline().lastContext();
//        System.out.println("ctx:"+ctx);
//        ctx.writeAndFlush(rpcRequest, channelPromise);
//        Thread.sleep(1000);

//        System.out.println("new print");
//        System.out.println("channel is active:" + channel.isActive());
//        System.out.println("channel is open:"+channel.isOpen());
//        System.out.println("my handler:" + channel.pipeline().get("actionHandler"));
        CountDownLatch countDownLatch=new CountDownLatch(1);
        waitMap.put(rpcRequest.getThreadId(), countDownLatch);
//        System.out.println("lock set:"+rpcRequest.getThreadId()+" "+lock);
        channel.writeAndFlush(rpcRequest);
//        long begin_time=System.currentTimeMillis();
        RequestIdManager requestIdManager = RequestIdManager.getMyRequestId();
        if(countDownLatch.await(timeout, TimeUnit.MILLISECONDS))
        {
//            System.out.println("use time:"+(System.currentTimeMillis()-begin_time));
//            System.out.println("have waited the result");
//            System.out.println("The resutl is:" + map.get(rpcRequest.getThreadId()));
            RpcResponse rpcResponse=(RpcResponse)map.get(rpcRequest.getThreadId());
            requestIdManager.getOneResponse(rpcResponse.getRequestId());
            if(rpcResponse.getThrowable()!=null)
            {
                throw rpcResponse.getThrowable();
            }else{
                return rpcResponse.getAppResponse();
            }
        }else{
            requestIdManager.getOneResponse(rpcRequest.getRequestId());
//            System.out.println("use time:"+(System.currentTimeMillis()-begin_time));
            throw new TimeoutException();
        }
    }

    public <T extends ResponseCallbackListener> void doAsyncMethod(RpcRequest rpcRequest,int timeout,T callbackListener)
    {
//        System.out.println("do Async Method");
        RequestIdManager requestIdManager = RequestIdManager.getMyRequestId();
        requestIdManager.attachAsync(rpcRequest.getRequestId(), callbackListener);
        requestIdManager.setOutTime(rpcRequest.getRequestId(), timeout + System.currentTimeMillis());
        AsynFuture asynFuture=new AsynFuture();
        new ResponseFuture().setFuture(asynFuture);
        requestIdManager.setAsynFuture(asynFuture);
        channel.writeAndFlush(rpcRequest);
    }
}
