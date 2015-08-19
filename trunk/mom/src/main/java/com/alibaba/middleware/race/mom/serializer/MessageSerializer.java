package com.alibaba.middleware.race.mom.serializer;

import com.alibaba.middleware.race.mom.ConsumeResult;
import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.SendResult;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;

import java.io.IOException;

/**
 * Created by wlw on 15-8-3.
 */
public interface MessageSerializer {
    byte[] encodeTransmittingMessage(TransmittingMessage transmittingMessage) throws IOException;
    byte[] encodeConsumeResult(ConsumeResult consumeResult) throws IOException;
    byte[] encodeMessage(Message message) throws IOException;
    byte[] encodeSendResult(SendResult sendResult) throws IOException;
    TransmittingMessage decodeTransmittingMessage(byte[] bytes);
    ConsumeResult decodeConsumeResult(byte[] bytes);
    Message decodeMessage(byte[] bytes);
    SendResult decodeSendResult(byte[] bytes);
}
