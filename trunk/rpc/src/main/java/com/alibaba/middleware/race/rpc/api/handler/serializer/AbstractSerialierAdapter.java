package com.alibaba.middleware.race.rpc.api.handler.serializer;

import com.alibaba.middleware.race.rpc.api.util.BufferCache;
import com.alibaba.middleware.race.rpc.api.util.SchemaCache;
import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;

import java.io.IOException;

/**
 * Created by wlw on 15-7-29.
 */
public abstract class AbstractSerialierAdapter implements ClientSerializer, ServerSerializer {

    protected abstract <T> byte[] writeObject(LinkedBuffer buffer, T object,
                                              Schema<T> schema);

    protected abstract <T> void parseObject(byte[] bytes, T template,
                                            Schema<T> schema);


    public byte[] encodeRequest(RpcRequest rpcRequest) throws IOException {
        return encode(rpcRequest);
    }

    public RpcResponse decodeResponse(byte[] bytes) throws IOException {
        RpcResponse rpcResponse=new RpcResponse();
        decode(rpcResponse, bytes);
        return rpcResponse;
    }

    public byte[] encodeRpcResponse(RpcResponse rpcResponse) throws IOException {
        return encode(rpcResponse);
    }

    public RpcRequest decodeRpcRequest(byte[] bytes) throws IOException {
        RpcRequest rpcRequest=new RpcRequest();
        decode(rpcRequest, bytes);
        return rpcRequest;
    }

    private <T> byte[] encode(T object) throws IOException {
        LinkedBuffer linkedBuffer=BufferCache.getBuffer();
        Schema schema = null;
        if (null == object) {
            schema = SchemaCache.getSchema(Object.class);
        } else {
            schema = SchemaCache.getSchema(object.getClass());
        }

        return writeObject(linkedBuffer, object, schema);
    }

    private <T> void decode(T object, byte[] bytes)
    {
        Schema schema=SchemaCache.getSchema(object.getClass());
        parseObject(bytes, object, schema);
    }
}
