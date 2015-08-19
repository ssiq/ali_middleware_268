package com.alibaba.middleware.race.mom.broker;

import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.service.RequestProcesser;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wlw on 15-8-7.
 */
public class CloseRequestService implements RequestProcesser {
    @Override
    public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
        ctx.channel().close();
    }
}
