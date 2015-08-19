package com.alibaba.middleware.race.rpc.api.handler;

import com.alibaba.middleware.race.rpc.api.handler.serializer.ClientSerializer;
import com.alibaba.middleware.race.rpc.model.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wlw on 15-7-28.
 */
public class RpcRequestEncoder extends MessageToByteEncoder {

    private ClientSerializer clientSerializer;

    public RpcRequestEncoder(ClientSerializer clientSerializer)
    {
        this.clientSerializer=clientSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
//        System.out.println("begin encode request");
        RpcRequest rpcRequest=(RpcRequest)msg;
        byte[] bytes=clientSerializer.encodeRequest(rpcRequest);
        out.writeBytes(bytes);
//        System.out.println("encode the request");
    }
}
