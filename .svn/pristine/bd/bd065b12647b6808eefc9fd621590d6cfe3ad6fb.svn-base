package com.alibaba.middleware.race.rpc.api.registry;

import com.alibaba.middleware.race.rpc.api.error.MessageRegistryException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.lang.reflect.Method;

/**
 * Created by wlw on 15-7-28.
 */
public class RpcMessageRegistry implements IMessageRegistry{

    private BiMap<Short, Class<?>> bimap;
    private short maxId;

    public RpcMessageRegistry() {
        bimap= HashBiMap.create();
        maxId=0;
    }

    public short getMessageID(Class<?> messageType) throws MessageRegistryException {
        return 0;
    }

    public Class<?> getMessageType(short messageID) throws MessageRegistryException {
        return null;
    }

    public void touchInterface(Class<?> iInterface)
    {
        Method[] methods=iInterface.getMethods();
        for(int i=0;i<methods.length;++i)
        {
            Method method=methods[i];
        }
    }

    public void touchClass(Class<?> iClass)
    {

    }
}
