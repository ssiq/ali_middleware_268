package com.alibaba.middleware.race.mom.service;

import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wlw on 15-8-4.
 */
public interface RequestProcesser {
    void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage);
}
