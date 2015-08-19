package com.alibaba.middleware.race.rpc.api.factory;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by wlw on 15-7-29.
 */
public class ChannelInitializerFactory {

    public static ChannelInitializer<SocketChannel> getChannelInitializer(
            final ChannelInboundHandlerAdapter decodeHandler,
            final ChannelOutboundHandlerAdapter encodeHandler,
            final ChannelInboundHandlerAdapter actionHandler)
    {
        return new ChannelInitializer<SocketChannel>(){

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("frameDecoder",
                        new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                ch.pipeline().addLast("decodeHandler", decodeHandler);
                ch.pipeline().addLast("frameEncoder",
                        new LengthFieldPrepender(2));
                ch.pipeline().addLast("encodeHandler", encodeHandler);
                ch.pipeline().addLast("actionHandler", actionHandler);
            }
        };
    }

}
