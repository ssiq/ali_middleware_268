package com.alibaba.middleware.race.mom.handler;

import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.SocketAddress;

/**
 * Created by wlw on 15-8-7.
 */
public class ClientHeartbeatHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent)
        {
            IdleStateEvent e=(IdleStateEvent)evt;
            if(e.state()== IdleState.READER_IDLE)
            {
                //do something to reconnect
                Channel channel=ctx.channel();
                System.out.println("find one server lose");
                System.out.println("channel is active:"+channel.isActive());
                System.out.println("channel is open:"+channel.isOpen());
                System.out.println("remote address is:"+channel.remoteAddress());
            }else if(e.state()==IdleState.WRITER_IDLE){
                System.out.println("do heart beat action");
                ctx.writeAndFlush(TransmittingMessage.wrapRequestMessage(MessageConst.getHEARTBEAT(),null));
            }
        }
    }
}
