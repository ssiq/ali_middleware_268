package com.alibaba.middleware.race.mom.handler;

import com.alibaba.middleware.race.mom.serializer.MessageSerializer;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-3.
 */
public class TransmittingMessageEncoder extends MessageToByteEncoder {

    public TransmittingMessageEncoder() {

    }

//    private static AtomicInteger idi=new AtomicInteger(0);
//    private AtomicInteger nowuse=new AtomicInteger(0);
//    private int id=idi.incrementAndGet();
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
//        System.out.println(" "+id+" now use:"+nowuse.incrementAndGet());
        TransmittingMessage transmittingMessage=(TransmittingMessage)msg;
        transmittingMessage.encode(out);
        ByteBufOutputStream byteBufOutputStream=new ByteBufOutputStream(out);
//        nowuse.decrementAndGet();
//        System.out.println("encode"+transmittingMessage.toString());
    }
}
