package com.alibaba.middleware.race.mom.model;

import com.alibaba.middleware.race.mom.error.AsyncInvokeTooQuickException;
import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import io.netty.channel.Channel;

/**
 * Created by wlw on 15-8-5.
 */
public interface InvokeInterface {
    TransmittingMessage invokeSync(Channel channel, final TransmittingMessage transmittingMessage)
            throws InterruptedException, InvokeFailException, RemoteRunOuttimeException;
    void invokeAsync(Channel channel, final TransmittingMessage transmittingMessage,CallbackListener callbackListener)
            throws InterruptedException, AsyncInvokeTooQuickException;
    void invokeOneway(Channel channel,TransmittingMessage transmittingMessage);
}
