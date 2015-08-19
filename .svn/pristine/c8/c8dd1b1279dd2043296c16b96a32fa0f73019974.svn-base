package com.alibaba.middleware.race.rpc.api.handler;

import com.alibaba.middleware.race.rpc.api.handler.serializer.ClientSerializer;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by wlw on 15-7-28.
 */
public class RpcResponseDecoder extends MessageToMessageDecoder<ByteBuf> {

    private ClientSerializer clientSerializer;

    public RpcResponseDecoder(ClientSerializer clientSerializer) {
        this.clientSerializer = clientSerializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
//        System.out.println("begin decode response");
        int length=msg.readableBytes();
        byte[] bytes=new byte[length];
        msg.getBytes(msg.readerIndex(), bytes, 0, length);
        RpcResponse rpcResponse=clientSerializer.decodeResponse(bytes);
        out.add(rpcResponse);
//        System.out.println("decode the response");
    }
}
