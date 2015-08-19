package com.alibaba.middleware.race.rpc.api.handler.serializer;

import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;

import java.io.IOException;

/**
 * Created by wlw on 15-7-29.
 */
public interface ClientSerializer {
    public byte[] encodeRequest(RpcRequest rpcRequest) throws IOException;
    public RpcResponse decodeResponse(byte[] bytes) throws IOException;
}
