package com.alibaba.middleware.race.mom.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;

/**
 * Created by wlw on 15-8-3.
 */
public class ProtostufSerializer extends AbstractMessageSerializer {

    @Override
    protected <T> byte[] writeObject(LinkedBuffer buffer, T object, Schema<T> schema) {
        return ProtobufIOUtil.toByteArray(object, schema, buffer);
    }

    @Override
    protected <T> void parseObject(byte[] bytes, T template, Schema<T> schema) {
        ProtobufIOUtil.mergeFrom(bytes, template, schema);
    }
}
