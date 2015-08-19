package com.alibaba.middleware.race.rpc.api.client;

import com.alibaba.middleware.race.rpc.context.RpcContext;

/**
 * Created by wlw on 15-7-31.
 */
public class RpcClientManager {

    public static RpcClient connect(String ip,int port) throws InterruptedException {
        RpcClient rpcClient=new RpcClient();
        rpcClient.connect(ip,port);
        RpcContext.setClientAction(rpcClient);
        return rpcClient;
    }

}
