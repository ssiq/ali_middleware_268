package com.alibaba.middleware.race.mom.model;

/**
 * Created by wlw on 15-8-10.
 */
public interface NotSendMessagePair {
    String getGroupId();
    String getMsgId();
    void setSend();
    boolean isSend();
}
