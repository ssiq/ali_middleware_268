package com.alibaba.middleware.race.mom.service;

import com.alibaba.middleware.race.mom.consumer.ReceiveRequestService;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wlw on 15-8-18.
 */
public abstract class AbstractRequestService implements RequestProcesser {

    protected void doResponse(ChannelHandlerContext ctx,
                            TransmittingMessage request,
                            TransmittingMessage response)
    {
        if(!request.isOneway())
        {
            response.setOpaque(request.getOpaque());
            ctx.writeAndFlush(response);
        }
    }
}
