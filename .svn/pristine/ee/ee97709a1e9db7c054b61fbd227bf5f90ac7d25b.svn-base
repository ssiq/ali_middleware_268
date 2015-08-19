package com.alibaba.middleware.race.mom.broker;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.model.IndexMessage;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Created by wlw on 15-8-18.
 */
public class PostMessage {
    private Message message;
    private byte[] messageByte;
    private ChannelHandlerContext ctx;
    private TransmittingMessage requeset;
    private TransmittingMessage broadRequest;
    private TransmittingMessage postResponse;
    private IndexMessage indexMessage;
    private List<GroupChannel> list;

    public PostMessage(Message message, byte[] messageByte, ChannelHandlerContext ctx, TransmittingMessage requeset, IndexMessage indexMessage) {
        this.message = message;
        this.messageByte = messageByte;
        this.ctx = ctx;
        this.requeset = requeset;
        this.indexMessage = indexMessage;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public byte[] getMessageByte() {
        return messageByte;
    }

    public void setMessageByte(byte[] messageByte) {
        this.messageByte = messageByte;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public TransmittingMessage getRequeset() {
        return requeset;
    }

    public void setRequeset(TransmittingMessage requeset) {
        this.requeset = requeset;
    }

    public IndexMessage getIndexMessage() {
        return indexMessage;
    }

    public void setIndexMessage(IndexMessage indexMessage) {
        this.indexMessage = indexMessage;
    }

    public List<GroupChannel> getList() {
        return list;
    }

    public void setList(List<GroupChannel> list) {
        this.list = list;
    }

    public TransmittingMessage getBroadRequest() {
        return broadRequest;
    }

    public void setBroadRequest(TransmittingMessage broadRequest) {
        this.broadRequest = broadRequest;
    }

    public TransmittingMessage getPostResponse() {
        return postResponse;
    }

    public void setPostResponse(TransmittingMessage postResponse) {
        this.postResponse = postResponse;
    }
}
