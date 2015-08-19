package com.alibaba.middleware.race.mom.model;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wlw on 15-8-11.
 */
public interface IndexMessage {
//    void setUse();
    boolean isUsed();
    byte[] getMessage();
//    long getBornTime();
//    boolean isRegistered();
//    void setRegistered();
//    String getMsgId();
    void up();
    void down();
    void downOnce(AtomicBoolean once);
    void endUp();
//    String getPutQueueName();
    int getIndex();
}
