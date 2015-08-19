package com.alibaba.middleware.race.rpc.api.handler.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;

/**
 * Created by wlw on 15-7-29.
 */
public class ProtoBufSerialier extends AbstractSerialierAdapter {
    @Override
    protected <T> byte[] writeObject(LinkedBuffer buffer, T object, Schema<T> schema) {
        return ProtobufIOUtil.toByteArray(object, schema, buffer);
    }

    @Override
    protected <T> void parseObject(byte[] bytes, T template, Schema<T> schema) {
        ProtobufIOUtil.mergeFrom(bytes, template, schema);
    }
}
