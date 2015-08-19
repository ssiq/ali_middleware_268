package com.alibaba.middleware.race.rpc.context;

import com.alibaba.middleware.race.rpc.api.client.RpcClient;
import com.alibaba.middleware.race.rpc.async.DefaultCallbackListener;
import com.alibaba.middleware.race.rpc.model.RequestIdManager;
import com.alibaba.middleware.race.rpc.model.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by wlw on 15-7-30.
 */
public class ContextActionProxy implements InvocationHandler {

    private ContextActionProxy(){}
    private static ContextActionProxy contextActionProxy=new ContextActionProxy();

    public static RpcContextAction getInstance(RpcClient rpcClient)
    {
        if(contextActionProxy.rpcClient==null)
        {
            contextActionProxy.rpcClient=rpcClient;
        }
        return (RpcContextAction)Proxy.newProxyInstance(RpcContextAction.class.getClassLoader(),
                new Class[]{RpcContextAction.class},
                contextActionProxy);
    }

    private RpcClient rpcClient;

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        RpcRequest rpcRequest=new RpcRequest(method,objects,true);
        rpcRequest.setRequestId(RequestIdManager.getMyRequestId().getNextRequest());
//        if(method.getReturnType()==void.class)
//        {
//            System.out.println("do async context call");
//            rpcClient.doAsyncMethod(rpcRequest, 3000,new DefaultCallbackListener());
//            return null;
//        }else{
//            return rpcClient.doSyncMethod(rpcRequest, 3000);
//        }
        return rpcClient.doSyncMethod(rpcRequest, 3000);
    }
}