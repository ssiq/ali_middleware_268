package com.alibaba.middleware.race.mom.util;

import com.alibaba.middleware.race.mom.Message;

/**
 * Created by wlw on 15-8-6.
 */
public class PrintMessage {
    public static void print(Message message)
    {
        System.out.println("message{ topic:"+message.getTopic()+",msgId:"+message.getMsgId());
    }
}
