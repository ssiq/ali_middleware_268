package com.alibaba.middleware.race.mom.broker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wlw on 15-8-6.
 */
public class MessageId {

    private AtomicInteger atomicInteger=new AtomicInteger(0);
    private String brokername;

    private MessageId(String brokername) {
        this.brokername = brokername;
    }

    public MessageId() {
    }

    public String getMessageId()
    {
        return brokername+atomicInteger.getAndIncrement();
    }

    public void setBrokername(String brokername) {
        this.brokername = brokername;
    }
}
