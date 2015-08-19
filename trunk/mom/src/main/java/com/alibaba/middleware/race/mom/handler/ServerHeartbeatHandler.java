package com.alibaba.middleware.race.mom.handler;

import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.SocketAddress;

/**
 * Created by wlw on 15-8-7.
 */
public class ServerHeartbeatHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent e=(IdleStateEvent)evt;
        if(e.state()== IdleState.READER_IDLE)
        {
            System.out.println("close one channel");
            ctx.channel().close();
        }
    }

//    @Override
//    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) throws Exception {
//        super.connect(ctx, remoteAddress, localAddress, future);
//    }
//
//    @Override
//    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
//        super.disconnect(ctx, future);
//    }
//
//    @Override
//    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
//        super.close(ctx, future);
//    }
}
