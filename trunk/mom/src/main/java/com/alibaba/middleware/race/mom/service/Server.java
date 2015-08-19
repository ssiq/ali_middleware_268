package com.alibaba.middleware.race.mom.service;

import com.alibaba.middleware.race.mom.error.AsyncInvokeTooQuickException;
import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import com.alibaba.middleware.race.mom.handler.ServerHeartbeatHandler;
import com.alibaba.middleware.race.mom.handler.TransmittingMessageDecoder;
import com.alibaba.middleware.race.mom.handler.TransmittingMessageEncoder;
import com.alibaba.middleware.race.mom.model.CallbackListener;
import com.alibaba.middleware.race.mom.model.InvokeInterface;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.google.common.collect.Maps;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

/**
 * Created by wlw on 15-8-5.
 */
public class Server extends AbstractService {

    private ServerBootstrap b;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private class ActionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            TransmittingMessage transmittingMessage=(TransmittingMessage)msg;
            processAction(ctx, transmittingMessage);
        }
    }

    private int cores=Runtime.getRuntime().availableProcessors()*2;
    public Server(int asyncPermit,long timeOut) {
        this.timeOut=timeOut;
        this.semaphoreAsync=new Semaphore(asyncPermit,true);
        futureMap= Maps.newConcurrentMap();
        processMap=Maps.newHashMap();
        bossGroup=new NioEventLoopGroup();
        workerGroup=new NioEventLoopGroup();
//        ThreadFactory acceptFactory = new DefaultThreadFactory("accept");
//        ThreadFactory connectFactory = new DefaultThreadFactory("connect");
//        NioEventLoopGroup bossGroup = new NioEventLoopGroup(cores, acceptFactory, NioUdtProvider.BYTE_PROVIDER);
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup(cores, connectFactory, NioUdtProvider.BYTE_PROVIDER);
        b=new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .channelFactory(NioUdtProvider.BYTE_ACCEPTOR)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_SNDBUF, 1048576)
                .option(ChannelOption.SO_RCVBUF, 1048576)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 64 * 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
//                .childHandler(new ChannelInitializer<UdtChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                    protected void initChannel(UdtChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(4, 0, 0));
                        ch.pipeline().addLast(new ServerHeartbeatHandler());
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(8388608, 0, 4, 0, 4));
                        ch.pipeline().addLast(new TransmittingMessageDecoder());
                        ch.pipeline().addLast(new TransmittingMessageEncoder());
                        ch.pipeline().addLast(new ActionHandler());
                    }
                });
    }

    public void bind(int port)
    {
        ChannelFuture channelFuture=b.bind(port);
        while (true){
            try {
                channelFuture.sync();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ExecutorService executorService= Executors.newFixedThreadPool(30);
    @Override
    protected ExecutorService getCallbackExecutor() {
        return executorService;
    }

    private class InvokeProxy implements InvokeInterface{

        @Override
        public TransmittingMessage invokeSync(Channel channel, TransmittingMessage transmittingMessage) throws InterruptedException, InvokeFailException, RemoteRunOuttimeException {
            return doSyncInvoke(channel,transmittingMessage);
        }

        @Override
        public void invokeAsync(Channel channel, TransmittingMessage transmittingMessage, CallbackListener callbackListener) throws InterruptedException, AsyncInvokeTooQuickException {
            doAsyncInvoke(channel, transmittingMessage, callbackListener);
        }

        @Override
        public void invokeOneway(Channel channel, TransmittingMessage transmittingMessage) {
            doOneWayInvoke(channel, transmittingMessage);
        }
    }

    public InvokeInterface getInvokeProxy()
    {
        return new InvokeProxy();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
