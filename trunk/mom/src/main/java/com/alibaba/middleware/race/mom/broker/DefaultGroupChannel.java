package com.alibaba.middleware.race.mom.broker;

import io.netty.channel.Channel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by wlw on 15-8-5.
 */
public class DefaultGroupChannel implements GroupChannel{

    private String groupId;
    private Queue<Channel> channels=new ConcurrentLinkedQueue();

    public DefaultGroupChannel(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public Channel getNowChannel() {
        Channel channel;
        while (true) {
            channel=channels.poll();
            if(channel!=null&&channel.isActive())
            {
                channels.add(channel);
                break;
            }
        }
        return channel;
    }

    @Override
    public void addChannel(Channel channel)
    {
        channels.add(channel);
    }

    @Override
    public boolean isEnpty()
    {
        return channels.isEmpty();
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

}
