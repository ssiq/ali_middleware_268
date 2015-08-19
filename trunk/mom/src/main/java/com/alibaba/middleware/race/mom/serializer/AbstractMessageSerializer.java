package com.alibaba.middleware.race.mom.serializer;

import com.alibaba.middleware.race.mom.ConsumeResult;
import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.SendResult;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.util.BufferCache;
import com.alibaba.middleware.race.mom.util.SchemaCache;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;

import java.io.IOException;

/**
 * Created by wlw on 15-8-3.
 */
public abstract class AbstractMessageSerializer implements MessageSerializer {

    protected abstract <T> byte[] writeObject(LinkedBuffer buffer, T object,
                                              Schema<T> schema);

    protected abstract <T> void parseObject(byte[] bytes, T template,
                                            Schema<T> schema);

    @Override
    public byte[] encodeTransmittingMessage(TransmittingMessage transmittingMessage) throws IOException {
        return encode(transmittingMessage, SchemaCache.getTransmittingMessageSchema());
    }

    @Override
    public byte[] encodeConsumeResult(ConsumeResult consumeResult) throws IOException {
        return encode(consumeResult, SchemaCache.getConsumeResultSchema());
    }

    @Override
    public byte[] encodeMessage(Message message) throws IOException {
        return encode(message, SchemaCache.getMessageSchema());
    }

    @Override
    public byte[] encodeSendResult(SendResult sendResult) throws IOException {
        return encode(sendResult, SchemaCache.getSendResultSchema());
    }

    @Override
    public TransmittingMessage decodeTransmittingMessage(byte[] bytes) {
        TransmittingMessage transmittingMessage=new TransmittingMessage();
        decode(transmittingMessage, bytes, SchemaCache.getTransmittingMessageSchema());
        return transmittingMessage;
    }

    @Override
    public ConsumeResult decodeConsumeResult(byte[] bytes) {
        ConsumeResult consumeResult=new ConsumeResult();
        decode(consumeResult, bytes, SchemaCache.getConsumeResultSchema());
        return consumeResult;
    }

    @Override
    public Message decodeMessage(byte[] bytes) {
        Message message=new Message();
        decode(message, bytes, SchemaCache.getMessageSchema());
        return message;
    }

    @Override
    public SendResult decodeSendResult(byte[] bytes) {
        SendResult sendResult=new SendResult();
        decode(sendResult, bytes, SchemaCache.getSendResultSchema());
        return sendResult;
    }

    private <T> byte[] encode(T object,Schema<T> schema) throws IOException {
        LinkedBuffer linkedBuffer= BufferCache.getBuffer();
        Schema inschema=null;
        if (null == object) {
            inschema = SchemaCache.getObjectSchema();
        } else {
            inschema = schema;
        }

        return writeObject(linkedBuffer, object, inschema);
    }

    private <T> void decode(T object, byte[] bytes,Schema<T> schema)
    {
        parseObject(bytes, object, schema);
    }
}
