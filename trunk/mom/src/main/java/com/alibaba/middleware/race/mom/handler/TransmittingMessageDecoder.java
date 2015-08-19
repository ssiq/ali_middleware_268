package com.alibaba.middleware.race.mom.handler;

import com.alibaba.middleware.race.mom.serializer.MessageSerializer;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by wlw on 15-8-3.
 */
public class TransmittingMessageDecoder extends ByteToMessageDecoder {

    public TransmittingMessageDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuffer byteBuffer=in.nioBuffer();
        if(byteBuffer.limit()<4)return;
        TransmittingMessage transmittingMessage=TransmittingMessage.decode(byteBuffer);
        in.readerIndex(in.readerIndex()+byteBuffer.limit());
//        System.out.println("decode:"+transmittingMessage.toString());
        out.add(transmittingMessage);
    }
}
