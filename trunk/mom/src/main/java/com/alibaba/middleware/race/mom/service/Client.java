package com.alibaba.middleware.race.mom.service;

import com.alibaba.middleware.race.mom.error.AsyncInvokeTooQuickException;
import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import com.alibaba.middleware.race.mom.handler.ClientHeartbeatHandler;
import com.alibaba.middleware.race.mom.handler.TransmittingMessageDecoder;
import com.alibaba.middleware.race.mom.handler.TransmittingMessageEncoder;
import com.alibaba.middleware.race.mom.model.CallbackListener;
import com.alibaba.middleware.race.mom.model.OnceSempore;
import com.alibaba.middleware.race.mom.model.ResponseFuture;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.*;
import io.netty.util.concurrent.Future;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-4.
 */
public class Client extends AbstractService {

    private Bootstrap b;
    private EventLoopGroup group;

    private class ActionHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            TransmittingMessage transmittingMessage=(TransmittingMessage)msg;
            processAction(ctx, transmittingMessage);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            System.out.println("ctx:"+ctx+" channel:"+ctx.channel());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            System.out.println("I got one cause:"+cause);
        }
    }


    public Client(int asyncPermit,long timeOut)
    {
        this.timeOut=timeOut;
        this.semaphoreAsync=new Semaphore(asyncPermit,true);
        futureMap=Maps.newConcurrentMap();
        processMap=Maps.newHashMap();
//        ThreadFactory connectFactory = new DefaultThreadFactory("connect");
        group=new NioEventLoopGroup();
//        group=new NioEventLoopGroup(cores,connectFactory, NioUdtProvider.BYTE_PROVIDER);
        b=new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
//        b.group(group).channelFactory(NioUdtProvider.BYTE_CONNECTOR)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, 1048576)
                .option(ChannelOption.SO_RCVBUF, 1048576)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 64 * 1024)
                .handler(new ChannelInitializer<SocketChannel>() {
//                .handler(new ChannelInitializer<UdtChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                    protected void initChannel(UdtChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(8388608, 0, 4, 0, 4));
                        ch.pipeline().addLast(new TransmittingMessageDecoder());
                        ch.pipeline().addLast(new TransmittingMessageEncoder());
//                        ch.pipeline().addLast(new IdleStateHandler(4, 2, 0));
//                        ch.pipeline().addLast(new ClientHeartbeatHandler());
                        ch.pipeline().addLast(new ActionHandler());
                    }
                });
    }

    private ExecutorService executorService= Executors.newCachedThreadPool();
    @Override
    protected ExecutorService getCallbackExecutor() {
        return executorService;
    }

    private AtomicBoolean isDown=new AtomicBoolean(false);

    private AtomicInteger ids=new AtomicInteger(0);
    private ThreadLocal<Integer> idThreadLocal=new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return ids.getAndIncrement()%cores;
        }
    };

    private ConcurrentMap<String,Channel[]> channelMap=Maps.newConcurrentMap();

    private void connect(final String ip, final int port, final int id, final ActiveService activeService)throws InterruptedException
    {
        Channel[] channels=channelMap.get(ip);
        Channel channel=channels[id];
        if(channel==null||!channel.isOpen())
        {
            channel=b.connect(ip, port).sync().channel();
            channels[id]=channel;
            channel.closeFuture().addListener(new GenericFutureListener() {

                @Override
                public void operationComplete(Future future) throws Exception {
                    System.out.println(future.isSuccess());
                    System.out.println("one connect close ip:" + ip + " port:" + port);
                    System.out.println("reconnect");
                    System.out.println("reason :"+future.cause());
                    while (true) {
                        try {
                            if(isDown.get())return;
                            connect(ip, port,id,activeService);
                            break;
                        } catch (InterruptedException e) {
                            continue;
                        } catch (Exception e) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                            }
                        }
                    }
                    System.out.println("reconnect success");
                }
            });
            if(activeService!=null)
            {
                activeService.process(channel);
            }
        }

    }

    private int cores=Runtime.getRuntime().availableProcessors()*2;

    public void connect(final String ip, final int port, final ActiveService activeService) throws InterruptedException {

        Channel[]channels=channelMap.get(ip);
        if(channels!=null)return;
        channels=new Channel[cores];
        channelMap.putIfAbsent(ip,channels);
        for(int i=0;i<cores;++i)
        {
            connect(ip,port,i,activeService);
        }
    }



    private Channel getChannel(String ip,int id)
    {
//        ConcurrentMap<String,Channel> concurrentMap=channelThreadLocal.get();
//        return concurrentMap.get(ip);
        return channelMap.get(ip)[id];
    }

    public TransmittingMessage doSyncInvoke(String ip, final TransmittingMessage transmittingMessage)
            throws InterruptedException, InvokeFailException, RemoteRunOuttimeException {
        Channel channel=this.getChannel(ip,idThreadLocal.get());
        return doSyncInvoke(channel, transmittingMessage);
    }

    public void doAsyncInvoke(String ip, final TransmittingMessage transmittingMessage,CallbackListener callbackListener)
            throws InterruptedException, AsyncInvokeTooQuickException {
        Channel channel=this.getChannel(ip,idThreadLocal.get());
        doAsyncInvoke(channel,transmittingMessage,callbackListener);
    }

    public void doOneWayInvoke(String ip,TransmittingMessage transmittingMessage)
    {
        Channel channel=this.getChannel(ip,idThreadLocal.get());
        doOneWayInvoke(channel,transmittingMessage);
    }

    @Override
    public void shutdown() {
        isDown.set(true);
        super.shutdown();
        group.shutdownGracefully();
    }
}
