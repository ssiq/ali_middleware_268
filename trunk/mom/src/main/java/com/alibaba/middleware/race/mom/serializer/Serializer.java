package com.alibaba.middleware.race.mom.serializer;

import com.alibaba.middleware.race.mom.ConsumeResult;
import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.SendResult;
import com.alibaba.middleware.race.mom.consumer.SubscriptMessage;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.util.BufferCache;
import io.netty.buffer.ByteBuf;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Created by wlw on 15-8-4.
 */
public class Serializer {

    private Schema schema;

    public Serializer(Schema schema) {
        this.schema = schema;
    }

    public byte[] encode(Object o)
    {
        return ProtobufIOUtil.toByteArray(o,schema, BufferCache.getBuffer());
    }

    public <T> void decode(byte[] bytes,T object)
    {
        ProtobufIOUtil.mergeFrom(bytes,object,schema);
    }

    public <T> T decode(byte[] bytes)
    {
        T object=(T)schema.newMessage();
        decode(bytes,object);
        return object;
    }

    private static Serializer transmittingMessaageSerialier=
            new Serializer(RuntimeSchema.getSchema(TransmittingMessage.class));

    public static Serializer getTransmittingMessaageSerialier()
    {
        return transmittingMessaageSerialier;
    }

    private static Serializer subscriptSerialier=
            new Serializer(RuntimeSchema.getSchema(SubscriptMessage.class));

    public static Serializer getSubscriptSerialier() {
        return subscriptSerialier;
    }

    private static Serializer messageSerialier=
            new Serializer(RuntimeSchema.getSchema(Message.class));

    public static Serializer getMessageSerialier() {
        return messageSerialier;
    }

    private static Serializer consumnerResultSerialier=
            new Serializer(RuntimeSchema.getSchema(ConsumeResult.class));

    public static Serializer getConsumnerResultSerialier() {
        return consumnerResultSerialier;
    }

    private static Serializer sendResultSerialier=
            new Serializer(RuntimeSchema.getSchema(SendResult.class));

    public static Serializer getSendResultSerialier() {
        return sendResultSerialier;
    }
}
