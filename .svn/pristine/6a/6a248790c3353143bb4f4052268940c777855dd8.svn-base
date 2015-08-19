package com.alibaba.middleware.race.rpc.api.impl;

import com.alibaba.middleware.race.rpc.aop.ConsumerHook;
import com.alibaba.middleware.race.rpc.aop.DefaultConsumerHook;
import com.alibaba.middleware.race.rpc.api.RpcConsumer;
import com.alibaba.middleware.race.rpc.api.client.RpcClient;
import com.alibaba.middleware.race.rpc.api.client.RpcClientManager;
import com.alibaba.middleware.race.rpc.api.util.MethodCache;
import com.alibaba.middleware.race.rpc.api.util.SchemaCache;
import com.alibaba.middleware.race.rpc.async.DefaultCallbackListener;
import com.alibaba.middleware.race.rpc.async.ResponseCallbackListener;
import com.alibaba.middleware.race.rpc.model.RequestIdManager;
import com.alibaba.middleware.race.rpc.model.RpcRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlw on 15-7-26.
 */
public class RpcConsumerImpl extends RpcConsumer{

    static {
        System.setProperty("protostuff.runtime.always_use_sun_reflection_factory", "true");
    }

    private Class<?> interfaceClass;
    private int clientTimeOut;
    private Map<String,ResponseCallbackListener> asynMethodName;
    private String sip;
    private int port;
    private RpcClient rpcClient;
    private ConsumerHook consumerHook=new DefaultConsumerHook();

    static {
        SchemaCache.load();
    }

    public RpcConsumerImpl() {
        asynMethodName=new HashMap<String, ResponseCallbackListener>();
        sip=System.getProperty("SIP");
        port=8888;
    }

    @Override
    public RpcConsumer interfaceClass(Class<?> interfaceClass) {
        this.interfaceClass=interfaceClass;
        MethodCache.getInstance().init(interfaceClass);
        return this;
    }

    @Override
    public RpcConsumer version(String version) {
        return super.version(version);
    }

    @Override
    public RpcConsumer clientTimeout(int clientTimeout) {
        this.clientTimeOut=clientTimeout;
        return this;
    }

    @Override
    public RpcConsumer hook(ConsumerHook hook) {
        consumerHook=hook;
        return this;
    }

    @Override
    public Object instance() {
        try {
            rpcClient= RpcClientManager.connect(sip,port);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},this);
    }

    @Override
    public void asynCall(String methodName) {
        asynCall(methodName, new DefaultCallbackListener());
    }

    @Override
    public <T extends ResponseCallbackListener> void asynCall(String methodName, T callbackListener) {
        asynMethodName.put(methodName, callbackListener);
    }

    @Override
    public void cancelAsyn(String methodName) {
        asynMethodName.remove(methodName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest=new RpcRequest(method,args);
        consumerHook.before(rpcRequest);
        rpcRequest.setRequestId(RequestIdManager.getMyRequestId().getNextRequest());
        Object result=null;
        if(asynMethodName.containsKey(method.getName()))
        {
            rpcClient.doAsyncMethod(rpcRequest,clientTimeOut,asynMethodName.get(method.getName()));
        }else{
            result=rpcClient.doSyncMethod(rpcRequest,clientTimeOut);
        }
        consumerHook.after(rpcRequest);
        return result;
    }
}
