package com.alibaba.middleware.race.rpc.api.handler;

import com.alibaba.middleware.race.rpc.api.handler.serializer.ServerSerializer;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wlw on 15-7-28.
 */
public class RpcResponseEncoder extends MessageToByteEncoder {

    private ServerSerializer serverSerializer;

    public RpcResponseEncoder(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
//        System.out.println("begin encode response");
        RpcResponse rpcResponse=(RpcResponse)msg;
        byte[] bytes=serverSerializer.encodeRpcResponse(rpcResponse);
        out.writeBytes(bytes);
//        System.out.println("encode the response");
    }
}
