package com.alibaba.middleware.race.mom.store.queue;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by wlw on 15-8-9.
 */
public interface MessageQueue{
    int add(byte[] bytes, int flag);
    int add(byte[] bytes);
    void pop();
    void force();
    boolean isFull();
    boolean isEmpty();
    int getForceLevel();
    byte[] get(int index);
    int leftSpace();
    String getName();
    void close();
    void start() throws IOException;
    void waitStarted();
    ByteBuffer allocSpace(int size);
    void remove();
    void remove(int index);
}
