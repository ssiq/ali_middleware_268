package race.momtest.stake;

import com.alibaba.middleware.race.mom.ConsumeResult;
import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.consumer.SubscriptMessage;
import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
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
public class ConsumerTestBroker {
    public void start()
    {
        final Server server=new Server(300,3000);
        server.addResponseService(MessageConst.getSBSCRIPT(), new RequestProcesser() {
            @Override
            public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
                Serializer serializer=Serializer.getSubscriptSerialier();
                SubscriptMessage subscriptMessage=new SubscriptMessage();
                serializer.decode(transmittingMessage.getBody(), subscriptMessage);
                System.out.println("accept transmitting Message:" + transmittingMessage);
                System.out.println("the subscriptMessage is" + subscriptMessage.toString());
                serializer=Serializer.getMessageSerialier();
                Message message=new Message();
                message.setMsgId("1");
                message.setBornTime(System.currentTimeMillis());
                message.setProperty("area", "us");
                message.setTopic("T-test");
                PrintMessage.print(message);
                try {
                    TransmittingMessage transmittingMessage1=server.doSyncInvoke(ctx.channel(), TransmittingMessage.wrapRequestMessage(MessageConst.getBROAD(), serializer.encode(message)));
                    serializer=Serializer.getConsumnerResultSerialier();
                    ConsumeResult consumeResult=new ConsumeResult();
                    serializer.decode(transmittingMessage1.getBody(),consumeResult);
                    System.out.println("consumer returned ok:"+consumeResult.getStatus());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvokeFailException e) {
                    e.printStackTrace();
                } catch (RemoteRunOuttimeException e) {
                    e.printStackTrace();
                }
//                return null;
            }
        }, Executors.newFixedThreadPool(30));
        server.bind(9999);
    }
}
