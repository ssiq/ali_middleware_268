package com.alibaba.middleware.race.mom.broker;

import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.service.AbstractRequestService;
import com.alibaba.middleware.race.mom.service.RequestProcesser;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wlw on 15-8-7.
 */
public class HeartbeatService extends AbstractRequestService {
    @Override
    public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
        doResponse(ctx, transmittingMessage, TransmittingMessage.wrapResponseMessage(MessageConst.getHEARTBEAT(), null));
    }
}
