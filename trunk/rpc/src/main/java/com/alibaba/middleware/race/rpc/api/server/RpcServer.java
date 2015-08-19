package com.alibaba.middleware.race.rpc.api.server;

import com.alibaba.middleware.race.rpc.api.factory.ChannelInitializerFactory;
import com.alibaba.middleware.race.rpc.api.handler.*;
import com.alibaba.middleware.race.rpc.api.handler.serializer.ProtoBufSerialier;
import com.alibaba.middleware.race.rpc.api.handler.serializer.ServerSerializer;
import com.alibaba.middleware.race.rpc.api.util.MethodCache;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by wlw on 15-7-27.
 */
public class RpcServer {
    private Class<?> serviceInterface;
    private Object serviceInstance;

    public RpcServer(){}

    public RpcServer serviceInterface(Class<?> serviceInterface){
        this.serviceInterface=serviceInterface;
        return this;
    }

    public RpcServer impl(Object serviceInstance) {
        this.serviceInstance=serviceInstance;
        return this;
    }

    public void start(int port) throws InterruptedException {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            final ServerSerializer serverSerializer=new ProtoBufSerialier();
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>(){

                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast("frameDecoder",
                                            new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                                    ch.pipeline().addLast("decodeHandler",
                                            new RpcRequestDecoder(serverSerializer));
                                    ch.pipeline().addLast("frameEncoder",
                                            new LengthFieldPrepender(2));
                                    ch.pipeline().addLast("encodeHandler",
                                            new RpcResponseEncoder(serverSerializer));
                                    ch.pipeline().addLast("actionHandler",
                                            new RpcServerHandler(serviceInterface,serviceInstance));
                                }
                            }
                    );

            ChannelFuture f=b.bind(port).sync();

            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
