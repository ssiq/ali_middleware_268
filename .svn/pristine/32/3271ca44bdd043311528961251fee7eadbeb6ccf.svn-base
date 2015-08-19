package com.alibaba.middleware.race.rpc.api.impl;

import com.alibaba.middleware.race.rpc.api.RpcProvider;
import com.alibaba.middleware.race.rpc.api.server.RpcServer;
import com.alibaba.middleware.race.rpc.api.util.MethodCache;
import com.alibaba.middleware.race.rpc.api.util.SchemaCache;

/**
 * Created by wlw on 15-7-26.
 */
public class RpcProviderImpl extends RpcProvider {
    private Class<?> serviceInterface;
    private Object serviceInstance;

    public RpcProviderImpl() {
        super();
    }

    static {
        SchemaCache.load();
    }

    @Override
    public RpcProvider serviceInterface(Class<?> serviceInterface) {
        this.serviceInterface=serviceInterface;
        MethodCache.getInstance().init(serviceInterface);
        return this;
    }

    @Override
    public RpcProvider version(String version) {
        return super.version(version);
    }

    @Override
    public RpcProvider impl(Object serviceInstance) {
        this.serviceInstance=serviceInstance;
        return this;
    }

    @Override
    public RpcProvider timeout(int timeout) {
        return super.timeout(timeout);
    }

    @Override
    public RpcProvider serializeType(String serializeType) {
        return super.serializeType(serializeType);
    }

    @Override
    public void publish() {
        System.setProperty("protostuff.runtime.always_use_sun_reflection_factory", "true");
        int port=8888;
        RpcServer rpcServer=new RpcServer();
        try {
            rpcServer.serviceInterface(serviceInterface)
                    .impl(serviceInstance)
                    .start(port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
