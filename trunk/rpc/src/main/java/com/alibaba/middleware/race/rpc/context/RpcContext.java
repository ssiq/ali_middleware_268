package com.alibaba.middleware.race.rpc.context;

import com.alibaba.middleware.race.rpc.api.client.RpcClient;

import java.util.Map;

/**
 * Created by huangsheng.hs on 2015/4/8.
 */
public class RpcContext {
    //TODO how can I get props as a provider? tip:ThreadLocal
    private static RpcContextAction contextAction=ServerContextAction.getInstance();

    public static void setClientAction(RpcClient rpcClient)
    {
        contextAction= ContextActionProxy.getInstance(rpcClient);
    }

    public static void addProp(String key ,Object value){
        contextAction.addProp(key, value);
    }

    public static Object getProp(String key){
        return contextAction.getProp(key);
    }

    public static Map<String,Object> getProps(){
        return contextAction.getProps();
    }
}