package com.alibaba.middleware.race.rpc.api.registry;

import com.alibaba.middleware.race.rpc.api.error.MessageRegistryException;

/**
 * Created by wlw on 15-7-28.
 */
public interface IMessageRegistry {
    public short getMessageID (Class<?> messageType) throws MessageRegistryException;
    public Class<?> getMessageType (short messageID) throws MessageRegistryException;
}
