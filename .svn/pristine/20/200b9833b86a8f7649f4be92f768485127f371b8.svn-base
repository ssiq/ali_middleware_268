package com.alibaba.middleware.race.mom.util;

import com.alibaba.middleware.race.mom.ConsumeResult;
import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.SendResult;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Created by wlw on 15-8-2.
 */
public class SchemaCache {

    private static Schema<TransmittingMessage> transmittingMessageSchema;
    private static Schema<ConsumeResult> consumeResultSchema;
    private static Schema<Message> messageSchema;
    private static Schema<SendResult> sendResultSchema;
    private static Schema<Object> objectSchema;

    static {
        transmittingMessageSchema= RuntimeSchema.getSchema(TransmittingMessage.class);
        consumeResultSchema=RuntimeSchema.getSchema(ConsumeResult.class);
        messageSchema=RuntimeSchema.getSchema(Message.class);
        sendResultSchema=RuntimeSchema.getSchema(SendResult.class);
        objectSchema=RuntimeSchema.getSchema(Object.class);
    }

    public static void load(){}

    public static Schema<TransmittingMessage> getTransmittingMessageSchema() {
        return transmittingMessageSchema;
    }

    public static Schema<ConsumeResult> getConsumeResultSchema() {
        return consumeResultSchema;
    }

    public static Schema<Message> getMessageSchema() {
        return messageSchema;
    }

    public static Schema<SendResult> getSendResultSchema() {
        return sendResultSchema;
    }

    public static Schema<Object> getObjectSchema() {
        return objectSchema;
    }

    public static Schema getSchema(Class<?> clazz)
    {
        return RuntimeSchema.getSchema(clazz);
    }

}
