package com.alibaba.middleware.race.rpc.api.util;

import io.protostuff.LinkedBuffer;

/**
 * Created by wlw on 15-7-29.
 */
public class BufferCache {

    private static ThreadLocal<LinkedBuffer> BUFFERS = new ThreadLocal<LinkedBuffer>() {
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(4096);
        };
    };

    public static LinkedBuffer getBuffer() {
        LinkedBuffer buffer = BUFFERS.get();
        buffer.clear();
        return buffer;
    }
}