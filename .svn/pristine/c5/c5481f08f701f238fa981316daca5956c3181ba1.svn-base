package race.momtest.stake;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.SendResult;
import com.alibaba.middleware.race.mom.SendStatus;
import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.service.RequestProcesser;
import com.alibaba.middleware.race.mom.service.Server;
import com.alibaba.middleware.race.mom.util.PrintMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executors;

/**
 * Created by wlw on 15-8-6.
 */
public class ProducerTetsBroker {
    public void start()
    {
        final Server server=new Server(300,3000);
        server.addResponseService(MessageConst.getPOST(), new RequestProcesser() {
            @Override
            public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
                Serializer serializer=Serializer.getMessageSerialier();
                Message message=new Message();
                serializer.decode(transmittingMessage.getBody(), message);
                System.out.println("accept transmitting Message:" + transmittingMessage);
                PrintMessage.print(message);
                SendResult sendResult=new SendResult();
                sendResult.setStatus(SendStatus.SUCCESS);
                sendResult.setMsgId("1");
                serializer=Serializer.getSendResultSerialier();
                TransmittingMessage response=TransmittingMessage.wrapResponseMessage(
                        MessageConst.getPOSTRESULT(),
                        serializer.encode(sendResult));
                System.out.println("response send result:"+sendResult.toString());
//                return response;
            }
        }, Executors.newFixedThreadPool(30));
        server.bind(9999);
    }
}
