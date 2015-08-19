package race.momtest.stake;

import com.alibaba.middleware.race.mom.broker.CloseRequestService;
import com.alibaba.middleware.race.mom.broker.HeartbeatService;
import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.service.RequestProcesser;
import com.alibaba.middleware.race.mom.service.Server;
import io.netty.channel.ChannelHandlerContext;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.Executors;

/**
 * Created by wlw on 15-8-18.
 */
public class NetDelayServer {
    public static void main(String[]args)
    {
        Server server=new Server(300,10000);
        final Schema schema= RuntimeSchema.getSchema(DelayMessaage.class);
        final LinkedBuffer linkedBuffer=LinkedBuffer.allocate(1024);
        server.addResponseService(MessageConst.getTEST(), new RequestProcesser() {
            @Override
            public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
                linkedBuffer.clear();
                DelayMessaage delayMessaage=new DelayMessaage();
                ProtobufIOUtil.mergeFrom(transmittingMessage.getBody(), delayMessaage, schema);
                byte[] bytes=ProtobufIOUtil.toByteArray(delayMessaage,schema,linkedBuffer);
//                return TransmittingMessage.wrapResponseMessage(MessageConst.getTEST(),bytes);
            }
        }, Executors.newFixedThreadPool(200));
        server.addResponseService(MessageConst.getCLOSE(),new CloseRequestService(),Executors.newFixedThreadPool(10));
        server.addResponseService(MessageConst.getHEARTBEAT(),new HeartbeatService(),Executors.newFixedThreadPool(10));
        server.bind(9999);
    }
}
