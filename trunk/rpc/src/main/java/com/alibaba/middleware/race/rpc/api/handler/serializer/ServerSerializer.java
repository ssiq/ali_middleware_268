package com.alibaba.middleware.race.rpc.api.handler.serializer;

import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;

import java.io.IOException;

/**
 * Created by wlw on 15-7-29.
 */
public interface ServerSerializer {
    public byte[] encodeRpcResponse(RpcResponse rpcResponse) throws IOException;
    public RpcRequest decodeRpcRequest(byte[] bytes) throws IOException;
}
