package com.alibaba.middleware.race.rpc.api.handler;

import com.alibaba.middleware.race.rpc.api.handler.serializer.ServerSerializer;
import com.alibaba.middleware.race.rpc.model.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by wlw on 15-7-28.
 */
public class RpcRequestDecoder extends MessageToMessageDecoder<ByteBuf> {

    private ServerSerializer serverSerializer;

    public RpcRequestDecoder(ServerSerializer serverSerializer)
    {
        this.serverSerializer=serverSerializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
//        System.out.println("begin decode request");
        int length=msg.readableBytes();
        byte[] bytes=new byte[length];
        msg.getBytes(msg.readerIndex(), bytes, 0, length);
        RpcRequest rpcRequest=serverSerializer.decodeRpcRequest(bytes);
//        System.out.println("decoded the request");
        out.add(rpcRequest);
    }
}
